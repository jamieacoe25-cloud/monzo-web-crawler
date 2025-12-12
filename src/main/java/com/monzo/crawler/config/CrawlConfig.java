package com.monzo.crawler.config;

/**
 * Configuration for crawler.
 */
public class CrawlConfig {

    private int maxDepth;
    private int timeoutInMs;
    private int threads = 8;
    private int maxQueueSize;

    public CrawlConfig(int maxDepth, int timeoutInMs, int maxQueueSize) {
        this.maxDepth = maxDepth;
        this.timeoutInMs = timeoutInMs;
        this.maxQueueSize = maxQueueSize;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getTimeoutInMS() {
        return timeoutInMs;
    }

    public int getThreads() {
        return threads;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
