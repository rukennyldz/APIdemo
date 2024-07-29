package com.example.apidemo.repository;

import com.example.apidemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {

    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // HSET operations
    public void saveProduct(String key, Product product) {
        redisTemplate.opsForHash().put(key, product.getProductId(), product);
    }

    public void saveProduct(String key, Product product, long ttl) {
        try {
            redisTemplate.opsForHash().put(key, product.getProductId(), product);
            redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Redis exception occurred while saving product with TTL: key: {}, productId: {}, message: {}", key, product.getProductId(), e.getMessage());
        }
    }

    public Product getProduct(String key, String productId) {
        try {
            Object result = redisTemplate.opsForHash().get(key, productId);
            if (result instanceof Product) {
                return (Product) result;
            } else {
                log.warn("Product not found or type mismatch for key: {} and productId: {}", key, productId);
                return null;
            }
        } catch (Exception e) {
            log.error("Redis exception occurred while retrieving product: key: {}, productId: {}, message: {}", key, productId, e.getMessage());
            return null;
        }
    }

    // ZADD operations
    public void saveProductToSortedSet(String key, Product product) {
        redisTemplate.opsForZSet().add(key, product, product.getPrice());
    }

    public void saveProductToSortedSet(String key, Product product, long ttl) {
        try {
            redisTemplate.opsForZSet().add(key, product, product.getPrice());
            redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Redis exception occurred while saving product to sorted set with TTL: key: {}, productId: {}, message: {}", key, product.getProductId(), e.getMessage());
        }
    }

    public Set<Object> getProductsFromSortedSet(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }
}
