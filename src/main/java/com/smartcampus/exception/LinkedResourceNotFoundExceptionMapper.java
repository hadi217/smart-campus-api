package com.smartcampus.exception;

import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        return Response.status(422)
                .entity(new ErrorResponse(422, "Unprocessable Entity", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
