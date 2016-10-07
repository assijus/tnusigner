package br.jus.trf2.tnu.signer;


public class Id {
	long processoid;
	long documentoid;

	public Id(String id) {
		String[] split = id.split("_");
		this.processoid = Integer.valueOf(split[0]);
		this.documentoid = Long.valueOf(split[1]);
	}

	public Id(long idsecao, long idtextoweb) {
		this.processoid = idsecao;
		this.documentoid = idtextoweb;
	}

	public String toString() {
		return processoid + "_" + documentoid;
	}
}
