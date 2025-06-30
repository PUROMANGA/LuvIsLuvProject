package com.example.luvisluvproject.domain.image.repository;

import java.util.List;

import com.example.luvisluvproject.domain.image.entity.Image;
import com.example.luvisluvproject.domain.store.entity.Store;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findByStore(Store store);
}
