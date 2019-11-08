package com.suelen.artesanato.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suelen.artesanato.api.model.Produto;
import com.suelen.artesanato.api.repository.produto.ProdutoRepositoryQuery;

public interface ProdutoRepository extends JpaRepository<Produto,Long>, ProdutoRepositoryQuery{

	Optional<Produto> findByDescricaoIgnoreCase(String descricao);

}
