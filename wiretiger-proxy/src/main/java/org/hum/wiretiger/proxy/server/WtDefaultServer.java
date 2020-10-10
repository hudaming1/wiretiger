package org.hum.wiretiger.proxy.server;

import java.util.List;

import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.common.util.NamedThreadFactory;
import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.facade.event.EventListener;
import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.pipe.FullRequestDecoder;
import org.hum.wiretiger.proxy.pipe.ProxyHandshakeHandler;
import org.hum.wiretiger.proxy.pipe.chain.FullPipeContextManagerHandler;
import org.hum.wiretiger.proxy.pipe.chain.FullPipeEventHandler;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.proxy.session.SessionManagerHandler;
import org.hum.wiretiger.proxy.util.NettyUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WtDefaultServer implements WtServer {
	
	private WiretigerCoreConfig config;
	
	private List<EventListener> listeners;
	
	private MockHandler mockHandler;

	public WtDefaultServer(WiretigerCoreConfig config) {
		this.config = config;
	}

	@Override
	public ChannelFuture start() {
		EventHandler eventHandler = new EventHandler();
		
		// regist event
		if (listeners != null && !listeners.isEmpty()) {
			eventHandler.addAll(listeners);
		}
		
		// Configure the server.
		EventLoopGroup bossGroup = NettyUtils.initEventLoopGroup(1, new NamedThreadFactory("wt-boss-thread"));
		EventLoopGroup masterThreadPool = NettyUtils.initEventLoopGroup(config.getThreads(), new NamedThreadFactory("wt-worker-thread"));
		try {
			ServerBootstrap bootStrap = new ServerBootstrap();
			bootStrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootStrap.group(bossGroup, masterThreadPool).channel(NioServerSocketChannel.class);
			if (config.isDebug()) {
				bootStrap.handler(new LoggingHandler(LogLevel.DEBUG));
			}
			// singleton
			FullPipeEventHandler pipeEventHandler = new FullPipeEventHandler(null, eventHandler);
			bootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					// SessionManagerHandler和FullPipeContextManagerHandler需要保证每一个连接独享
					FullPipeContextManagerHandler fullPipeContextManagerHandler = new FullPipeContextManagerHandler(new SessionManagerHandler(pipeEventHandler));
					ProxyHandshakeHandler httpProxyHandshakeHandler = new ProxyHandshakeHandler(fullPipeContextManagerHandler, mockHandler);
					ch.pipeline().addLast(new HttpResponseEncoder(), new HttpRequestDecoder() , new FullRequestDecoder(), httpProxyHandshakeHandler);
				}
			});

			return bootStrap.bind(config.getPort());
		} catch (Exception e) {
			log.error("start occur error, config=" + config, e);
			throw new WiretigerException("WtDefaultServer start failed.", e);
		}
	}
	
	@Override
	public void onClose(Object hook) {
//		bossGroup.shutdownGracefully();
//		masterThreadPool.shutdownGracefully();
	}

	public void setListeners(List<EventListener> listeners) {
		this.listeners = listeners;
	}

	public void setMockHandler(MockHandler mockHandler) {
		this.mockHandler = mockHandler;
	}
	
	public int getListeningPort() {
		return config.getPort();
	}
}
