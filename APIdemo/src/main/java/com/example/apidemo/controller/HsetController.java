package com.example.apidemo.controller;

import com.example.apidemo.model.Product;
import com.example.apidemo.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hset")
public class HsetController {

    @Autowired
    private RedisRepository redisRepository;

    @PostMapping("/save")
    public void saveProduct(@RequestParam String key, @RequestParam(required = false) Long ttl, @RequestBody Product product) {
        if (ttl != null) {
            redisRepository.saveProduct(key, product, ttl);
        } else {
            redisRepository.saveProduct(key, product);
        }
    }

    @GetMapping("/get")
    public Product getProduct(@RequestParam String key, @RequestParam String productId) {
        return redisRepository.getProduct(key, productId);
    }
}
