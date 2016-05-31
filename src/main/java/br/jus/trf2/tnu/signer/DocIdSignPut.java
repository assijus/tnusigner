package br.jus.trf2.tnu.signer;

import java.io.ByteArrayInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;

@SuppressWarnings("serial")
public class DocIdSignPut implements IRestAction {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response,
			JSONObject req, JSONObject resp) throws Exception {
		Id id = new Id(req.getString("id"));
		String envelope = req.getString("envelope");
		String cpf = req.getString("cpf");

		byte[] assinatura = envelope.getBytes("UTF-8");

		byte[] envelopeCompressed = Utils.compress(assinatura);

		String msg = null;

		// Chama a procedure que faz a gravação da assinatura
		//
		Connection conn = null;
		CallableStatement cstmt = null;
		try {
			conn = Utils.getConnection();

			cstmt = conn.prepareCall(Utils.getSQL("save"));

			// p_CodSecao -> Código da Seção Judiciária (50=ES; 51=RJ;
			// 2=TRF)
			cstmt.setInt(1, id.processoid);

			// p_CodDoc -> Código interno do documento
			cstmt.setLong(2, id.documentoid);

			// p_ArqAssin -> Arquivo de assinatura (Compactado)
			cstmt.setBlob(3, new ByteArrayInputStream(envelopeCompressed));

			// CPF
			cstmt.setString(4, cpf);

			// Status
			cstmt.registerOutParameter(5, Types.VARCHAR);

			// Error
			cstmt.registerOutParameter(6, Types.VARCHAR);

			cstmt.execute();

			// Produce response
			resp.put("status", cstmt.getObject(5));
			resp.put("error", cstmt.getObject(6));
		} finally {
			if (cstmt != null)
				cstmt.close();
			if (conn != null)
				conn.close();
		}
	}

	@Override
	public String getContext() {
		return "salvar assinatura no TNU";
	}
}