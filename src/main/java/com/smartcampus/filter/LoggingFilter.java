package com.smartcampus.filter;

import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.*;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        LOGGER.info(String.format("--> %s %s",
                req.getMethod(),
                req.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        LOGGER.info(String.format("<-- %d | %s %s",
                res.getStatus(),
                req.getMethod(),
                req.getUriInfo().getRequestUri()));
    }
}
