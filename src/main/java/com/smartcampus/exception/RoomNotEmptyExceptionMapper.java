package com.smartcampus.exception;

import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(409, "Conflict", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
