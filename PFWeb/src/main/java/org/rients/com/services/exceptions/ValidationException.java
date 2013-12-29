/**
 *
 */
package org.rients.com.services.exceptions;


/**
 * @author Rients
 *
 */
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 4709690209114641236L;

    /**
     * Creates a new <code>FrameworkException</code> instance.
     *
     * @param message
     *            the error description
     */
    public ValidationException(final String message) {
        super(message);
    }
}
