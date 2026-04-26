package com.compartamos.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import com.compartamos.entites.Product;


import java.util.List;

@ApplicationScoped
public class ProductRepository {
    @Inject
    EntityManager em;

    @Transactional
    public void createdProduct(Product p){
        em.persist(p);
    }

    @Transactional
    public void deleteProduct(Product p){
        em.remove(em.contains(p) ? p : em.merge(p));
    }

    @Transactional
    public List<Product> listProduct(){
        List<Product> products = em.createQuery("select p from Product p").getResultList();
        return products;
    }
    @Transactional
    public Product findProduct(Long Id){
        return em.find(Product.class, Id);
    }
    @Transactional
    public void updateProduct(Product p){
        em.merge(p);
    }
}
