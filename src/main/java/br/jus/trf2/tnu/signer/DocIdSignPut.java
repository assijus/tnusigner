package br.jus.trf2.tnu.signer;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;

public class DocIdSignPut implements IRestAction {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response,
			JSONObject req, JSONObject resp) throws Exception {
		Id id = new Id(req.getString("id"));
		String envelope = req.getString("envelope");
		String cpf = req.getString("cpf");

		String msg = null;

		// Chama a procedure que faz a gravação da assinatura
		//
		Connection conn = null;
		CallableStatement cstmt = null;
		try {
			conn = Utils.getConnection();

			cstmt = conn.prepareCall(Utils.getSQL("save"));

			cstmt.setString(1, envelope);
			cstmt.setLong(2, id.processoid);
			cstmt.setLong(3, id.documentoid);
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