package br.jus.trf2.tnu.signer;

import java.sql.CallableStatement;
import java.sql.Connection;

import com.crivano.blucservice.api.IBlueCrystal;
import com.crivano.swaggerservlet.SwaggerCall;
import com.crivano.swaggerservlet.SwaggerUtils;

import br.jus.trf2.assijus.system.api.AssijusSystemContext;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdSignPut;

public class DocIdSignPut implements IDocIdSignPut {

	@Override
	public void run(Request req, Response resp, AssijusSystemContext ctx) throws Exception {
		Id id = new Id(req.id);
		String detached = SwaggerUtils.base64Encode(req.envelope);

		byte[] pdf = DocIdPdfGet.retrievePdf(id).pdf;

		// Call bluc-server attach webservice
		IBlueCrystal.IAttachPost.Request q = new IBlueCrystal.IAttachPost.Request();
		q.envelope = req.envelope;
		q.content = pdf;
		IBlueCrystal.IAttachPost.Response s = SwaggerCall.call("bluc-attach", null, "POST",
				Utils.getUrlBluCServer() + "/attach", q, IBlueCrystal.IAttachPost.Response.class);
		String attached = SwaggerUtils.base64Encode(s.envelope);

		// Chama a procedure que faz a gravação da assinatura
		//
		Connection conn = null;
		CallableStatement cstmt = null;
		try {
			conn = Utils.getConnection();

			cstmt = conn.prepareCall(Utils.getSQL("save"));

			cstmt.setString(1, detached);
			cstmt.setString(2, attached);
			cstmt.setLong(3, id.processoid);
			cstmt.setLong(4, id.documentoid);
			cstmt.execute();
		} finally {
			if (cstmt != null)
				cstmt.close();
			if (conn != null)
				conn.close();
		}
		resp.status = "OK";
	}

	@Override
	public String getContext() {
		return "salvar assinatura";
	}

}