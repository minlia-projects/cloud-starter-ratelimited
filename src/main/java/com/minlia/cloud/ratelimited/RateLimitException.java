package com.minlia.cloud.ratelimited;

public class RateLimitException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3119416813619871276L;

	public RateLimitException() {
        super();
    }

    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimitException(Throwable cause) {
        super(cause);
    }

    protected RateLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
