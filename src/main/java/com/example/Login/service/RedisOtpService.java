package com.example.Login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisOtpService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);

    public void saveOtp(String email, String otp) {
        otp =bCryptPasswordEncoder.encode(otp);
        redisTemplate.opsForValue().set(email, otp, OTP_EXPIRY);


    }

    public boolean checkOtp(String email, String otp) {
        boolean otpStatus = bCryptPasswordEncoder.matches(otp,redisTemplate.opsForValue().get(email));
        redisTemplate.delete(email);
        return otpStatus;
    }
}
