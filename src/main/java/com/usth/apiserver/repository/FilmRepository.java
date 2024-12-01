package com.usth.apiserver.repository;

import com.usth.apiserver.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    //khai báo các hàm tùy chỉnh ở đây, ngoài các hàm có sẵn như findById, findAll, save, delete, count, exists,...
    //ví dụ: tìm kiếm film theo title, phim nào có title chứa từ khóa Dinosour thì trả về
    @Query(value = "SELECT film.title AS movies " +
            "FROM film " +
            "JOIN inventory ON film.film_id = inventory.film_id " +
            "JOIN store ON inventory.store_id = store.store_id " +
            "WHERE store.store_id = :storeId " +
            "AND film.title LIKE %:keyword%", nativeQuery = true)
    List<String> findFilmsByStoreAndTitleLike(@Param("storeId") Long storeId, @Param("keyword") String keyword);


}
