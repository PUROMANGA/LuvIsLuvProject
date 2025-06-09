package com.example.luvisluvproject.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.store.entity.Store;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByStoreId(Store store);

}
