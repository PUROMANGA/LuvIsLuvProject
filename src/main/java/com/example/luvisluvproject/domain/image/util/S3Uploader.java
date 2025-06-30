package com.example.luvisluvproject.domain.image.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	private String s3BaseUrl;

	@PostConstruct
	public void init() {
		this.s3BaseUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
	}

	/**
	 * MultipartFile을 받아 S3에 업로드하고 S3 key(path)를 반환
	 *
	 * @param file      업로드할 파일
	 * @param dirName   S3 내 저장 디렉토리 (예: store, profile)
	 * @return          S3 키 (예: store/test.jpg)
	 */

	public String upload(MultipartFile file, String dirName) {
		String originalFilename = file.getOriginalFilename();
		String extension = getExtension(originalFilename);
		String uuid = UUID.randomUUID().toString();
		String fileName = dirName + "/" + uuid + extension;

		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			metadata.setContentType(file.getContentType());

			amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
			return fileName;

		} catch (IOException e) {
			throw new CustomRuntimeException(ExceptionCode.S3_UPLOAD_FAILED);
		}
	}

	public String getFullUrl(String fileName) {
		return s3BaseUrl + fileName;
	}

	private String getExtension(String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf("."));
	}

	/**
	 * S3에 저장된 이미지 삭제
	 *
	 * @param key S3에 저장된 파일 경로 (예: store/test.jpg)
	 */
	public void delete(String key) {
		if (amazonS3.doesObjectExist(bucket, key)) {
			amazonS3.deleteObject(bucket, key);
		}
	}
}
