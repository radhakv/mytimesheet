package moten.david.util.google.app.engine.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginFilter implements Filter {

	private final UserService userService;

	public LoginFilter() {
		userService = UserServiceFactory.getUserService();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURI();
		if (httpRequest.getQueryString() != null)
			url += "?" + httpRequest.getQueryString();
		if (userService.getCurrentUser() != null) {
			if ("true".equals(request.getParameter("logout")))
				((HttpServletResponse) response).sendRedirect(userService
						.createLogoutURL(url));
			else
				chain.doFilter(request, response);
		} else if (response instanceof HttpServletResponse)
			((HttpServletResponse) response).sendRedirect(userService
					.createLoginURL(url));
		else

			throw new ServletException(
					"Unauthorized access, unable to forward to login page");
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
