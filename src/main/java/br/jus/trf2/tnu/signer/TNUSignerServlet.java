package br.jus.trf2.tnu.signer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.SwaggerUtils;
import com.crivano.swaggerservlet.dependency.SwaggerServletDependency;
import com.crivano.swaggerservlet.dependency.TestableDependency;

import br.jus.trf2.assijus.system.api.IAssijusSystem;

public class TNUSignerServlet extends SwaggerServlet {
	private static final long serialVersionUID = -1611417120964698257L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.setAPI(IAssijusSystem.class);

		super.setActionPackage("br.jus.trf2.tnu.signer");

		super.setAuthorization(SwaggerUtils.getProperty("tnusigner.password", null));

		addDependency(new SwaggerServletDependency("webservice", "blucservice", false) {
			@Override
			public String getUrl() {
				return Utils.getUrlBluCServer();
			}

			@Override
			public String getResponsable() {
				return null;
			}
		});

		addDependency(new TestableDependency("database", "tnuds", false) {
			@Override
			public String getUrl() {
				return "java:/jboss/datasources/TnuDS";
			}

			@Override
			public boolean test() throws Exception {
				Utils.getConnection().close();
				return true;
			}
		});

	}
}
