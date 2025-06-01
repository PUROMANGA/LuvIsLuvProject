package com.example.luvisluvproject.domain.store.repository;

import com.example.luvisluvproject.domain.store.entity.Store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Store 엔티티에 대한 데이터베이스 접근을 처리하는 JPA Repository 인터페이스
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 메서드를 제공
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
	/**
	 * 주어진 좌표(lat, lng)를 기준으로 반경 2km 이내에 위치한 가게 목록 조회
	 *
	 * @param lat 사용자의 위도
	 * @param lng 사용자의 경도
	 * @return 반경 2km 이내 가게 리스트
	 */
	@Query(value = """
		SELECT *
		FROM store
		WHERE
		    (6371 * acos(
		        cos(radians(:lat)) * cos(radians(latitude)) *
		        cos(radians(longitude) - radians(:lng)) +
		        sin(radians(:lat)) * sin(radians(latitude))
		    )) <= 2
		""", nativeQuery = true)
	List<Store> findStoresWithin2km(@Param("lat") double lat, @Param("lng") double lng);
}