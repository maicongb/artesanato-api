package com.suelen.artesanato.api.storage.local;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.suelen.artesanato.api.model.Foto;

public interface FotoStorage {

	public List<Foto> salvarTemporariamente(MultipartFile[] fotos);

	public byte[] recuperarFotoTemporaria(String descricao);
}
