package com.example.Login.service;

import com.example.Login.dto.LoginDetails;
import com.example.Login.dto.OTPDTO;
import com.example.Login.pojo.Users;
import com.example.Login.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Service
public class UsersService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private WebClient webClient;

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
                    user.setVerified(false);
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    return loginRepo.save(user).flatMap(users ->
                    {       OTPDTO otpDto = new OTPDTO();
                        otpDto.setEmail(user.getEmail());
                            return  webClient.post().uri("/account-created").bodyValue(otpDto).retrieve().bodyToMono(String.class);})
                ;}));


}
public String generateOTP()
{
    String numbers = "0123456789";

    // 2. Use SecureRandom for cryptographic strength
    SecureRandom random = new SecureRandom();

    // 3. Use StringBuilder for efficient string manipulation
    StringBuilder otp = new StringBuilder(6);

    for (int i = 0; i < 6; i++) {
        // 4. Get a random index from the numbers string
        int index = random.nextInt(numbers.length());

        // 5. Append the character at that index
        otp.append(numbers.charAt(index));
    }

    return otp.toString();

}




}
