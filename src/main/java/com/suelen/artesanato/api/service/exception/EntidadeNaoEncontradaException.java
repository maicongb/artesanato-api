package com.suelen.artesanato.api.service.exception;

public class EntidadeNaoEncontradaException  extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EntidadeNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	
}