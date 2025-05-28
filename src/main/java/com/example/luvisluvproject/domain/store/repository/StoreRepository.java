package com.example.luvisluvproject.domain.store.repository;

import com.example.luvisluvproject.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Store 엔티티에 대한 데이터베이스 접근을 처리하는 JPA Repository 인터페이스
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 메서드를 제공
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
	// 추후 GPS 기반 필터링 또는 커스텀 쿼리는 여기에 추가
	// pr 템플릿테스트 푸쉬
}