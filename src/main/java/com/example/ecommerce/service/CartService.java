package com.example.ecommerce.service;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public CartItem addToCart(String userId, String productId, Integer quantity) {
        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            return cartRepository.save(newItem);
        }
    }

    public List<CartItem> getCartByUserId(String userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        // Enrich with product details
        items.forEach(item -> {
            Optional<Product> product = productRepository.findById(item.getProductId());
            product.ifPresent(item::setProduct);
        });
        return items;
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
