package com.suelen.artesanato.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suelen.artesanato.api.model.Categoria;
import com.suelen.artesanato.api.model.Foto;
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
	
	@Autowired
	private ApplicationEventPublisher publisher;

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
		
		for(Foto foto : produto.getFoto()) {
			foto.setProduto(produto);
		}
		
				
		//publisher.publishEvent(new ProdutoSalvoEvent(produto));
		
		return produtoRepository.saveAndFlush(produto);
	}

	public Produto atualizar(Long codigo, @Valid Produto produto) {
		
		Produto produtoSalvo = buscarPorProdutoExistente(codigo);
		
		BeanUtils.copyProperties(produto, produtoSalvo, "codigo");
		
		return produtoRepository.save(produtoSalvo);
	}

	private Produto buscarPorProdutoExistente(Long codigo) {

		Produto produtoSalvo = produtoRepository.findById(codigo)
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Não existe o produto que deseja alterar %d", codigo)));
		
		
		return produtoSalvo;
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Produto produtoSalvo = buscarProdutoPeloCodigo(codigo);
		produtoSalvo.setAtivo(ativo);
		
		produtoRepository.save(produtoSalvo);
	}

	private Produto buscarProdutoPeloCodigo(Long codigo) {
		Optional<Produto> produtoSalvo = produtoRepository.findById(codigo);
		
		if(!produtoSalvo.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return produtoSalvo.get();
	}

}
