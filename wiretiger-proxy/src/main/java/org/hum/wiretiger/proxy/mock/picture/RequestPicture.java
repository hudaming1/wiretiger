package org.hum.wiretiger.proxy.mock.picture;

import org.hum.wiretiger.proxy.mock.Mock;
import org.hum.wiretiger.proxy.mock.rebuild.RequestRebuilder;
import org.hum.wiretiger.proxy.mock.rebuild.ResponseRebuild;

import lombok.Data;

@Data
public class RequestPicture {
	
	private RequestInterceptor requestInterceptor;
	private RequestRebuilder requestRebuilder;
	private ResponseRebuild responseRebuild;

	public RequestPicture eval(RequestInterceptor requestInterceptor) {
		this.requestInterceptor = requestInterceptor;
		return this;
	}

	public RequestPicture rebuildRequest(RequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	public RequestPicture rebuildResponse(ResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(requestInterceptor, null, requestRebuilder, responseRebuild);
	}
}
