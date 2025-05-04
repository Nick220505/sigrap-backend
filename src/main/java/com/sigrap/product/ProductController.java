package com.sigrap.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public List<ProductInfo> findAll() {
    return productService.findAll();
  }

  @GetMapping("/{id}")
  public ProductInfo findById(@PathVariable Integer id) {
    return productService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductInfo create(@Valid @RequestBody ProductData productData) {
    return productService.create(productData);
  }

  @PutMapping("/{id}")
  public ProductInfo update(@PathVariable Integer id, @Valid @RequestBody ProductData productData) {
    return productService.update(id, productData);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Integer id) {
    productService.delete(id);
  }

  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllById(@RequestBody List<Integer> ids) {
    productService.deleteAllById(ids);
  }
}