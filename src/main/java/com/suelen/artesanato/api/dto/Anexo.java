package com.suelen.artesanato.api.dto;

public class Anexo {
	
	private String nome;
	private String contentType;
	private Long tamanho;
	private String url;
	
	public Anexo(String nome, String contentType, Long tamanho, String url) {
		this.nome = nome;
		this.contentType = contentType;
		this.tamanho = tamanho;
		this.url = url;
	}

	public String getNome() {
		return nome;
	}

	public String getContentType() {
		return contentType;
	}

	public Long getTamanho() {
		return tamanho;
	}

	public String getUrl() {
		return url;
	}

	
}
