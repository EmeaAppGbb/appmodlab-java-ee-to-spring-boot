package com.medflow.rest;

import com.medflow.ejb.OrderProcessingBean;
import com.medflow.entity.Order;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @EJB
    private OrderProcessingBean orderProcessingBean;

    @GET
    public Response getAll() {
        List<Order> orders = orderProcessingBean.findAll();
        return Response.ok(orders).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Order order = orderProcessingBean.findById(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }

    @GET
    @Path("/customer/{customerId}")
    public Response getByCustomer(@PathParam("customerId") Long customerId) {
        List<Order> orders = orderProcessingBean.findByCustomer(customerId);
        return Response.ok(orders).build();
    }

    @GET
    @Path("/status/{status}")
    public Response getByStatus(@PathParam("status") String status) {
        List<Order> orders = orderProcessingBean.findByStatus(status);
        return Response.ok(orders).build();
    }

    @POST
    public Response create(Order order) {
        Order created = orderProcessingBean.createOrder(order);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @POST
    @Path("/{id}/process")
    public Response processOrder(@PathParam("id") Long id) {
        Order order = orderProcessingBean.processOrder(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }

    @POST
    @Path("/{id}/ship")
    public Response shipOrder(@PathParam("id") Long id) {
        Order order = orderProcessingBean.shipOrder(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }

    @POST
    @Path("/{id}/cancel")
    public Response cancelOrder(@PathParam("id") Long id) {
        Order order = orderProcessingBean.cancelOrder(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }
}
