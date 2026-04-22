package com.smartcampus.exception;

import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.util.logging.*;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            Response r = ((WebApplicationException) throwable).getResponse();
            if (r.getEntity() != null) return r;
        }
        LOGGER.log(Level.SEVERE, "Unexpected error: " + throwable.getMessage(), throwable);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(500, "Internal Server Error",
                        "An unexpected error occurred. Please contact the administrator."))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
