package com.compartamos.resteasyjackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import com.compartamos.entites.Customer;
import com.compartamos.entites.Product;
import com.compartamos.repositories.CustomerRepository;

import jakarta.annotation.PostConstruct; 
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;

@Slf4j
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerApi {

    @Inject
    CustomerRepository pr;
    
    @Inject
    Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void initialize() {
        this.webClient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8099).setSsl(false).setTrustAll(true)); // Puerto 8099 donde está Product
    }

    @GET
    public Uni<List<Customer>> list() {
        return pr.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Customer> getById(@PathParam("id") Long id) {
        return pr.findById(id);
    }

    @GET
    @Path("/{id}/product")
    public Uni<Customer> getByIdProduct(@PathParam("id") Long id) {
        // Combinamos el Uni del cliente y el del API externa
        return Uni.combine().all().unis(pr.findById(id), getAllProducts())
                .combinedWith((customer, allProducts) -> {
                    if (customer == null) return null;
                    customer.getProducts().forEach(cp -> {
                        allProducts.stream()
                            .filter(p -> p.getId().equals(cp.getId()))
                            .findFirst()
                            .ifPresent(p -> {
                                cp.setName(p.getName());
                                cp.setDescription(p.getDescription());
                            });
                    });
                    return customer;
                });
    }

    @POST
    @WithTransaction
    public Uni<Response> add(Customer c) {
    if (c.getProducts() != null) {
        c.getProducts().forEach(p -> p.setCustomer(c));
    }
    return pr.getSession()
            .flatMap(session -> session.merge(c))
            .replaceWith(Response.ok(c).status(Response.Status.CREATED).build());
}

    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> delete(@PathParam("id") Long id) {
        return pr.deleteById(id)
                .map(res -> res ? Response.ok().build() : Response.status(404).build());
    }

    @PUT
    @WithTransaction
    public Uni<Response> update(Customer p) {
        return pr.findById(p.getId())
                .onItem().ifNotNull().transformToUni(customer -> {
                    customer.setCode(p.getCode());
                    customer.setAccountNumber(p.getAccountNumber());
                    customer.setSurname(p.getSurname());
                    customer.setPhone(p.getPhone());
                    customer.setAddress(p.getAddress());
                    return Uni.createFrom().item(Response.ok(customer).build());
                })
                .onItem().ifNull().continueWith(Response.status(404).build());
    }


    private Uni<List<Product>> getAllProducts() {
        return webClient.get(8099, "localhost", "/product").send()
                .onFailure().invoke(res -> log.error("Error recuperando productos ", res))
                .onItem().transform(res -> {
                    List<Product> lista = new ArrayList<>();
                    JsonArray objects = res.bodyAsJsonArray();
                    objects.forEach(p -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            Product product = objectMapper.readValue(p.toString(), Product.class);
                            lista.add(product);
                        } catch (Exception e) {
                            log.error("Error parseando producto", e);
                        }
                    });
                    return lista;
                });
    }
}
