package com.example.apidemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void hset(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }


    public void zadd(String key, double score, String member) {
        redisTemplate.opsForZSet().add(key, member, score);
    }
}
