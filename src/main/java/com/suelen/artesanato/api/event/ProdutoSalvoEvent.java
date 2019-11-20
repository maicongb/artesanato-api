package com.suelen.artesanato.api.event;

import org.springframework.util.StringUtils;

import com.suelen.artesanato.api.model.Produto;

public class ProdutoSalvoEvent {
	
	private Produto produto;

	public ProdutoSalvoEvent(Produto produto) {
		this.produto = produto;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	public boolean TemFoto() {
		return !StringUtils.isEmpty(produto.getFoto());
	}
	
}
