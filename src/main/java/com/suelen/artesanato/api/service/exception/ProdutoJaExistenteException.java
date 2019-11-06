package com.suelen.artesanato.api.service.exception;

public class ProdutoJaExistenteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProdutoJaExistenteException(String mensagem) {
		super(mensagem);
	}
	
}