package com.suelen.artesanato.api.dto;

import java.math.BigInteger;

import com.suelen.artesanato.api.model.Foto;

public class FotoDTO extends Foto {
	
	private Long codigo;
	private String descricao;
	private String contentType;
	private BigInteger tamanho;
	
	public FotoDTO(Long codigo, String descricao, String contentType, BigInteger tamanho) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.contentType = contentType;
		this.tamanho = tamanho;
	}
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public BigInteger getTamanho() {
		return tamanho;
	}
	public void setTamanho(BigInteger tamanho) {
		this.tamanho = tamanho;
	}
	
	
	

}
