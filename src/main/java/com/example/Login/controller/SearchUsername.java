package com.example.Login.controller;

import com.example.Login.pojo.Users;
import com.example.Login.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SearchUsername {

    @Autowired
    private LoginRepo loginRepo;

    @GetMapping("/search/username")
    public Flux<Users> searchUsername(@RequestParam String q)
    {
        return loginRepo.searchByUsername(q);
    }
}
