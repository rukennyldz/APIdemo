package com.example.apidemo.controller;

import com.example.apidemo.model.Product;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    Gson gson = new Gson();

    @PostMapping("/saveProduct")
    public String saveProduct(@RequestBody Product product) {
        if (ObjectUtils.isEmpty(product.getProductId()) || ObjectUtils.isEmpty(product.getBrand())) {
            log.error("ProductId or Brand is empty or null: {}", product);
            return null;
        }
        try {
            redisTemplate.opsForHash().put("PRODUCT", product.getProductId(), product.getBrand());
            log.info("Product saved successfully: {}", product);
            return "Product saved successfully: " + gson.toJson(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage());
            return null;
        }
    }

    @PostMapping("/batchSaveProductList")
    public String saveProducts(@RequestBody List<Product> products) {
        if (CollectionUtils.isEmpty(products)) {
            log.error("Product list is empty.");
            return gson.toJson(Collections.singletonMap("error", "Product list is empty"));
        }
        List<Product> savedProducts = new ArrayList<>();
        try {
            for (Product product : products) {
                if (ObjectUtils.isEmpty(product.getProductId()) || ObjectUtils.isEmpty(product.getBrand())) {
                    log.error("ProductId or Brand is empty with given Product: {}", product);
                    continue;
                }
                redisTemplate.opsForHash().put("PRODUCT", product.getProductId(), product.getBrand());
                savedProducts.add(product);
            }
            log.info("Products saved successfully.");
            return gson.toJson(savedProducts);
        } catch (Exception e) {
            log.error("Error saving products: {}", e.getMessage());
            return gson.toJson(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable String productId) {
        try {
            Object brand = redisTemplate.opsForHash().get("PRODUCT", productId);
            if (brand == null) {
                log.warn("Product not found! Product ID: {}", productId);
                return null;
            }
            log.info("Product found! Product ID: {}, Brand: {}", productId, brand);
            return Product.builder().productId(productId).brand((String) brand).build();
        } catch (Exception e) {
            log.error("Error retrieving product: {}", e.getMessage());
            return null;
        }
    }

    @GetMapping("/getProductBrands")
    public List<Product> getProductBrands(@RequestParam List<String> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            log.warn("Product ID list is empty.");
            return Collections.emptyList();
        }
        try {
            List<Product> productBrandList = new ArrayList<>();
            for (String productId : productIds) {
                Object brand = redisTemplate.opsForHash().get("PRODUCT", productId);
                if (brand == null) {
                    log.warn("Product not found: {}", productId);
                    continue;
                }
                log.info("Product found: {} - {}", productId, brand);
                productBrandList.add(Product.builder().productId(productId).brand((String) brand).build());
            }
            return productBrandList;
        } catch (Exception e) {
            log.error("Error retrieving products: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @PutMapping("/updateProduct/{productId}")
    public String updateProduct(@PathVariable String productId, @RequestBody Product product) {
        if (ObjectUtils.isEmpty(productId) || ObjectUtils.isEmpty(product.getBrand())) {
            log.error("ProductId or Brand is empty or null: {}", product);
            return gson.toJson(Collections.singletonMap("error", "ProductId or Brand is empty"));
        }
        try {
            if (!redisTemplate.opsForHash().hasKey("PRODUCT", productId)) {
                log.warn("Product not found for update! Product ID: {}", productId);
                return gson.toJson(Collections.singletonMap("error", "Product not found for update"));
            }
            redisTemplate.opsForHash().put("PRODUCT", productId, product.getBrand());
            log.info("Product updated successfully: {}", product);
            return gson.toJson(Collections.singletonMap("message", "Product updated successfully"));
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return gson.toJson(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public String deleteProduct(@PathVariable String productId) {
        try {
            if (!redisTemplate.opsForHash().hasKey("PRODUCT", productId)) {
                log.warn("Product not found for deletion! Product ID: {}", productId);
                return gson.toJson(Collections.singletonMap("error", "Product not found for deletion"));
            }
            redisTemplate.opsForHash().delete("PRODUCT", productId);
            log.info("Product deleted successfully! Product ID: {}", productId);
            return gson.toJson(Collections.singletonMap("message", "Product deleted successfully! Product ID: " + productId));
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            return gson.toJson(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
