package br.jus.trf2.tnu.signer;


public class Id {
	String cpf;
	long processoid;
	long documentoid;

	public Id(String id) {
		String[] split = id.split("_");
		this.cpf = split[0];
		this.processoid = Integer.valueOf(split[1]);
		this.documentoid = Long.valueOf(split[2]);
	}

	public Id(String cpf, long idsecao, long idtextoweb) {
		this.cpf = cpf;
		this.processoid = idsecao;
		this.documentoid = idtextoweb;
	}

	public String toString() {
		return cpf + "_" + processoid + "_" + documentoid;
	}
}
