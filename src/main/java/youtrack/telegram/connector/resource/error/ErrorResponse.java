package youtrack.telegram.connector.resource.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private List<Error> errors = new ArrayList<>();

    public ErrorResponse() {
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

}
