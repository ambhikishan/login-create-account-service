package com.example.Login.controller;

import com.example.Login.dto.LoginDetails;
import com.example.Login.pojo.Users;
import com.example.Login.repo.LoginRepo;
import com.example.Login.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class LoginController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private LoginRepo loginRepo;

    @GetMapping("/")
    public Flux<Users> helloWorld()
    {
        Mono.just(ResponseEntity.ok("Hello World"));

        return loginRepo.findAll();
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginDetails>> loginUser(@RequestBody LoginDetails user) {
        LoginDetails loginDetails = new LoginDetails();

            Mono<ResponseEntity<LoginDetails>> returnedUser = usersService.login(user).map(u1 -> {
                loginDetails.setEmail(u1.getEmail());
                loginDetails.setPassword(null);
                loginDetails.setLoggedIn(true);
                loginDetails.setId(u1.getId());
                return ResponseEntity.ok(loginDetails);
            })
                    .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()))
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));




    return  returnedUser;
    }

    @PostMapping("create-account")
    public Mono<Object> createAccount(@RequestBody Users user) {

        return usersService.createAccount(user).onErrorResume((e -> Mono.just(ResponseEntity.status(404).build())));
    }

    }



