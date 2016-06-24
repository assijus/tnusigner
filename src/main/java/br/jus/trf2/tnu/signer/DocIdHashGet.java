package br.jus.trf2.tnu.signer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;

public class DocIdHashGet implements IRestAction {
	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {
		Id id = new Id(req.getString("id"));

		byte[] pdf = DocIdPdfGet.retrievePdf(id);

		// Produce response
		String sha1 = Base64.encodeBase64String(calcSha1(pdf));
		String sha256 = Base64.encodeBase64String(calcSha256(pdf));
		resp.put("sha1", sha1);
		resp.put("sha256", sha256);
	}

	public static byte[] calcSha1(byte[] content)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.reset();
		md.update(content);
		byte[] output = md.digest();
		return output;
	}

	public static byte[] calcSha256(byte[] content)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(content);
		byte[] output = md.digest();
		return output;
	}

	@Override
	public String getContext() {
		return "obter o hash de documento do TNU";
	}
}
