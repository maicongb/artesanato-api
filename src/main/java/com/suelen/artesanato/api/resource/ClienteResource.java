package com.suelen.artesanato.api.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suelen.artesanato.api.dto.ClientePesquisaDTO;
import com.suelen.artesanato.api.event.RecursoCriadoEvent;
import com.suelen.artesanato.api.exceptionhandler.ArtesanatoExceptionHandler.Erro;
import com.suelen.artesanato.api.model.Cliente;
import com.suelen.artesanato.api.repository.ClienteRepository;
import com.suelen.artesanato.api.repository.filter.ClienteFilter;
import com.suelen.artesanato.api.service.ClienteService;
import com.suelen.artesanato.api.service.exception.ClienteExistenteException;

@RestController 
@RequestMapping("/clientes") 
public class ClienteResource {
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired  
	private ClienteRepository clienteRepository;
	
	@Autowired 
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MessageSource messageSource;
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente, HttpServletResponse response) {
		Cliente clienteSalvo  = clienteService.salvar(cliente);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, clienteSalvo.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Cliente> buscarPeloCodigo(@PathVariable Long codigo){
		Optional<Cliente> clienteSalvo = clienteRepository.findById(codigo);
		return clienteSalvo.isPresent() ? ResponseEntity.ok(clienteSalvo.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public void remover(@PathVariable Long codigo) {
		clienteRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Cliente> atualizar(@PathVariable Long codigo, @Valid @RequestBody Cliente cliente) {
		Cliente clienteSalvo = clienteService.atualizar(codigo, cliente);
		return ResponseEntity.ok(clienteSalvo);
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		clienteService.atualizarPropriedadeAtivo(codigo, ativo);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public Page<ClientePesquisaDTO> pesquisar(ClienteFilter clienteFilter, Pageable pageable) {
		
		return clienteRepository.filtrar(clienteFilter, pageable);
	
	}
	
	@ExceptionHandler({ ClienteExistenteException.class })
	public ResponseEntity<Object> handleClienteExistenteException(ClienteExistenteException ex){
		String mensagemUsuario = messageSource.getMessage("cliente.existente", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		
		return ResponseEntity.badRequest().body(erros);
	}
	
	
}
