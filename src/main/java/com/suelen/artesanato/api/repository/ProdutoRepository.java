package com.suelen.artesanato.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suelen.artesanato.api.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto,Long>{

	Optional<Produto> findByDescricaoIgnoreCase(String descricao);

}
