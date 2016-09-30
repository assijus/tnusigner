package br.jus.trf2.tnu.signer;

import java.sql.CallableStatement;
import java.sql.Connection;

import org.json.JSONObject;

import br.jus.trf2.tnu.signer.ITNUSigner.DocIdSignPutRequest;
import br.jus.trf2.tnu.signer.ITNUSigner.DocIdSignPutResponse;
import br.jus.trf2.tnu.signer.ITNUSigner.IDocIdSignPut;

import com.crivano.restservlet.RestUtils;
import com.crivano.swaggerservlet.SwaggerUtils;

public class DocIdSignPut implements IDocIdSignPut {

	@Override
	public void run(DocIdSignPutRequest req, DocIdSignPutResponse resp)
			throws Exception {
		Id id = new Id(req.id);
		String detached = SwaggerUtils.base64Encode(req.envelope);
		String cpf = req.cpf;

		byte[] pdf = DocIdPdfGet.retrievePdf(id);
		String content = new String(SwaggerUtils.base64Encode(pdf));

		String msg = null;

		// Call bluc-server attach webservice
		JSONObject blucreq = new JSONObject();
		blucreq.put("envelope", detached);
		blucreq.put("content", content);
		JSONObject blucresp = RestUtils.restPost("bluc-attach", null,
				Utils.getUrlBluCServer() + "/attach", blucreq);
		String attached = blucresp.getString("envelope");

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