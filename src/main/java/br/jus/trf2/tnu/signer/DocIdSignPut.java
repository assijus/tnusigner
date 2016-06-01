package br.jus.trf2.tnu.signer;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;
import com.crivano.restservlet.RestUtils;

public class DocIdSignPut implements IRestAction {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response,
			JSONObject req, JSONObject resp) throws Exception {
		Id id = new Id(req.getString("id"));
		String detached = req.getString("envelope");
		String cpf = req.getString("cpf");

		byte[] pdf = DocIdPdfGet.retrievePdf(id);
		String content = new String(new Base64().encode(pdf));

		String msg = null;

		// Call bluc-server attach webservice
		JSONObject blucreq = new JSONObject();
		blucreq.put("envelope", detached);
		blucreq.put("content", content);
		JSONObject blucresp = RestUtils.getJsonObjectFromJsonPost(
				new URL(Utils.getUrlBluCServer() + "/attach"), blucreq,
				"bluc-attach");
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
		resp.put("status", "OK");
	}

	@Override
	public String getContext() {
		return "salvar assinatura no TNU";
	}
}