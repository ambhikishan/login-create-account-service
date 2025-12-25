package com.example.Login.repo;

import com.example.Login.pojo.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LoginRepo extends ReactiveCrudRepository<Users,Long>{
    Mono<Users> findByEmail(String email);
    Mono<Users> findByUsername(String username);

}
