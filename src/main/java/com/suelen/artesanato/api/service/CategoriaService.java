package com.suelen.artesanato.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suelen.artesanato.api.model.Categoria;
import com.suelen.artesanato.api.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
	@Autowired 
	private CategoriaRepository categoriaRepository;

	public Categoria atualizar(Long codigo, @Valid Categoria categoria) {
		
		Categoria categoriaSalva = buscarCategoriaPorCodigo(codigo);
		
		BeanUtils.copyProperties(categoria, categoriaSalva, "codigo");
		
		
		return categoriaRepository.save(categoriaSalva);
	}

	public Categoria buscarCategoriaPorCodigo(Long codigo) {
		
		Optional<Categoria> categoriaSalva = categoriaRepository.findById(codigo);
		
		if(!categoriaSalva.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return categoriaSalva.get();
		
	}

}
