package br.jus.trf2.tnu.signer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.dependency.SwaggerServletDependency;
import com.crivano.swaggerservlet.dependency.TestableDependency;

import br.jus.trf2.assijus.system.api.IAssijusSystem;

public class TNUSignerServlet extends SwaggerServlet {
	private static final long serialVersionUID = -1611417120964698257L;

	public static TNUSignerServlet INSTANCE = null;

	public static String getProp(String name) {
		return INSTANCE.getProperty(name);
	}

	@Override
	public void initialize(ServletConfig config) throws ServletException {
		this.INSTANCE = this;
		setAPI(IAssijusSystem.class);
		setActionPackage("br.jus.trf2.tnu.signer");

		addRestrictedProperty("datasource.name", "java:/jboss/datasources/TnuDS");
		addRestrictedProperty("datasource.url", null);
		addRestrictedProperty("datasource.username", null);
		addPrivateProperty("datasource.password", null);

		addRestrictedProperty("blucservice.url", "http://localhost:8080/blucservice/api/v1");

		addPrivateProperty("password", null);
		super.setAuthorization(getProperty("password"));

		addDependency(new SwaggerServletDependency("webservice", "blucservice", false, 0, 10000) {
			@Override
			public String getUrl() {
				return Utils.getUrlBluCServer();
			}
		});

//		addDependency(new TestableDependency("database", "tnuds", false, 0, 10000) {
//			@Override
//			public String getUrl() {
//				return getProperty("datasource.name");
//			}
//
//			@Override
//			public boolean test() throws Exception {
//				Utils.getConnection().close();
//				return true;
//			}
//		});

	}
}
