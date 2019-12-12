package com.suelen.artesanato.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suelen.artesanato.api.model.Foto;

public interface FotoRepository extends JpaRepository<Foto, Long> {

	List<Foto> findByDescricaoIgnoreCase(String descricao);

}
