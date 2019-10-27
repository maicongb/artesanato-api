package com.suelen.artesanato.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suelen.artesanato.api.model.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Long>{

	List<Cidade> findByEstadoCodigo(Long estado);

}
