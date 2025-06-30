package com.example.luvisluvproject.domain.image.service;

import com.example.luvisluvproject.domain.image.entity.Image;
import com.example.luvisluvproject.domain.image.repository.ImageRepository;
import com.example.luvisluvproject.domain.image.util.S3Uploader;
import com.example.luvisluvproject.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final S3Uploader s3Uploader;
	private final ImageRepository imageRepository;

	/**
	 * 가게 이미지들을 S3에 업로드 & DB에 저장
	 *
	 * @param store     연결할 Store 엔티티
	 * @param images    업로드할 이미지들
	 * @return 저장된 Image 목록
	 */
	public List<Image> uploadImages(Store store, List<MultipartFile> images) {
		return images.stream().map(file -> {
			String path = s3Uploader.upload(file, "store");
			Image image = Image.builder()
				.imagePath(path)
				.originalName(file.getOriginalFilename())
				.store(store)
				.build();
			return imageRepository.save(image);
		}).collect(Collectors.toList());
	}

	/**
	 * 주어진 가게(Store)와 연결된 이미지 삭제
	 * - S3에서도 삭제
	 * - DB에서도 삭제
	 *
	 * @param store 이미지 삭제 대상 가게
	 */
	public void deleteImagesByStore(Store store) {
		// 1. 가게에 연결된 모든 이미지 조회
		List<Image> images = imageRepository.findByStore(store);

		// 2. S3에서 삭제 + DB에서 삭제
		for (Image image : images) {
			s3Uploader.delete(image.getImagePath());     // S3 삭제
			imageRepository.delete(image);               // DB 삭제
		}
	}

}
