package com.recalot.common.exceptions;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class BaseException extends Exception{
    private String message = null;

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(String format, String arg) {
        super(String.format(format, arg));
        this.message = String.format(format, arg);
    }

    public BaseException(String format, String arg1, String arg2) {
        super(String.format(format, arg1, arg2));
        this.message = String.format(format, arg1, arg2);
    }

    public BaseException(String format, String arg1, String arg2, String arg3) {
        super(String.format(format, arg1, arg2, arg3));
        this.message = String.format(format, arg1, arg2, arg3);
    }

    public BaseException(String format, String arg1, String arg2, String arg3, String arg4) {
        super(String.format(format, arg1, arg2, arg3, arg4));
        this.message = String.format(format, arg1, arg2, arg3, arg4);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
