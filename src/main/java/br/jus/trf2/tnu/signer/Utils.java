package br.jus.trf2.tnu.signer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.crivano.restservlet.RestUtils;

public class Utils {
	private static final Logger log = Logger.getLogger(Utils.class.getName());

	private static final Map<String, byte[]> cache = new HashMap<String, byte[]>();

	public static void fileWrite(String filename, byte[] ba)
			throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(ba);
		fos.close();
	}

	public static byte[] fileRead(String filename) throws IOException {
		File file = new File(filename);
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[409600];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				ous.write(buffer, 0, read);
			}
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
			}

			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}
		return ous.toByteArray();
	}

	public static byte[] compress(byte[] data) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				data.length);
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		return output;
	}

	public static byte[] decompress(byte[] data) throws IOException,
			DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				data.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		// log.info("Original: " + data.length);
		// log.info("Compressed: " + output.length);
		return output;
	}

	public static Connection getConnection() throws Exception {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:");
			DataSource ds = (DataSource) envContext
					.lookup("java:/jboss/datasources/ApoloDS");
			Connection connection = ds.getConnection();
			if (connection == null)
				throw new Exception("Can't open connection to Oracle.");
			return connection;
		} catch (NameNotFoundException nnfe) {
			Connection connection = null;

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			String dbURL = RestUtils.getProperty(
					"tnusigner.datasource.url", null);
			String username = RestUtils.getProperty(
					"tnusigner.datasource.username", null);
			;
			String password = RestUtils.getProperty(
					"tnusigner.datasource.password", null);
			;
			connection = DriverManager.getConnection(dbURL, username, password);
			if (connection == null)
				throw new Exception("Can't open connection.");
			return connection;
		}
	}

	public static String getSQL(String filename) {
		String text = new Scanner(DocListGet.class.getResourceAsStream(filename
				+ ".sql"), "UTF-8").useDelimiter("\\A").next();
		return text;
	}

	public static void store(String sha1, byte[] ba) {
		cache.put(sha1, ba);
	}

	public static byte[] retrieve(String sha1) {
		if (cache.containsKey(sha1)) {
			byte[] ba = cache.get(sha1);
			cache.remove(sha1);
			return ba;
		}
		return null;
	}
}
