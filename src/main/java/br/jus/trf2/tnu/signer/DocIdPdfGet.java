package br.jus.trf2.tnu.signer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;
import com.crivano.restservlet.RestUtils;

public class DocIdPdfGet implements IRestAction {
	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {
		Id id = new Id(req.getString("id"));

		byte[] pdf = retrievePdf(id);

		// Produce response
		resp.put("doc", RestUtils.base64Encode(pdf));
	}

	protected static byte[] retrievePdf(Id id) throws Exception, SQLException {
		byte[] pdfCompressed = null;
		String status;
		String error;
		// Chama a procedure que recupera os dados do PDF para viabilizar a
		// assinatura
		//
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = Utils.getConnection();
			pstmt = conn.prepareStatement(Utils.getSQL("pdfinfo"));
			pstmt.setLong(1, id.processoid);
			pstmt.setLong(2, id.documentoid);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				String conteudo = rset.getString("conteudo");
				byte[] pdf = RestUtils.base64Decode(conteudo);
				return pdf;
			}
		} finally {
			if (rset != null)
				rset.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		throw new Exception("Não foi possível descomprimir o PDF.");
	}

	@Override
	public String getContext() {
		return "visualizar documento";
	}
}
