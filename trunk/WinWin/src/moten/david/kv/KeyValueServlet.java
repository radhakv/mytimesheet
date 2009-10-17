package moten.david.kv;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.kv.test.Base64;

import com.google.inject.Inject;

public class KeyValueServlet extends HttpServlet {

	private static final long serialVersionUID = 7666105647132551950L;

	@Inject
	private KeyValueService keyValueService;

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
		String key = request.getParameter("key");
		String value = request.getParameter("value");
		if (key == null)
			throw new ServletException("key parameter must not be null");
		String action = request.getParameter("action");
		if (request.getCharacterEncoding() != null) {
			key = new String(key.getBytes(), request.getCharacterEncoding());
			value = new String(value.getBytes(), request.getCharacterEncoding());
			action = new String(action.getBytes(), request
					.getCharacterEncoding());
		}

		if ("get".equals(action)) {
			String contentType = request.getParameter("contentType");
			if (contentType != null)
				response.setContentType(contentType);
			String result = keyValueService.get(key);
			String filename = request.getParameter("filename");
			if (filename != null)
				response.setHeader("Content-Disposition", "inline; filename="
						+ filename);
			if ("true".equalsIgnoreCase(request.getParameter("decodeB64")))
				// decode B64 to bytes
				respondWith(response, Base64.toBytes(result));
			else
				respondWith(response, result);
		} else if ("put".equals(action)) {
			if (value == null)
				throw new ServletException("value parameter must not be null");
			keyValueService.put(key, value);
			respondWith(response, "ok");
		} else if ("append".equals(action)) {
			if (value == null)
				throw new ServletException("value parameter must not be null");
			keyValueService.append(key, value);
			respondWith(response, "ok");
		} else
			throw new ServletException(
					"action parameter must be either 'get' or 'put'");
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
