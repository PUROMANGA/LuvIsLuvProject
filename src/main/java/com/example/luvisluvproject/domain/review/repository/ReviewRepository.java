package com.example.luvisluvproject.domain.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Slice<Review> findByStoreId(Long storeId, Pageable pageable);
}
