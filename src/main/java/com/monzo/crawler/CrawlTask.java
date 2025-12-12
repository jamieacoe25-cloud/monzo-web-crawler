package com.monzo.crawler;

import java.util.List;

import org.jsoup.nodes.Document;

/**
 * CrawlTask Runnable.
 */
public class CrawlTask implements Runnable {
    private final String url;
    private final int depth;
    private final Crawler crawler;

    /*
     * Constructor.
     */
    public CrawlTask(String url, int depth, Crawler crawler) {
        this.url = url;
        this.depth = depth;
        this.crawler = crawler;
    }

    /*
     * Runnable Crawl task method.
     */
    @Override
    public void run() {
        if (!crawler.getDomainFilter().isAllowed(url)) {
            System.out.println("Domain not allowed for URL: " + url);
            return;
        }

        Document doc = crawler.getFetcher().fetch(url);
        List<String> links = crawler.getLinkExtractor().extractLinks(doc);
        System.out.println("Crawled URL: " + url + " at depth " + depth + " found " + links.size() + " links.");
        links.forEach(link -> {
            System.out.println("Found link: " + link + " at depth " + (depth + 1));
            if (!crawler.hasBeenVisited(link) && depth + 1 <= crawler.getConfig().getMaxDepth()) {
                crawler.crawl(link, depth + 1);
            }
        });
    }
}
