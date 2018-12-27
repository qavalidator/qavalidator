package de.qaware.qav.app.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception if requested object is not found.
 *
 * @author QAware GmbH
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)  // 404
public class NotFoundException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message the message
     */
    public NotFoundException(String message) {
        super(message);
    }
}
