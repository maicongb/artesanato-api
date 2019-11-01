package com.suelen.artesanato.api.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suelen.artesanato.api.model.Cliente;
import com.suelen.artesanato.api.repository.cliente.ClienteRepositoryQuery;

public interface ClienteRepository extends JpaRepository<Cliente, Long>, ClienteRepositoryQuery{

	Optional<Cliente> findByCpf(String cpf);

}
