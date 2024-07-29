package com.example.apidemo.controller;

import com.example.apidemo.model.Product;
import com.example.apidemo.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/zadd")
public class ZaddController {

    @Autowired
    private RedisRepository redisRepository;

    @PostMapping("/save")
    public void saveProductToSortedSet(@RequestParam String key, @RequestParam(required = false) Long ttl, @RequestBody Product product) {
        if (ttl != null) {
            redisRepository.saveProductToSortedSet(key, product, ttl);
        } else {
            redisRepository.saveProductToSortedSet(key, product);
        }
    }

    @GetMapping("/range")
    public Set<Object> getProductsFromSortedSet(@RequestParam String key, @RequestParam double min, @RequestParam double max) {
        return redisRepository.getProductsFromSortedSet(key, min, max);
    }
}
