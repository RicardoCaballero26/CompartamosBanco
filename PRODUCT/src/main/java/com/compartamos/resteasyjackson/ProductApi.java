package com.compartamos.resteasyjackson;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.compartamos.entites.Product;
import com.compartamos.repositories.ProductRepository;

import io.smallrye.mutiny.Uni;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import java.util.List;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductApi {

    @Inject
    ProductRepository pr;

    @GET
    public Uni<List<Product>> list() {
        return pr.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Product> getById(@PathParam("id") Long id) {
        return pr.findById(id);
    }

    @POST
    @WithTransaction 
    public Uni<Response> add(Product p) {
        return pr.persist(p)
                .replaceWith(Response.ok(p).status(Response.Status.CREATED).build());
    }

    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> delete(@PathParam("id") Long id) {
        return pr.deleteById(id)
                .map(deleted -> deleted 
                    ? Response.ok().build() 
                    : Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @WithTransaction
    public Uni<Response> update(Product p) {
        return pr.findById(p.getId())
                .onItem().ifNotNull().transformToUni(product -> {
                    product.setCode(p.getCode());
                    product.setName(p.getName());
                    product.setDescription(p.getDescription());
                    return Uni.createFrom().item(Response.ok(product).build());
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }
}