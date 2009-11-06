package moten.david.util.tv.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.jamesmurty.utils.XMLBuilder;

public class ProgrammeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<String> channels = readChannels();
		try {
			XMLBuilder builder = XMLBuilder.create("html").e("table");
			for (String channel : channels) {
				builder = builder.e("tr").e("td").t(channel).up().up();
			}

			Properties outputProperties = new Properties();

			// Explicitly identify the output as an XML document
			outputProperties.put(javax.xml.transform.OutputKeys.METHOD, "xml");

			// Pretty-print the XML output (doesn't work in all cases)
			outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");

			// Get 2-space indenting when using the Apache transformer
			outputProperties.put("{http://xml.apache.org/xslt}indent-amount",
					"2");
			String result = builder.asString(outputProperties);
			result = result.substring(result.indexOf("\n") + 1);
			resp.getOutputStream().write(result.getBytes());
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		} catch (FactoryConfigurationError e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	private List<String> readChannels() {
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream("/channels.txt")));
		ArrayList<String> list = new ArrayList<String>();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					String[] items = line.split("\t");
					list.add(items[0]);
				}
			}
			return list;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
