package com.suelen.artesanato.api.repository.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suelen.artesanato.api.model.Produto;
import com.suelen.artesanato.api.repository.filter.ProdutoFilter;

public interface ProdutoRepositoryQuery {
	
	Page<Produto> filtrar(ProdutoFilter produtoFilter, Pageable pageable);

}
