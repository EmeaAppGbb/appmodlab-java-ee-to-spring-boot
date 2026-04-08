package com.medflow.rest;

import com.medflow.ejb.InventoryBean;
import com.medflow.entity.Inventory;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @EJB
    private InventoryBean inventoryBean;

    @GET
    @Path("/product/{productId}")
    public Response getByProduct(@PathParam("productId") Long productId) {
        List<Inventory> inventory = inventoryBean.findByProduct(productId);
        return Response.ok(inventory).build();
    }

    @GET
    @Path("/warehouse/{warehouseId}")
    public Response getByWarehouse(@PathParam("warehouseId") Long warehouseId) {
        List<Inventory> inventory = inventoryBean.findByWarehouse(warehouseId);
        return Response.ok(inventory).build();
    }

    @GET
    @Path("/expiring")
    public Response getExpiringItems(@QueryParam("date") String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("date parameter required (yyyy-MM-dd)").build();
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date expiryDate = format.parse(dateStr);
            List<Inventory> inventory = inventoryBean.findExpiringItems(expiryDate);
            return Response.ok(inventory).build();
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format").build();
        }
    }

    @GET
    @Path("/check-availability")
    public Response checkAvailability(@QueryParam("productId") Long productId,
                                     @QueryParam("quantity") Integer quantity) {
        if (productId == null || quantity == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("productId and quantity parameters required").build();
        }
        boolean available = inventoryBean.checkAvailability(productId, 1L, quantity);
        return Response.ok("{\"available\": " + available + "}").build();
    }

    @POST
    public Response create(Inventory inventory) {
        Inventory created = inventoryBean.create(inventory);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Inventory inventory) {
        Inventory existing = inventoryBean.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        inventory.setId(id);
        Inventory updated = inventoryBean.update(inventory);
        return Response.ok(updated).build();
    }
}
