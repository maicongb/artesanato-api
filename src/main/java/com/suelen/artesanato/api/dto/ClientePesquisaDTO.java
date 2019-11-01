package com.suelen.artesanato.api.dto;

public class ClientePesquisaDTO {
	
	private Long codigo;
	private String nome;
	private String cpf;
	private Boolean ativo;
	private String estado;
	private String cidade;
	
	
	public ClientePesquisaDTO(Long codigo, String nome, String cpf, Boolean ativo, String estado, String cidade) {
		this.codigo = codigo;
		this.nome = nome;
		this.cpf = cpf;
		this.ativo = ativo;
		this.estado = estado;
		this.cidade = cidade;
	}


	public Long getCodigo() {
		return codigo;
	}


	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}


	public Boolean getAtivo() {
		return ativo;
	}


	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getCidade() {
		return cidade;
	}


	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	
	
	
	
	

	

}
