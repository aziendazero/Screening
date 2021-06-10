package model.commons;

import java.io.Serializable;

public class Scelta implements Serializable {
	private String nome;
	private String valore;


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getNome() {
		return nome;
	}


	public void setValore(String valore) {
		this.valore = valore;
	}


	public String getValore() {
		return valore;
	}
}