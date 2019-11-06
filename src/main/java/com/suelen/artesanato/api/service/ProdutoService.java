package com.suelen.artesanato.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suelen.artesanato.api.model.Categoria;
import com.suelen.artesanato.api.model.Produto;
import com.suelen.artesanato.api.repository.CategoriaRepository;
import com.suelen.artesanato.api.repository.ProdutoRepository;
import com.suelen.artesanato.api.service.exception.EntidadeNaoEncontradaException;
import com.suelen.artesanato.api.service.exception.ProdutoJaExistenteException;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	public Produto salvar(Produto produto) {
		
		Long categoriaId = produto.getCategoria().getCodigo();
		
		Categoria categoria = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format("Não existe cadastro de categoria com código %d", categoriaId)));		
		
		Optional<Produto> produtoExiste = produtoRepository
								.findByDescricaoIgnoreCase(produto.getDescricao());
		
		if(produtoExiste.isPresent()) {
			throw new ProdutoJaExistenteException("Produto já cadastrado com esta descrição");
		}
		
		produto.setCategoria(categoria);
		
		
		return produtoRepository.save(produto);
	}

}
