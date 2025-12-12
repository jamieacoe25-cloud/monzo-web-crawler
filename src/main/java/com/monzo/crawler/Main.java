package com.monzo.crawler;

import java.time.Duration;
import java.time.Instant;

import com.monzo.crawler.config.CrawlConfig;
import com.monzo.crawler.utils.DomainFilter;
import com.monzo.crawler.utils.HtmlFetcher;
import com.monzo.crawler.utils.LinkExtractor;

/*
 * Main class
*/
public class Main {

    /*
     * Main method to instantiate classes, set timer and start crawler.
     */
    public static void main(String[] args) throws InterruptedException {
        CrawlConfig config = new CrawlConfig(10, 100, 10);
        DomainFilter filter = new DomainFilter("crawlme.monzo.com");
        HtmlFetcher fetcher = new HtmlFetcher();
        LinkExtractor extractor = new LinkExtractor();

        Instant start = Instant.now();

        Crawler crawler = new Crawler(config, fetcher, extractor, filter);
        crawler.start("https://crawlme.monzo.com/products.html");

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Execution took " + duration.toMillis() + " ms");

    }
}
