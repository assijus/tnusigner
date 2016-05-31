package br.jus.trf2.tnu.signer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.crivano.swaggerservlet.Swagger;
import com.crivano.swaggerservlet.SwaggerServlet;

public class TNUSignerServlet extends SwaggerServlet {
	private static final long serialVersionUID = -1611417120964698257L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.setActionPackage("br.jus.trf2.tnu.signer");

		Swagger sw = new Swagger();
		sw.loadFromInputStream(this.getServletContext().getResourceAsStream(
				"/api/v1/swagger.yaml"));

		super.setSwagger(sw);
	}
}