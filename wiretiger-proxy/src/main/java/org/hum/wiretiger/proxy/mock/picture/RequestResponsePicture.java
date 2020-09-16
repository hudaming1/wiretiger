package org.hum.wiretiger.proxy.mock.picture;

import org.hum.wiretiger.proxy.mock.Mock;
import org.hum.wiretiger.proxy.mock.rebuild.RequestRebuilder;
import org.hum.wiretiger.proxy.mock.rebuild.ResponseRebuild;

public class RequestResponsePicture {

	private RequestInterceptor requestInterceptor;
	private ResponseInterceptor responseInterceptor;
	private RequestRebuilder requestRebuilder;
	private ResponseRebuild responseRebuild;

	public RequestResponsePicture eval(RequestInterceptor requestInterceptor, ResponseInterceptor responseInterceptor) {
		this.requestInterceptor = requestInterceptor;
		this.responseInterceptor = responseInterceptor;
		return this;
	}

	public RequestResponsePicture rebuildRequest(RequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	public RequestResponsePicture rebuildResponse(ResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(requestInterceptor, responseInterceptor, requestRebuilder, responseRebuild);
	}
}
