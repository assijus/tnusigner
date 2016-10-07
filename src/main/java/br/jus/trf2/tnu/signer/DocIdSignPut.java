package br.jus.trf2.tnu.signer;

import java.sql.CallableStatement;
import java.sql.Connection;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdSignPutRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdSignPutResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdSignPut;

import com.crivano.blucservice.api.IBlueCrystal;
import com.crivano.swaggerservlet.SwaggerCall;
import com.crivano.swaggerservlet.SwaggerUtils;

public class DocIdSignPut implements IDocIdSignPut {

	@Override
	public void run(DocIdSignPutRequest req, DocIdSignPutResponse resp)
			throws Exception {
		Id id = new Id(req.id);
		String detached = SwaggerUtils.base64Encode(req.envelope);

		byte[] pdf = DocIdPdfGet.retrievePdf(id);

		// Call bluc-server attach webservice
		IBlueCrystal.AttachPostRequest q = new IBlueCrystal.AttachPostRequest();
		q.envelope = req.envelope;
		q.content = pdf;
		IBlueCrystal.AttachPostResponse s = SwaggerCall.call("bluc-attach",
				null, "POST", Utils.getUrlBluCServer() + "/attach", q,
				IBlueCrystal.AttachPostResponse.class);
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