package com.compartamos.resteasyjackson;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.compartamos.entites.Product;
import com.compartamos.repositories.ProductRepository;


import java.util.List;


@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductApi {
 @Inject
ProductRepository pr;

    @GET
    public List<Product> list() {
        return pr.listProduct();
    }

    @GET
    @Path("/{Id}")
    public Product getById(@PathParam("Id") Long Id) {
        return pr.findProduct(Id);
    }

    @POST
    public Response add(Product p) {
        pr.createdProduct(p);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{Id}")
    public Response delete(@PathParam("Id") Long Id) {
         pr.deleteProduct( pr.findProduct(Id));
        return Response.ok().build();
    }
    @PUT
    public Response update(Product p) {
        Product product = pr.findProduct(p.getId());
        product.setCode(p.getCode());
        product.setName(p.getName());
        product.setDescription(p.getDescription());
        pr.updateProduct(product);
        return Response.ok().build();
    }


}
