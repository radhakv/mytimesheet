package moten.david.kv;

import static moten.david.kv.Constants.AUTHENTICATED;
import static moten.david.kv.Constants.AUTHENTICATION;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 930288771386646504L;

	private static Logger log = Logger.getLogger(LoginServlet.class);

	@Inject
	private KeyValueService keyValueService;

	public LoginServlet() {
		ApplicationInjector.getInjector().injectMembers(this);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String key = req.getParameter("key");
			String password = req.getParameter("password");
			if (key == null)
				throw new ServletException("must specify a key parameter");
			if (!key.startsWith(AUTHENTICATED))
				throw new ServletException(
						"login only deals with keys that start with '"
								+ AUTHENTICATED + "'");
			String theKey = key.substring(AUTHENTICATED.length());
			String passwordKey = AUTHENTICATION + theKey;
			String passwordFromService = keyValueService.get(passwordKey);
			if (passwordFromService == null)
				throw new ServletException(
						"this key is an authenticated key but no password has been set for it yet. This can only be done by an administrator. Contact the adminstrator(s).");
			else if (passwordFromService.equals(password))
				req.getSession().setAttribute(key, Boolean.TRUE);
			else
				throw new ServletException(
						"password incorrect. Go back a page and attempt login again.");
			// having succeeded logging in, redirect to the originally requested
			// page
			String redirectTo = req.getParameter("continue");
			if (redirectTo != null) {
				// resp.getOutputStream().println("redirecting to " +
				// redirectTo);
				// resp.getOutputStream().flush();
				resp.sendRedirect(redirectTo);
			} else
				// no redirect specified so let user know that login worked
				resp.getOutputStream().print("logged in ok");
		} catch (ServletException e) {
			resp.getOutputStream().println(e.getMessage());
		}
	}
}
