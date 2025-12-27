package com.example.Login.controller;

import com.example.Login.dto.LoginDetails;
import com.example.Login.dto.OTPDTO;
import com.example.Login.pojo.Users;
import com.example.Login.repo.LoginRepo;
import com.example.Login.service.RedisOtpService;
import com.example.Login.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class LoginController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private LoginRepo loginRepo;
    @Autowired
    private WebClient webClient;

    @Autowired
    private RedisOtpService redisOtpService;

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

    @GetMapping("/verify")
public Mono<ResponseEntity<String>> verfifyAccount(@RequestParam String email) {
      return  loginRepo.findByEmail(email).flatMap(users -> {
          if(users.isVerified()==true) {
              return Mono.error(new RuntimeException("User is already verified"));
          }
         String otp = usersService.generateOTP();
            redisOtpService.saveOtp(email,otp);
            OTPDTO  otpDTO = new OTPDTO();
            otpDTO.setOtp(otp);
            otpDTO.setEmail(users.getEmail());

       return webClient.post().uri("/send-otp")
                .bodyValue(otpDTO)
                .retrieve()
                .bodyToMono(String.class)
          .map(emailResponse -> ResponseEntity.ok("OTP sent successfully"));

        }).switchIfEmpty(Mono.just(ResponseEntity.badRequest().body("User not found")));

    }

    @PostMapping("/verify")
    public Mono<ResponseEntity<?>> verifyAccount(@RequestBody OTPDTO otpDTO)
    {
       return loginRepo.findByEmail(otpDTO.getEmail()).flatMap( users -> {
            if(users.isVerified()==true) {
                return Mono.error(new RuntimeException("User is already verified"));
            }
            if(redisOtpService.checkOtp(otpDTO.getEmail(), otpDTO.getOtp())) {
                users.setVerified(true);
                return loginRepo.save(users).map(e ->
                 Mono.just(ResponseEntity.ok("User email verified successfully")));
            }
            return  Mono.just(ResponseEntity.badRequest().body("incorrect otp"));
        }).switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
               .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Internal server error"))).map(ResponseEntity::ok);

    }
    }



