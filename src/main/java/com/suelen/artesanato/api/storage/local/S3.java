package com.suelen.artesanato.api.storage.local;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.suelen.artesanato.api.config.property.ArtesanatoApiProperty;
import com.suelen.artesanato.api.dto.Anexo;

@Component
public class S3 {

	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private ArtesanatoApiProperty property;
	
	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	public Anexo salvarTemporariamente(MultipartFile arquivo) {
		
		
		System.err.println(arquivo.getSize());
		
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());
		
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		String contentType = arquivo.getContentType();
		Long tamanho = arquivo.getSize();
		String url = configurarUrl(nomeUnico);
		
		//ENVIAR ARQUIVO PARA O S3
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(),
					nomeUnico,
					arquivo.getInputStream(),
					objectMetadata)
					.withAccessControlList(acl);
			
			//TEMPO DE EXPIRAR O ARQUIVO 1 DIA
			putObjectRequest.setTagging(new ObjectTagging(
					Arrays.asList(new Tag("expirar", "true"))));
			
				amazonS3.putObject(putObjectRequest);
				
				if (logger.isDebugEnabled()) {
					logger.debug("Arquivo {} enviado com sucesso para o S3.", 
							arquivo.getOriginalFilename());
				}
				
				return new Anexo(nomeUnico, contentType, tamanho, url);
				
				
		} catch (IOException e) {
			throw new RuntimeException("Problema para enviar o arquivo para o S3", e);
		}
	}

	
	//SALVAR PERMANENTEMENTE NO S3
	public void salvar(String objeto) {
		
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto, 
				new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}
	
	//RETORNAR A URL
	public String configurarUrl(String objeto) {
		// AS BARRAS \\ SERVEM PARA PROTOCOLO HTTP OU HTTPS
		return "https:" + property.getS3().getBucket() +
				".s3.amazonaws.com/" + objeto;
	}

	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}


}
