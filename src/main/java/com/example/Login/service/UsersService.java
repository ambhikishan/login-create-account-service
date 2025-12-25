package com.example.Login.service;

import com.example.Login.dto.LoginDetails;
import com.example.Login.pojo.Users;
import com.example.Login.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UsersService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private LoginRepo loginRepo;

    public Mono<Users> login(LoginDetails login){

        if(login.getUsername()!=null && login.getPassword()!=null) {
            if(!(login.getUsername().isEmpty()&&login.getPassword().isEmpty()))
            {
                return Mono.error(new RuntimeException("empty username and password"));
            }
        }

        return loginRepo.findByEmail(login.getEmail()).flatMap(u -> {
           if( bCryptPasswordEncoder.matches(login.getPassword(),u.getPassword()))
           {
                    return Mono.just(u);}

        else
            return Mono.error(new RuntimeException("Invalid username and password"));


    });
}

public Mono<Object> createAccount(Users user){
        return loginRepo.findByUsername(user.getUsername())
                .flatMap(u->Mono.error(new RuntimeException("username already exists")))
                .switchIfEmpty(Mono.defer(()->{
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    return loginRepo.save(user);
                }));


}



}
