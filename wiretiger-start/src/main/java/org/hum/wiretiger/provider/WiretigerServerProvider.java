package org.hum.wiretiger.provider;

import org.hum.wiretiger.console.common.chain.PipeManagerInvokeChain;
import org.hum.wiretiger.console.common.chain.SessionManagerInvokeChain;
import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WiretigerConsoleConfig;
import org.hum.wiretiger.console.websocket.WebSocketServer;
import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.server.WtDefaultServer;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WiretigerServerProvider {
	
	private WtDefaultServer proxyServer;
	private ConsoleServer consoleServer;
	private WebSocketServer webSocketServer;
	
	public WiretigerServerProvider(WiretigerCoreConfig coreConfig, WiretigerConsoleConfig consoleConfig) {
		super();
		PipeManagerInvokeChain pipeManagerInvokeChain = new PipeManagerInvokeChain(null);
		// proxy-server
		this.proxyServer = new WtDefaultServer(coreConfig);
		this.proxyServer.setMockHandler(new MockHandler(coreConfig.getMockList()));
		this.proxyServer.setInvokeChainInit(()-> {
			return new SessionManagerInvokeChain(pipeManagerInvokeChain);
		});
		
		// console HTTP-server
		this.consoleServer = new ConsoleServer(consoleConfig);
		
		// console WebSocket-server
		this.webSocketServer = new WebSocketServer(consoleConfig.getWebSocketPort());
	}

	public void start() throws InterruptedException {
		ChannelFuture proxyStartFuture = proxyServer.start();
		try {
			consoleServer.startJetty();
		} catch (Exception e) {
			log.error("console-server start error", e);
		}
		ChannelFuture wsStartFuture = webSocketServer.start();
		proxyStartFuture.sync();
		log.info("proxy server started, listening port:" + proxyServer.getListeningPort());
		wsStartFuture.sync();
		log.info("console-ws_server started, listening port:" + webSocketServer.getPort());
	}

}
