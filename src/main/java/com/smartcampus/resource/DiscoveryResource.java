package com.smartcampus.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> response = new HashMap<>();
        response.put("apiName", "Smart Campus API");
        response.put("version", "1.0.0");
        response.put("contact", "admin@smartcampus.ac.uk");
        response.put("timestamp", System.currentTimeMillis());

        Map<String, String> links = new HashMap<>();
        links.put("self",    "/api/v1");
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("resources", links);

        return Response.ok(response).build();
    }
}