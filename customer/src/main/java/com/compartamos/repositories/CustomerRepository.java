package com.compartamos.repositories;

import com.compartamos.entites.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CustomerRepository {
    @Inject
    EntityManager em;

    @Transactional
    public void createdCustomer(Customer p){
        em.persist(p);
    }

    @Transactional
    public void deleteCustomer(Customer p){
        em.remove(em.contains(p) ? p : em.merge(p));
    }

    @Transactional
    public List<Customer> listCustomer(){
        List<Customer> customers = em.createQuery("select p from Customer p").getResultList();
        return customers;
    }
    @Transactional
    public Customer findCustomer(Long Id){
        return em.find(Customer.class, Id);
    }
    @Transactional
    public void updateCustomer(Customer p){
        em.merge(p);
    }
}
