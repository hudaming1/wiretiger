package org.hum.wiretiger.proxy.session.bean;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

@Getter
public class WtSession {
	
	private static final AtomicInteger IdCreator = new AtomicInteger(0);
	
	private long id;
	private Integer pipeId;
	private DefaultHttpRequest request;
	private long requestTime;
	private FullHttpResponse response;
	private byte[] responseBytes;
	private long responseTime;
	
	public WtSession(Integer pipeId, DefaultHttpRequest request, long requestTime) {
		this.id = IdCreator.incrementAndGet();
		this.pipeId = pipeId;
		this.request = request;
		this.requestTime = requestTime;
	}
	
	public void setResponse(FullHttpResponse response, byte[] responseBytes, long time) {
		this.response = response;
		this.responseBytes = responseBytes;
		this.responseTime = time;
	}
}