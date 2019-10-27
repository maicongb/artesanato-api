package com.suelen.artesanato.api.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.suelen.artesanato.api.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

	Page<Cliente> findByNomeContaining(String nome, Pageable pageable);

}
