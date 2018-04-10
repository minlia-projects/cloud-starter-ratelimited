package com.minlia.cloud.ratelimited.cache;

import com.minlia.cloud.ratelimited.DefaultRateLimit;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterCache {
	private static LoadingCache<String, RateLimiter> rateLimiterLoadingCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, RateLimiter>() {
				@Override
				public RateLimiter load(String key) throws Exception {
					System.out.println("key:" + key);
					return DefaultRateLimit.create();
				}
			});

	public static void put(String key, RateLimiter value) {
		rateLimiterLoadingCache.put(key, value);
	}

	public static RateLimiter get(String key) throws ExecutionException {
		return rateLimiterLoadingCache.get(key);
	}
}
