package br.jus.trf2.tnu.signer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.crivano.swaggerservlet.SwaggerUtils;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdPdfGetRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdPdfGetResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdPdfGet;

public class DocIdPdfGet implements IDocIdPdfGet {

	@Override
	public void run(DocIdPdfGetRequest req, DocIdPdfGetResponse resp) throws Exception {
		Id id = new Id(req.id);

		br.jus.trf2.tnu.signer.PdfData pdfd = retrievePdf(id);
		resp.doc = pdfd.pdf;
		resp.secret = pdfd.secret;
	}

	protected static PdfData retrievePdf(Id id) throws Exception, SQLException {
		PdfData pdfd = new PdfData();

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
				pdfd.pdf = SwaggerUtils.base64Decode(conteudo);
				pdfd.secret = rset.getString("secret");
				return pdfd;
			}
		} finally {
			if (rset != null)
				rset.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		throw new Exception("Não foi possível obter o PDF.");
	}

	@Override
	public String getContext() {
		return "visualizar documento";
	}

}
