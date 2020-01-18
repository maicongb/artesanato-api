package com.suelen.artesanato.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("artesanato")
public class ArtesanatoApiProperty {
	
	private String originPermitida = "http://localhost:4200";

	private final Seguranca seguranca = new Seguranca();
	
	public Seguranca getSeguranca() {
		return seguranca;
	}
	
	private final S3 s3 = new S3();
	
	public S3 getS3() {
		return s3;
	}
	

	//===================================================================	
	//S3 STORAGE
	public static class S3 {
		
		private String accessKeyId;
		private String secretAccessKey;
		private String bucket = "mgb-artesanato-arquivos";
		
		//RENDERIZAR IMAGEM
		private Integer img;
		
		public String getBucket() {
			return bucket;
		}
		
		public void setBucket(String bucket) {
			this.bucket = bucket;
		}
		
		public String getAccessKeyId() {
			return accessKeyId;
		}
		
		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}
		
		public String getSecretAccessKey() {
			return secretAccessKey;
		}
		
		public void setSecretAccessKey(String secretAccessKey) {
			this.secretAccessKey = secretAccessKey;
		}

		public Integer getImg() {
			return img;
		}

		public void setImg(Integer img) {
			this.img = img;
		}
		
	}
//===================================================================	


	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}
	
	public static class Seguranca {

		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
	}
//===================================================================

}
