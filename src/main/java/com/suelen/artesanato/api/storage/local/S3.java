package com.suelen.artesanato.api.storage.local;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
	
	public Anexo salvarTemporariamente(InputStream arquvio, String nome, String contentType, Long tamanho) {
		
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(contentType);
		//objectMetadata.setContentLength(tamanho);
		
		String nomeUnico = gerarNomeUnico(nome);
		String url = configurarUrl(nomeUnico);
		
		PutObjectRequest putObjectRequest = new PutObjectRequest(
				property.getS3().getBucket(),
				nomeUnico,
				arquvio,
				objectMetadata)
				.withAccessControlList(acl);
		
		//TEMPO DE EXPIRAR O ARQUIVO 1 DIA
		putObjectRequest.setTagging(new ObjectTagging(
				Arrays.asList(new Tag("expirar", "true"))));
		
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", 
						nome);
			}
			
			return new Anexo(nomeUnico, contentType, tamanho, url);
	}

	
	//SALVAR PERMANENTEMENTE NO S3
	public void salvar(String objeto) {
		
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto, 
				new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}
	
	//REMOVER
	public void remover(String objeto) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), objeto);
		
		amazonS3.deleteObject(deleteObjectRequest);
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
