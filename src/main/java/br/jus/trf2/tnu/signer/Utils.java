package br.jus.trf2.tnu.signer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import com.crivano.swaggerservlet.SwaggerUtils;


public class Utils {
	private static final Map<String, byte[]> cache = new HashMap<String, byte[]>();

	public static String getUrlBluCServer() {
		return SwaggerUtils.getProperty("blucservice.url",
				"http://localhost:8080/blucservice/api/v1");
	}

	public static Connection getConnection() throws Exception {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:");
			DataSource ds = (DataSource) envContext
					.lookup("java:/jboss/datasources/TnuDS");
			Connection connection = ds.getConnection();
			if (connection == null)
				throw new Exception("Can't open connection to Oracle.");
			return connection;
		} catch (NameNotFoundException nnfe) {
			Connection connection = null;

			Class.forName("oracle.jdbc.OracleDriver");

			String dbURL = SwaggerUtils.getProperty("tnusigner.datasource.url",
					null);
			String username = SwaggerUtils.getProperty(
					"tnusigner.datasource.username", null);
			;
			String password = SwaggerUtils.getProperty(
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
