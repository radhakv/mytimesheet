package moten.david.kv;

import static moten.david.kv.Constants.AUTHENTICATED;
import static moten.david.kv.Constants.SECURE;
import static moten.david.kv.Constants.SECURE_PASSWORD;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.kv.test.Base64;

import com.google.inject.Inject;

public class KeyValueServlet extends HttpServlet {

	private static final String HTTPS = "https";

	private static final String UTF_8 = "UTF-8";

	private boolean secure;

	@Override
	public void init(ServletConfig config) throws ServletException {
		if ("true".equalsIgnoreCase(config.getInitParameter(SECURE)))
			secure = true;
	}

	private static final long serialVersionUID = 7666105647132551950L;

	@Inject
	private KeyValueService keyValueService;
	@Inject
	private Administration administration;

	public KeyValueServlet() {
		ApplicationInjector.getInjector().injectMembers(this);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String key = request.getParameter("key");
			String value = request.getParameter("value");
			if (key == null)
				throw new ServletException("key parameter must not be null");
			String action = request.getParameter("action");
			if (request.getCharacterEncoding() != null) {
				key = new String(key.getBytes(), request.getCharacterEncoding());
				value = new String(value.getBytes(), request
						.getCharacterEncoding());
				action = new String(action.getBytes(), request
						.getCharacterEncoding());
			}
			{
				// there are some key patterns that cause special behaviour
				// key starts with secure
				checkSecure(key);
				checkSecurePassword(key);
				String loginUrl = checkAuthenticated(key, request);
				if (loginUrl != null) {
					response.sendRedirect(loginUrl);
					return;
				}
			}

			String copyTo = request.getParameter("copyTo");
			if (copyTo != null) {
				checkSecure(copyTo);
				checkSecurePassword(copyTo);
				String loginUrl = checkAuthenticated(copyTo, request);
				if (loginUrl != null) {
					response.sendRedirect(loginUrl);
					return;
				}
			}

			if ("get".equals(action)) {
				String contentType = request.getParameter("contentType");
				if (contentType != null)
					response.setContentType(contentType);
				String result = keyValueService.get(key);
				String filename = request.getParameter("filename");
				if (filename != null)
					response.setHeader("Content-Disposition",
							"inline; filename=" + filename);
				if ("true".equalsIgnoreCase(request.getParameter("decodeB64")))
					// decode B64 to bytes
					respondWith(response, Base64.toBytes(result));
				else
					respondWith(response, (result == null ? "" : result));
			} else if ("put".equals(action)) {
				if (value == null)
					throw new ServletException(
							"value parameter must not be null");
				keyValueService.put(key, value);
				respondWith(response, "ok");
			} else if ("append".equals(action)) {
				if (value == null)
					throw new ServletException(
							"value parameter must not be null");
				keyValueService.append(key, value);
				respondWith(response, "ok");
			} else if ("copy".equals(action)) {
				if (copyTo == null)
					throw new ServletException(
							"copyTo parameter must not be null when action is 'copy'");
				keyValueService.copy(key, copyTo);
				respondWith(response, "ok");
			} else
				throw new ServletException(
						"action parameter must be either 'get' or 'put'");
		} catch (ServletException e) {
			respondWith(response, e.getMessage());
		}
	}

	/**
	 * if authenticated or authentication not required returns null otherwise
	 * returns the login url
	 * 
	 * @param key
	 * @param request
	 * @return
	 * @throws ServletException
	 */
	private String checkAuthenticated(String key, HttpServletRequest request)
			throws ServletException {
		// key starts with authenticate
		// this one is used to allow authentication but bypassing the google
		// apps authentication because it requires cookies and Google Earth for
		// instance do not allow cookies
		if (key.startsWith(AUTHENTICATED)
				&& key.length() > AUTHENTICATED.length()) {
			if (!request.getScheme().equals(HTTPS)) {
				throw new ServletException(
						"as the key "
								+ key
								+ " starts with '"
								+ AUTHENTICATED
								+ "' you must use https protocol rather than http. Just change the address you are using so it starts with https://");
			}

			if (!Boolean.TRUE.equals(request.getSession().getAttribute(key))) {
				// not authenticated
				try {
					String loginUrl = "/login.jsp?"
							+ encodeParameter("key", request, true)
							+ encodeParameter("action", request)
							+ encodeParameter("value", request)
							+ encodeParameter("copyTo", request)
							+ encodeParameter("contentType", request)
							+ encodeParameter("filename", request)
							+ encodeParameter("decodeB64", request)
							+ "&continue="
							+ URLEncoder.encode(getUrl(request), UTF_8);
					return loginUrl;
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	private String encodeParameter(String name, HttpServletRequest request) {
		return encodeParameter(name, request, false);
	}

	private String encodeParameter(String name, HttpServletRequest request,
			boolean isFirst) {
		String s = "";
		if (request.getParameter(name) != null)
			try {
				s = (isFirst ? "" : "&") + name + "="
						+ URLEncoder.encode(request.getParameter(name), UTF_8);

			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		return s;
	}

	private String getUrl(HttpServletRequest request) {

		String url = request.getScheme() + ":/" + request.getRequestURI();
		if (request.getQueryString() != null)
			url += "?" + request.getQueryString();
		return url;
	}

	private void checkSecurePassword(String key) throws ServletException {
		// key starts with securePassword
		if (key.startsWith(SECURE_PASSWORD)
				&& !administration.currentUserIsAdministrator())
			throw new ServletException(
					"as key starts with '"
							+ SECURE_PASSWORD
							+ "' you ("
							+ administration.getCurrentUser()
							+ ")  must be on the administrator list to amend or access this value");
	}

	private void checkSecure(String key) throws ServletException {
		if (!secure && key.startsWith(SECURE))
			throw new ServletException("as key starts with '" + SECURE
					+ "' you must use a secure servlet");
	}

	private void respondWith(HttpServletResponse response, String value)
			throws IOException {
		respondWith(response, value.getBytes());
	}

	private void respondWith(HttpServletResponse response, byte[] bytes)
			throws IOException {
		ServletOutputStream os = response.getOutputStream();
		if (bytes != null)
			os.write(bytes);
		os.close();
	}
}
