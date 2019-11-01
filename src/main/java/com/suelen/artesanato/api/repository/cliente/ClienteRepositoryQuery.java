package com.suelen.artesanato.api.repository.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.suelen.artesanato.api.dto.ClientePesquisaDTO;
import com.suelen.artesanato.api.repository.filter.ClienteFilter;

public interface ClienteRepositoryQuery {
	
	public Page<ClientePesquisaDTO> filtrar(ClienteFilter lancamentoFilter, Pageable pageable);

}
