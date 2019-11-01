package com.suelen.artesanato.api.repository.cliente;

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

import com.suelen.artesanato.api.model.Cliente;
import com.suelen.artesanato.api.repository.filter.ClienteFilter;

public class ClienteRepositoryImpl implements ClienteRepositoryQuery {
	
	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<Cliente> filtrar(ClienteFilter clienteFilter, Pageable pageable) {

		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
		
		Root<Cliente> root = query.from(Cliente.class);
		
		Predicate[] predicates = criarRestricoes(clienteFilter, cb, root);
		query.where(predicates);
		
		TypedQuery<Cliente> typedQueryCliente = manager.createQuery(query);
		adicionarRestricoesDePaginacao(typedQueryCliente, pageable);
		
		
		return new PageImpl<>(typedQueryCliente.getResultList(), pageable, total(clienteFilter));
	}

	private Predicate[] criarRestricoes(ClienteFilter clienteFilter, CriteriaBuilder cb, Root<Cliente> root) {

		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(clienteFilter.getNome())) {
			
			predicates.add(cb.like(
					cb.lower(root.get("nome")), "%" + clienteFilter.getNome().toLowerCase()+ "%"));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	//PAGINAÇÃO
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	
	//PAGINAÇÃO
	private Long total(ClienteFilter clienteFilter) {
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Cliente> root = criteria.from(Cliente.class);
		
		Predicate[] predicates = criarRestricoes(clienteFilter, cb, root);
		criteria.where(predicates);
		
		criteria.select(cb.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}
	

}
