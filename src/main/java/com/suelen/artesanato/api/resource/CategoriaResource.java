package com.suelen.artesanato.api.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suelen.artesanato.api.event.RecursoCriadoEvent;
import com.suelen.artesanato.api.model.Categoria;
import com.suelen.artesanato.api.repository.CategoriaRepository;
import com.suelen.artesanato.api.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher  publisher;
	
	@Autowired 
	private CategoriaService categoriaService;
	
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public Page<Categoria> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, Pageable pageable) {
		return categoriaRepository.findByNomeContaining(nome, pageable);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Categoria> incluir(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		//Chama o evento RecursoCriadoEvent
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> categoria = this.categoriaRepository.findById(codigo);
		return categoria.isPresent() ? 
				ResponseEntity.ok(categoria.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public void remover(@PathVariable Long codigo) {
		categoriaRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Categoria> atualizar(@PathVariable Long codigo, @Valid @RequestBody Categoria categoria){
		Categoria categoriaSalva = categoriaService.atualizar(codigo, categoria);
		return ResponseEntity.ok(categoriaSalva);
	}
	
}
