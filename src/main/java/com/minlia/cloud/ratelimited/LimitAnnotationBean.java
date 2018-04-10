package com.minlia.cloud.ratelimited;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.RateLimiter;
import com.minlia.cloud.ratelimited.annotation.RateLimit;
import com.minlia.cloud.ratelimited.cache.RateLimiterCache;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * @author will
 */
@Slf4j
public class LimitAnnotationBean implements InitializingBean {

  private String basePackage;

  public LimitAnnotationBean(String basePackage) {
    this.basePackage = basePackage;
  }

  @Override
  public void afterPropertiesSet() throws Exception {

//    Preconditions.checkArgument(!Strings.isNullOrEmpty(basePackage),
//        "base package must not be null or empty.");

    if (!StringUtils.isEmpty(basePackage)) {

      Set<Method> methods = Sets.newHashSet();

      Reflections reflections = null;
      String[] pkgs = StringUtils.delimitedListToStringArray(basePackage, ",");
      for (String pkg : pkgs) {
        Stopwatch.createStarted();
        log.debug("Start creating limitAnnotationBean for package {}",pkg);

        reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(pkg))
            .setScanners(new MethodAnnotationsScanner()));

        methods.addAll(reflections.getMethodsAnnotatedWith(RateLimit.class));

        for (Method method : methods) {
          String methodAbsPath = getAsbMethodCacheKey(method);

          if (!Strings.isNullOrEmpty(methodAbsPath)) {
            RateLimit rateLimit = method.getAnnotation(RateLimit.class);
            double value = rateLimit.value();
            RateLimiter rateLimiter = RateLimiter.create(value);
            RateLimiterCache.put(methodAbsPath, rateLimiter);
          }
        }

        log.debug("Ending of create limitAnnotationBean for package {}",pkg);
      }
    }
  }

  public String getAsbMethodCacheKey(Method method) {
    return null == method ? null : method.getDeclaringClass().getName() + "#" + method.getName();
  }

  public String getBasePackage() {
    return basePackage;
  }

  public void setBasePackage(String basePackage) {
    this.basePackage = basePackage;
  }
}
