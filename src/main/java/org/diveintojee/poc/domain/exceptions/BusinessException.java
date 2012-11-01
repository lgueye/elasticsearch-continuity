/**
 *
 */
package org.diveintojee.poc.domain.exceptions;

/**
 * @author louis.gueye@gmail.com
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String messageCode;
    private Object[] messageArgs;
    private String defaultMessage;
    private Throwable cause;

    public BusinessException(final String message) {
        super(message);
    }

    /**
     * @param messageCode
     * @param messageArgs
     * @param defaultMessage
     */
    public BusinessException(final String messageCode, final Object[] messageArgs, final String defaultMessage) {
        setMessageCode(messageCode);
        setMessageArgs(messageArgs);
        setDefaultMessage(defaultMessage);
    }

    /**
     * @param messageCode
     * @param messageArgs
     * @param defaultMessage
     * @param cause
     */
    public BusinessException(final String messageCode, final Object[] messageArgs, final String defaultMessage,
                             final Throwable cause) {
        this(messageCode, messageArgs, defaultMessage);
        setCause(cause);
    }

    /**
     * @return the cause
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * @return the defaultMessage
     */
    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    /**
     * @return the messageArgs
     */
    public Object[] getMessageArgs() {
        return this.messageArgs;
    }

    /**
     * @return the messageCode
     */
    public String getMessageCode() {
        return this.messageCode;
    }

    /**
     * @param cause the cause to set
     */
    private void setCause(final Throwable cause) {
        this.cause = cause;
    }

    /**
     * @param defaultMessage the defaultMessage to set
     */
    private void setDefaultMessage(final String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    /**
     * @param messageArgs the messageArgs to set
     */
    private void setMessageArgs(final Object[] messageArgs) {
        this.messageArgs = messageArgs;
    }

    /**
     * @param messageCode the messageCode to set
     */
    private void setMessageCode(final String messageCode) {
        this.messageCode = messageCode;
    }
}
