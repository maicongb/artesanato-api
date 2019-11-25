package com.suelen.artesanato.api.storage.local;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.suelen.artesanato.api.dto.FotoDTO;
import com.suelen.artesanato.api.event.ProdutoSalvoEvent;
import com.suelen.artesanato.api.model.Foto;


public class FotoStorageLocal implements FotoStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	
	private Path local;
	private Path localTemporario;
	
	
	public FotoStorageLocal() {
		//resultado ---->>>>> C:/brewerFotos
		this.local = FileSystems.getDefault().getPath(System.getenv("HOMEDRIVE"), "/produtosFotos");
		criarPastas();
	}

	@Override
	public List<Foto>  salvarTemporariamente(MultipartFile[] fotos) {
		String novoNome = null;
		String contentType; 
		BigInteger tamanho;
		String url;
		
		List<Foto> fotoSalvaTempararia = new ArrayList<>();
		
		
		if(fotos != null) {
			
			for (int i = 0; i < fotos.length; i++) {
				
				MultipartFile arquivo = fotos[i];

				novoNome = renomearArquivo(arquivo.getOriginalFilename());
				//novoNome = arquivo.getOriginalFilename();
				contentType = fotos[i].getContentType();
				tamanho = BigInteger.valueOf(fotos[i].getSize());
				url = "http://localhost:4200/produtosFotos/temp/" + novoNome;;
				
				System.err.println(tamanho);
				
				try {
					
					arquivo.transferTo(new File(this.localTemporario.toAbsolutePath().toString() + FileSystems.getDefault().getSeparator() + novoNome));
					fotoSalvaTempararia.add(new FotoDTO(null, novoNome, contentType, tamanho, url));
				
				} catch (IOException e) {
					throw new RuntimeException("Erro salvando a foto na pasta temporária", e);
				}
				
			}
			
		
		}
		
		return fotoSalvaTempararia;
		
	}
	
	@Override
	public byte[] recuperarFotoTemporaria(String descricao) {
		
		try {
			return Files.readAllBytes(this.localTemporario.resolve(descricao));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto na pasta temporária", e);
		}
	}
	
	//SALVAR PERMANENTEMENT UMA FOTO
	@Override
	public void salvar(ProdutoSalvoEvent produtoSalvoEvent) {
		
		for(int i= 0; i < produtoSalvoEvent.getProduto().getFoto().size(); i++) {
		
			//Files.move(this.localTemporario.);
		}	
		
	}

	//CRIAR PASTAS LOCAL E TEMPORARIA
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			this.localTemporario =  FileSystems.getDefault().getPath(this.local.toString(), "temp");
			Files.createDirectories(this.localTemporario);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Pastas criadas para salvar fotos.");
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
				logger.debug("Pasta temporária: " + this.localTemporario.toAbsolutePath());
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao cria pasta para salvar foto");
		}
		
	}

	private String renomearArquivo(String nomeOriginal) {
		String novoNome = UUID.randomUUID().toString() + "-" + nomeOriginal;
		return novoNome;
	}

}
