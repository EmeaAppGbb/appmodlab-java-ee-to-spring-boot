package com.medflow.rest;

import com.medflow.ejb.ProductCatalogBean;
import com.medflow.ejb.CacheManagerBean;
import com.medflow.entity.Product;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @EJB
    private ProductCatalogBean productCatalogBean;

    @EJB
    private CacheManagerBean cacheManagerBean;

    @GET
    public Response getAll() {
        List<Product> products = productCatalogBean.findAll();
        return Response.ok(products).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Product product = cacheManagerBean.getProductById(id);
        if (product == null) {
            product = productCatalogBean.findById(id);
        }
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(product).build();
    }

    @GET
    @Path("/sku/{sku}")
    public Response getBySku(@PathParam("sku") String sku) {
        Product product = cacheManagerBean.getProductBySku(sku);
        if (product == null) {
            product = productCatalogBean.findBySku(sku);
        }
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(product).build();
    }

    @GET
    @Path("/category/{category}")
    public Response getByCategory(@PathParam("category") String category) {
        List<Product> products = productCatalogBean.findByCategory(category);
        return Response.ok(products).build();
    }

    @GET
    @Path("/search")
    public Response search(@QueryParam("name") String name) {
        if (name == null || name.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("name parameter required").build();
        }
        List<Product> products = productCatalogBean.searchByName(name);
        return Response.ok(products).build();
    }

    @POST
    public Response create(Product product) {
        Product created = productCatalogBean.create(product);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Product product) {
        Product existing = productCatalogBean.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        product.setId(id);
        Product updated = productCatalogBean.update(product);
        cacheManagerBean.evict(id);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Product existing = productCatalogBean.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        productCatalogBean.delete(id);
        cacheManagerBean.evict(id);
        return Response.noContent().build();
    }
}
