package com.example.Login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisOtpService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);

    public void saveOtp(String email, String otp) {
        redisTemplate.opsForValue().set(email, otp, OTP_EXPIRY);


    }

    public boolean checkOtp(String email, String otp) {
        boolean otpStatus = redisTemplate.opsForValue().get(email).equals(otp);
        redisTemplate.delete(email);
        return otpStatus;
    }
}
