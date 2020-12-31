package youtrack.telegram.connector.resource.provider;

import org.apache.log4j.Logger;
import youtrack.telegram.connector.resource.error.Error;
import youtrack.telegram.connector.resource.error.ErrorCodes;
import youtrack.telegram.connector.resource.error.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;
import java.util.ResourceBundle;

@Provider
public class ExceptionsMapper implements ExceptionMapper<Exception> {

    private final Logger LOG = Logger.getLogger(ExceptionsMapper.class);

    @Override
    public Response toResponse(Exception e) {
        LOG.error("Exception occurred while processing the request.", e);
        ErrorResponse error = new ErrorResponse();
        error.getErrors().add(
                new Error(
                        ErrorCodes.ERROR,
                        ResourceBundle.getBundle("i18n/message", Locale.getDefault()).getString("internalServerError"),
                        e.getMessage()
                ));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }

}
