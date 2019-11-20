package com.suelen.artesanato.api.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.suelen.artesanato.api.event.ProdutoSalvoEvent;
import com.suelen.artesanato.api.storage.local.FotoStorage;

@Component
public class ProdutoListener {
	
	@Autowired
	private FotoStorage fotoStorage;
	
	//condition verifica se tem foto, se tiver executa o metodo
	@EventListener(condition = "#produtoSalvoEvent.temFoto()")
	public void produtoSalvo(ProdutoSalvoEvent produtoSalvoEvent) {
		
		//fotoStorage.salvar(produtoSalvoEvent);
	}

}
