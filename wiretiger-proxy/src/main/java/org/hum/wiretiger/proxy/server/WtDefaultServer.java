package org.hum.wiretiger.proxy.server;

import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.common.util.NamedThreadFactory;
import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.facade.InvokeChainInit;
import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.pipe.chain.ContextManagerInvokeChain;
import org.hum.wiretiger.proxy.pipe.core.FullRequestDecoder;
import org.hum.wiretiger.proxy.pipe.core.ProxyHandshakeHandler;
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
	
	private MockHandler mockHandler;
	
	private InvokeChainInit invokeChainInit;

	public WtDefaultServer(WiretigerCoreConfig config) {
		this.config = config;
	}

	@Override
	public ChannelFuture start() {
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
			bootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					// ContextManagerInvokeChain需要保证每一个连接独享
					ContextManagerInvokeChain cmInvokeChain = new ContextManagerInvokeChain(invokeChainInit.init());
					ProxyHandshakeHandler httpProxyHandshakeHandler = new ProxyHandshakeHandler(cmInvokeChain, mockHandler);
					ch.pipeline().addLast(new HttpResponseEncoder(), new HttpRequestDecoder() , new FullRequestDecoder(), httpProxyHandshakeHandler);
				}
			});

			return bootStrap.bind(config.getPort());
		} catch (Exception e) {
			log.error("start occur error, config=" + config, e);
			throw new WiretigerException("WtDefaultServer start failed.", e);
		}
	}
	
	public void setInvokeChainInit(InvokeChainInit invokeChainInit) {
		this.invokeChainInit = invokeChainInit;
	}

	@Override
	public void onClose(Object hook) {
//		bossGroup.shutdownGracefully();
//		masterThreadPool.shutdownGracefully();
	}

	public void setMockHandler(MockHandler mockHandler) {
		this.mockHandler = mockHandler;
	}
	
	public int getListeningPort() {
		return config.getPort();
	}
}
