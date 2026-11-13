package com.hookflow.config;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RetryConfig {
    public static final int MAX_ATTEMPTS = 5;
    public static final long INITIAL_DELAY_SECONDS = 5;
    public static final long MAX_DELAY_SECONDS = 300;
    public long calculateRetryDelaySeconds(int attemptCount) {
        long delay = INITIAL_DELAY_SECONDS * (1L << (attemptCount - 1));
        return Math.min(delay, MAX_DELAY_SECONDS);
    }
}
