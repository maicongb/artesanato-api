package com.suelen.artesanato.api.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

import com.suelen.artesanato.api.config.property.ArtesanatoApiProperty;
import com.suelen.artesanato.api.dto.Anexo;
import com.suelen.artesanato.api.event.RecursoCriadoEvent;
import com.suelen.artesanato.api.model.Foto;
import com.suelen.artesanato.api.model.Produto;
import com.suelen.artesanato.api.repository.ProdutoRepository;
import com.suelen.artesanato.api.repository.filter.ProdutoFilter;
import com.suelen.artesanato.api.service.ImagemService;
import com.suelen.artesanato.api.service.ProdutoService;
import com.suelen.artesanato.api.service.exception.EntidadeNaoEncontradaException;
import com.suelen.artesanato.api.service.exception.ProdutoJaExistenteException;
import com.suelen.artesanato.api.storage.local.S3;

@RestController
@RequestMapping("/produtos")
public class ProdutoResource {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private S3 s3;
	
	@Autowired
	private ImagemService imagemService;
	
	@Autowired
	private ArtesanatoApiProperty property;
	
	//LISTAR PRODUTO/FOTO
	@GetMapping("/listar/produtos")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<List<Produto>> listar() {
		
		List<Produto> produto = produtoRepository.findAll();
		
		return ResponseEntity.ok().body(produto);
	}
	
	
	//TRATADO PARA TER UM RETORNO ASSINCRONO
	//MELHORA A DISPONIBILIDADE
	@PostMapping("/foto")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public Anexo uploadAnexo(@RequestParam MultipartFile foto) throws IOException {
		
		String nome = foto.getOriginalFilename();
		String contentType = foto.getContentType();
		Long tamanho = foto.getSize();
				
		BufferedImage jpgImage = imagemService.getJpgImageFromFile(foto);
				
		Anexo anexo = savarImagemReduzir(nome, contentType, tamanho, jpgImage);
		salvarImagemOriginal(anexo.getNome(), contentType, tamanho, jpgImage);
		
		
		return anexo;
	}


	private void salvarImagemOriginal(String nome, String contentType, Long tamanho, BufferedImage jpgImage) {
		s3.salvarTemporariamente(imagemService.getInputStream(jpgImage, "jpg"), nome, contentType, tamanho);
		
	}


	private Anexo savarImagemReduzir(String nome, String contentType, Long tamanho, BufferedImage jpgImage) {
		jpgImage = imagemService.cropSquare(jpgImage);
		jpgImage = imagemService.resize(jpgImage, property.getS3().getImg());
		
		
		Anexo anexo = s3.salvarTemporariamente(imagemService.getInputStream(jpgImage, "jpg"), nome, contentType, tamanho);
		return anexo;
	}
	
	
	
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Produto> buscaPeloCodigo(@PathVariable Long codigo) {
		Optional<Produto> produto = produtoRepository.findById(codigo);
		return produto.isPresent() ? ResponseEntity.ok(produto.get()) : ResponseEntity.notFound().build();
	}
	
	@GetMapping("/detalhar/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Produto> detalharBuscaPeloCodigo(@PathVariable Long codigo) {
		
		
		System.err.println("Entrou");
		
		Optional<Produto> produto = produtoRepository.findById(codigo);
		for(Foto foto : produto.get().getFoto()) {
			String nomeUnico = foto.getDescricao().substring(0,36) + "_" + "original" + "_" + foto.getDescricao().substring(37);
			foto.setDescricao(nomeUnico);
			
		}
		
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
			//problema do usuario
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public Page<Produto> pesquisar(ProdutoFilter produtoFilter, Pageable pageable) {
		return produtoRepository.filtrar(produtoFilter, pageable);
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public void remover(@PathVariable Long codigo) {
		produtoRepository.deleteById(codigo);
	}
	
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public ResponseEntity<Produto> atualizar(@PathVariable Long codigo, @Valid @RequestBody Produto produto){
		
		try {
			
			Produto produtoSalvo = produtoService.atualizar(codigo, produto);
			return ResponseEntity.ok(produtoSalvo);
			
		} catch (IllegalArgumentException e) {
			//Problema do sistema
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		produtoService.atualizarPropriedadeAtivo(codigo, ativo);
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/deletar/foto/{descricao}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
	public void removerFoto(@PathVariable("descricao") String descricao) {
		produtoService.removerFoto(descricao);
		
	}
	
}
