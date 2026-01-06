package com.example.Login.controller;

import com.example.Login.dto.PasswordDTO;
import com.example.Login.repo.LoginRepo;
import com.example.Login.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProfileDetails {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping(value = "/user/picture",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public Mono<ResponseEntity<Object>> uploadPicture(@RequestPart FilePart filePart, @RequestHeader("Authorization") String token){

        Path folder = Paths.get("C:\\Users\\ambhi\\OneDrive\\Pictures\\uploads");
        Path file = folder.resolve(UUID.randomUUID().toString() +filePart.filename());
        try {
            String userName = jwtService.validateAndExtract(token.substring(7)); // Bearer
            System.out.println(userName);
            System.out.println(file.toString());
            return Mono.fromCallable(() -> Files.createDirectories(folder)) // Ensure folder exists (blocking IO wrapped)
                    .subscribeOn(Schedulers.boundedElastic())// Move blocking IO to separate thread
                    .then((filePart.transferTo(file))// Then write file
                            .then(loginRepo.findByUsername(userName)
                                    .flatMap(users -> {users.setProfile_path(file.toString()); return loginRepo.save(users);}))
                            .then(Mono.just(ResponseEntity.ok().build())));
        }
        catch (RuntimeException e)
        {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

    }

    @GetMapping("/user/profile/{username}")
    public Mono<ResponseEntity<?>> getProfilePicture(@PathVariable String username)
    {
        return loginRepo.findByUsername(username).flatMap(user ->{
            String picturePath = user.getProfile_path();
            if(picturePath == null || picturePath.isEmpty())
            {
                return Mono.just(ResponseEntity.notFound().build());
            }

            File file = new File(picturePath);

            if (!file.exists()) {
                return Mono.just(ResponseEntity.notFound().build());
            }

            else {
                Resource resource = new FileSystemResource(file);
                return Mono.just(ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource));

            }

        });
    }
    @PostMapping("user/profile/change-password")
    public Mono<ResponseEntity<Map<String,?>>> changePassword(@RequestBody PasswordDTO pwd, @RequestHeader("Authorization") String token)
    {
        if(token == null || token.isEmpty() || !token.startsWith("Bearer "))
        {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success",false,"message","invalid token")));
        }
        if(pwd == null || pwd.getPassword().isEmpty() || pwd.getConfirmPassword().isEmpty() || !pwd.getPassword().equals(pwd.getConfirmPassword()))
        {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success",false,"message","password error")));
        }

        String username;
        try{
            username = jwtService.validateAndExtract(token.substring(7));

        }
        catch (Exception e)
        {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success",false,"message","jwt token expired")));
        }
        return loginRepo.findByUsername(username)
            .flatMap(user -> {
                // Always encode passwords
                user.setPassword(passwordEncoder.encode(pwd.getPassword()));
                return loginRepo.save(user);
            })
            .map(updatedUser ->
                    ResponseEntity.ok(Map.of("success",true,"message","password changed successfully")));


    }


}
