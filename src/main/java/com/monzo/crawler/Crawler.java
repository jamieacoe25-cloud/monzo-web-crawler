package com.monzo.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.monzo.crawler.config.CrawlConfig;
import com.monzo.crawler.utils.DomainFilter;
import com.monzo.crawler.utils.HtmlFetcher;
import com.monzo.crawler.utils.LinkExtractor;

/**
 * Main Crawler class.
 */
public class Crawler {
    private final List<String> crawledLog;
    private final CrawlConfig config;
    private final ExecutorService executor;
    private final Set<String> visited = ConcurrentHashMap.newKeySet();
    private final HtmlFetcher fetcher;
    private final LinkExtractor extractor;
    private final DomainFilter filter;
    private final AtomicInteger activeTasks;
    private final BlockingQueue<UrlDepth> urlQueue;

    /*
     * Constructor.
     */
    public Crawler(CrawlConfig config, HtmlFetcher fetcher, LinkExtractor extractor, DomainFilter filter) {
        this.config = config;
        this.filter = filter;
        this.fetcher = fetcher;
        this.extractor = extractor;
        this.executor = Executors.newFixedThreadPool(config.getThreads());
        this.crawledLog = new ArrayList<>();
        this.activeTasks = new AtomicInteger(0);
        this.urlQueue = new LinkedBlockingQueue<UrlDepth>(config.getMaxQueueSize());
    }

    /*
     * Start the crawl.
     */
    public void start(String startUrl) throws InterruptedException {

        crawl(startUrl, 0);

        while (activeTasks.get() > 0) {
            UrlDepth urlDepth = urlQueue.poll(config.getTimeoutInMS(), TimeUnit.MILLISECONDS);
            if (urlDepth != null) {
                crawl(urlDepth.url, urlDepth.depth);
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("crawl finished, total URLs Visited: " + visited.size());
        compileCrawlLog();
        writeLogToFile();

    }

    /*
     * Crawl to setup new tasks.
     */
    public void crawl(final String url, final int depth) {
        if (!visited.add(url)) {
            return;
        }

        activeTasks.incrementAndGet();
        executor.submit(() -> {
            try {
                new CrawlTask(url, depth, this).run();
            } finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    /*
     * Mark URL as visited.
     */
    public void markVisited(String url) {
        System.out.println("Marking URL as visited: " + url);
        visited.add(url);
    }

    /**
     * check if URL has been visited.
     */
    public boolean hasBeenVisited(String url) {
        var visit = visited.contains(url);
        System.out.println("Has URL been visited (" + url + "): " + visit);
        return visit;
    }

    /**
     * Compile list of visited URL to a crawl log.
     * 
     */
    public void compileCrawlLog() {
        crawledLog.addAll(visited.stream().sorted().filter(visit -> filter.isAllowed(visit)).toList());
    }

    /**
     * Write Crawl log to file.
     * 
     */
    private void writeLogToFile() {
        try {
            System.out.println("Writing crawl log to crawler_output.txt");
            java.nio.file.Files.write(java.nio.file.Path.of("crawler_output.txt"), crawledLog);
        } catch (Exception e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    //
    private static class UrlDepth {
        final String url;
        final int depth;

        private UrlDepth(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }
    }

    public CrawlConfig getConfig() {
        return config;
    }

    public DomainFilter getDomainFilter() {
        return filter;
    }

    public HtmlFetcher getFetcher() {
        return fetcher;
    }

    public LinkExtractor getLinkExtractor() {
        return extractor;
    }

    // for testing
    public List<String> getCrawlLog() {
        return crawledLog;
    }

    public Set<String> getVisited() {
        return visited;
    }
}
