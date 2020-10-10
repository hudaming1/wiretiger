package org.hum.wiretiger.console.websocket.service;

import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerPipeListVO;
import org.hum.wiretiger.console.websocket.ConsoleManager;
import org.hum.wiretiger.console.websocket.bean.WsServerMessage;
import org.hum.wiretiger.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiretiger.proxy.facade.WtPipeContext;

public class WsPipeService {

	private static final ConsoleManager CM = ConsoleManager.get();
	
	public void sendConnectMsg(WtPipeContext pipeHolder) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiretigerPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeConnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(pipeHolder));
			channel.writeAndFlush(msg);
		});
	}

	public void sendStatusChangeMsg(WtPipeContext pipe) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiretigerPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeUpdate);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(pipe));
			channel.writeAndFlush(msg);
		});
	}

	public void sendDisConnectMsg(WtPipeContext pipe) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiretigerPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeDisconnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(pipe));
			channel.writeAndFlush(msg);
		});
	}
}
