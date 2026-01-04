package com.example.Login.repo;

import com.example.Login.pojo.Users;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LoginRepo extends ReactiveCrudRepository<Users,Long>{
    Mono<Users> findByEmail(String email);
    Mono<Users> findByUsername(String username);

    @Query("SELECT username FROM users WHERE username LIKE :q || '%' LIMIT 10")
    Flux<Users> searchByUsername(String q);

}
