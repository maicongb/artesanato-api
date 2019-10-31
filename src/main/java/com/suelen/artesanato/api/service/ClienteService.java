package com.suelen.artesanato.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.suelen.artesanato.api.model.Cliente;
import com.suelen.artesanato.api.repository.ClienteRepository;
import com.suelen.artesanato.api.service.exception.PessoaExistenteException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;

	public Cliente salvar(Cliente cliente) {
		
		Optional<Cliente> clienteExiste = clienteRepository.findByCpf(cliente.getCpf());
		
		if(clienteExiste.isPresent()) {
			throw new PessoaExistenteException();
		}
		
		
		cliente.getContatos().forEach(contato -> contato.setCliente(cliente));
		return clienteRepository.save(cliente);
	}

	public Cliente atualizar(Long codigo, @Valid Cliente cliente) {
		Cliente clienteSalvo =  buscarClientePeloCodigo(codigo);
		
		clienteSalvo.getContatos().clear();
		clienteSalvo.getContatos().addAll(cliente.getContatos());
		clienteSalvo.getContatos().forEach(contatos -> contatos.setCliente(clienteSalvo));
		
		BeanUtils.copyProperties(cliente, clienteSalvo, "codigo", "contatos");
		
		return clienteRepository.save(clienteSalvo);
	}
	
	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Cliente clienteSalvo = buscarClientePeloCodigo(codigo);
		clienteSalvo.setAtivo(ativo);
		clienteRepository.save(clienteSalvo);
		
	}
	
	public Cliente buscarClientePeloCodigo(Long codigo) {
		Optional<Cliente> clienteSalvo = clienteRepository.findById(codigo);
		
		if(!clienteSalvo.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return clienteSalvo.get();
	}

}
