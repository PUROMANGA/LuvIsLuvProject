package com.example.luvisluvproject.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.store.entity.Store;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByStoreId(Store store, Pageable pageable);
	// 평균 평점 조회
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.storeId = :store")
	Double findAverageRatingByStore(@Param("store") Store store);
}
