package com.minlia.cloud.ratelimited;

import com.minlia.cloud.exception.ApiException;
import com.minlia.cloud.ratelimited.annotation.RateLimit;
import com.minlia.cloud.stateful.code.ApiCode;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.util.concurrent.RateLimiter;

import com.minlia.cloud.ratelimited.cache.RateLimiterCache;

/**
 * @author will
 */
@Slf4j
public class RateLimitInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean b = true;
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			if (method.isAnnotationPresent(RateLimit.class)) {
				RateLimit rateLimit = method.getAnnotation(RateLimit.class);
				double value = rateLimit.value();
				log.debug("rateLimit value:" + value);
				String methodAbsKey = getAsbMethodCacheKey(method);
				RateLimiter rateLimiter = null;
				try {
					rateLimiter = RateLimiterCache.get(methodAbsKey);
					log.debug("rateLimiter/methodAbsKey:" + rateLimiter + "," + methodAbsKey);
				} catch (Exception e) {
					rateLimiter = DefaultRateLimit.create(value);
					RateLimiterCache.put(methodAbsKey, rateLimiter);
					log.debug("rateLimiter/methodAbsKey/value:" + rateLimiter + "," + methodAbsKey + "," + value);
				}
				
				if (null == rateLimiter) {
					b = true;
				}
				b = rateLimiter.tryAcquire();
			}
		}
		log.debug("limit token is acquired:" + b);
		if(!b) {
			throw new RateLimitException("limit token cannot be acquired.");
//			throw new ApiException(ApiCode.);
		}
		return b;
	}

	public String getAsbMethodCacheKey(Method method) {
		return null == method ? null : method.getDeclaringClass().getName() + "#" + method.getName();
	}
}
