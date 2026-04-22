package com.smartcampus.exception;

import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException ex) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(403, "Forbidden", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
