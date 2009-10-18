package moten.david.kv;

import static moten.david.kv.Constants.AUTHENTICATED;
import static moten.david.kv.Constants.SECURE_PASSWORD;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 930288771386646504L;

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
			if (key == null || !key.startsWith(AUTHENTICATED))
				throw new RuntimeException(
						"login only deals with keys that start with '"
								+ AUTHENTICATED + "'");
			String theKey = key.substring(AUTHENTICATED.length());
			String passwordKey = SECURE_PASSWORD + theKey;
			String passwordFromService = keyValueService.get(passwordKey);
			if (passwordFromService == null)
				throw new ServletException(
						"this key is an authenticated key but no password has been set for it yet. This can only be done by an administrator. Contact the adminstrator(s).");
			else if (passwordFromService.equals(password))
				req.getSession().setAttribute(key, Boolean.TRUE);
			else
				throw new ServletException(
						"password incorrect. Go back a page and attempt login again.");
		} catch (ServletException e) {
			resp.getOutputStream().println(e.getMessage());
		}
	}
}
