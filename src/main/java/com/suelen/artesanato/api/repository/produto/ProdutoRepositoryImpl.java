package com.suelen.artesanato.api.repository.produto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.suelen.artesanato.api.model.Produto;
import com.suelen.artesanato.api.repository.filter.ProdutoFilter;

public class ProdutoRepositoryImpl implements ProdutoRepositoryQuery {

	
	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Produto> filtrar(ProdutoFilter produtoFilter, Pageable pageable) {
		
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<Produto> query = cb.createQuery(Produto.class);
		
		Root<Produto> produto = query.from(Produto.class);
		
		Predicate[] predicates = criarRestricoes(produtoFilter, cb, produto);
		query.where(predicates);
		
		TypedQuery<Produto> typedQueryProduto = manager.createQuery(query);
		adicionarRestricoesDePaginacao(typedQueryProduto, pageable);
		
		
		return new PageImpl<>(typedQueryProduto.getResultList(), pageable, total(produtoFilter));
	}

	//PAGINAÇÃO
	private Long total(ProdutoFilter produtoFilter) {
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Produto> root = query.from(Produto.class);
			
		Predicate[] predicates = criarRestricoes(produtoFilter, cb, root);
		query.where(predicates);
		
		query.select(cb.count(root));
		return manager.createQuery(query).getSingleResult();
	}

	//PAGINAÇÃO
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	

	private Predicate[] criarRestricoes(ProdutoFilter produtoFilter, CriteriaBuilder cb, Root<Produto> produto) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(produtoFilter.getDescricao())) {
			predicates.add(cb.like(
					cb.lower(produto.get("descricao")), "%" + produtoFilter.getDescricao().toLowerCase()+ "%"));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
