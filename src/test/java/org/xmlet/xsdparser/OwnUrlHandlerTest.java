package org.xmlet.xsdparser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;

public class OwnUrlHandlerTest {

	public static class GzipURLStreamHandler extends URLStreamHandler {
		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return new GzipURLConnection(new File(u.getAuthority(), u.getFile()).toURI().toURL());
		}
	}

	public static class GzipURLConnection extends URLConnection {

		private URLConnection wrapped;

		protected GzipURLConnection(URL url) throws IOException {
			super(url);
			this.wrapped = url.openConnection();
		}

		@Override
		public void connect() throws IOException {
			wrapped.connect();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new GZIPInputStream(wrapped.getInputStream());
		}
	}

	static {
		URL.setURLStreamHandlerFactory(protocol -> {
			if ("gzip".equals(protocol)) {
				return new GzipURLStreamHandler();
			}
			return null;
		});
	}

	@Test
	public void testHandler() throws FileNotFoundException, IOException {
		InputStream is = getClass().getResourceAsStream("/issue_63/a.xsd");
		File file = new File("target/issue_63/a.xsd.gz");
		file.getParentFile().mkdirs();
		GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(file), true);
		copy(is, os);
		os.close();
		XsdParser parser = XsdParser.fromURL(new URL("gzip://target/issue_63/a.xsd.gz"), null);
		assertEquals("info", parser.getResultXsdElements().findFirst().get().getRawName());
		file.delete();
		file.getParentFile().delete();
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[8192];
		int n;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
	}

}
