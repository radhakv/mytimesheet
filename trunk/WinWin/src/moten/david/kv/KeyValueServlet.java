package moten.david.kv;

import static moten.david.kv.Constants.AUTHENTICATED;
import static moten.david.kv.Constants.AUTHENTICATION;
import static moten.david.kv.Constants.SECURE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;

import com.google.inject.Inject;

public class KeyValueServlet extends HttpServlet {

	private static final String HTTPS = "https";

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
			String action = request.getParameter("action");
			if (key == null)
				throw new ServletException("key parameter must not be null");
			if (action == null)
				throw new ServletException("action parameter must not be null");
			if (request.getCharacterEncoding() != null) {
				key = new String(key.getBytes(), request.getCharacterEncoding());
				if (value != null)
					value = new String(value.getBytes(), request
							.getCharacterEncoding());
				action = new String(action.getBytes(), request
						.getCharacterEncoding());
			}
			{
				// there are some key patterns that cause special behaviour
				// key starts with secure
				checkSecure(key);
				checkAuthentication(key);
				if (!checkAuthenticated(key, request)) {
					requestAuthentication(response);
					return;
				}
			}

			String copyTo = request.getParameter("copyTo");
			if (copyTo != null) {
				checkSecure(copyTo);
				checkAuthentication(copyTo);
				if (!checkAuthenticated(copyTo, request)) {
					requestAuthentication(response);
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
				setNoCacheParameters(response);
				if ("true".equalsIgnoreCase(request.getParameter("decodeB64"))) {
					// decode B64 to bytes
					if (result != null) {
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						Base64OutputStream os = new Base64OutputStream(bytes,
								false);
						os.write(result.getBytes());
						os.close();
						respondWith(response, bytes.toByteArray());
					}
				} else
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
			log(e.getMessage(), e);
			respondWith(response, e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void setNoCacheParameters(HttpServletResponse response) {
		// Set to expire far in the past.
		response.setHeader("Expires", "-1");

		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate");

		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");

		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
	}

	private void requestAuthentication(HttpServletResponse response)
			throws IOException {
		response.setStatus(401);
		response.setHeader("WWW-Authenticate", "Basic realm=\"Please login\"");
		response.setHeader("Date", new Date().toString());
		response.setHeader("Content-Type", "text/html");
		response
				.getOutputStream()
				.println(
						"<html><head><title>Error</title></head><body><h1>401 Unauthorized</h1></body></html>");
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
	private boolean checkAuthenticated(String key, HttpServletRequest request)
			throws ServletException {
		// key starts with authenticate
		// this one is used to allow authentication but bypassing the google
		// apps authentication because it requires cookies and Google Earth for
		// instance do not allow cookies
		boolean authorized = true;
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
			String authorization = request.getHeader("Authorization");
			String coreKey = key.substring(AUTHENTICATED.length());
			String expectedUsernameColonPassword = keyValueService
					.get(AUTHENTICATION + coreKey);
			authorized = false;
			if (authorization != null) {
				authorization = authorization.substring(6);
				String usernameColonPassword;
				Base64 base64 = new Base64();
				usernameColonPassword = new String(base64.decode(authorization));
				if (usernameColonPassword.equals(expectedUsernameColonPassword))
					authorized = true;
			}
			if (!authorized) {
				String usernameColonPassword = request.getParameter("username")
						+ ":" + request.getParameter("password");
				if (usernameColonPassword.equals(expectedUsernameColonPassword))
					authorized = true;
			}
		}
		return authorized;
	}

	private void checkAuthentication(String key) throws ServletException {
		// key starts with securePassword
		if (key.startsWith(AUTHENTICATION)
				&& !administration.currentUserIsAdministrator())
			throw new ServletException(
					"as key starts with '"
							+ AUTHENTICATION
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

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(URLEncoder.encode(":", "UTF-8"));
	}

}
