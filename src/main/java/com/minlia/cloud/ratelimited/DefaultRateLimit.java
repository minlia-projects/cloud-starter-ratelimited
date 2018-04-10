package com.minlia.cloud.ratelimited;

import com.google.common.util.concurrent.RateLimiter;

public class DefaultRateLimit {
	private static double permits;

	public static RateLimiter create() {
		return permits == 0.0d ? RateLimiter.create(Double.MAX_VALUE) : RateLimiter.create(permits);
	}

	public static RateLimiter create(double dPermits) {
		return dPermits == 0.0d ? RateLimiter.create(Double.MAX_VALUE) : RateLimiter.create(dPermits);
	}
}
