package com.goodworkalan.litany;

/**
 * A general purpose exception that indicates that an error occurred in one 
 * of the classes in the litany package.
 *   
 * @author Alan Gutierrez
 */
public final class LitanyException extends RuntimeException
{
    /** The serial version id. */
    private static final long serialVersionUID = 20081128L;
    
    /** An error occurred while reading the CSV source. */
    public static final int ERROR_IO_READ = 101;

    /**
     * Wrap the exception given by cause in an exception with the given error
     * code.
     * 
     * @param code
     *            The error code.
     */
    public LitanyException(int code, Throwable cause)
    {
        super(null, cause);
    }
}