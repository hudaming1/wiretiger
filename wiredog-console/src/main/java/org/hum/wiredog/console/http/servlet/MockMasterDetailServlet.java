package org.hum.wiredog.console.http.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.proxy.config.WiredogCoreConfigProvider;

/**
 * http://localhost:8080/mock/get
 */
public class MockMasterDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print(WiredogCoreConfigProvider.get().isOpenMasterMockStwich());
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}