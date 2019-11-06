package com.suelen.artesanato.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suelen.artesanato.api.event.RecursoCriadoEvent;
import com.suelen.artesanato.api.model.Produto;
import com.suelen.artesanato.api.repository.ProdutoRepository;
import com.suelen.artesanato.api.service.ProdutoService;
import com.suelen.artesanato.api.service.exception.EntidadeNaoEncontradaException;
import com.suelen.artesanato.api.service.exception.ProdutoJaExistenteException;

@RestController
@RequestMapping("/produtos")
public class ProdutoResource {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<List<Produto>> listar() {
		
		List<Produto> produto = produtoRepository.findAll();
		
		return ResponseEntity.ok().body(produto);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Produto> buscaPeloCodigo(@PathVariable Long codigo) {
		Optional<Produto> produto = produtoRepository.findById(codigo);
		return produto.isPresent() ? ResponseEntity.ok(produto.get()) : ResponseEntity.notFound().build();
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<?> criar(@Valid @RequestBody Produto produto, HttpServletResponse response) {
		
		try {
			
			Produto produtoSalvo = produtoService.salvar(produto);
			
			//Chama o evento RecursoCriadoEvent
			publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoSalvo.getCodigo()));
			
			return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
			
		} catch (ProdutoJaExistenteException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (EntidadeNaoEncontradaException e) {
			//BADREQUEST RECURSO NÃO ENCONTRADO TIPO CATEGORIA/10,
			//JA QUE EXISTE ATE CATEGORIA/6
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
}
