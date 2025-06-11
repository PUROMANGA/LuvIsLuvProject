package com.example.luvisluvproject.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.store.entity.Store;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByStoreId(Store store, Pageable pageable);
}
