package com.monzo.crawler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.monzo.crawler.config.CrawlConfig;
import com.monzo.crawler.utils.DomainFilter;
import com.monzo.crawler.utils.HtmlFetcher;
import com.monzo.crawler.utils.LinkExtractor;

class CrawlerTest {

    private Crawler crawler;
    private CrawlConfig config;
    private HtmlFetcher fetcher;
    private LinkExtractor extractor;
    private DomainFilter filter;

    @BeforeEach
    void setup() {
        config = mock(CrawlConfig.class);
        fetcher = mock(HtmlFetcher.class);
        extractor = mock(LinkExtractor.class);
        filter = mock(DomainFilter.class);

        when(config.getThreads()).thenReturn(2);
        when(config.getMaxDepth()).thenReturn(2);
        when(config.getMaxQueueSize()).thenReturn(2);

        // By default, all URLs are allowed
        when(filter.isAllowed(anyString())).thenReturn(true);

        crawler = new Crawler(config, fetcher, extractor, filter);
    }

    @Test
    void testCrawlerDependencies() {
        assertEquals(config, crawler.getConfig());
        assertEquals(fetcher, crawler.getFetcher());
        assertEquals(extractor, crawler.getLinkExtractor());
        assertEquals(filter, crawler.getDomainFilter());
    }

    @Test
    void testMarkVisitedAndHasBeenVisited() {
        String url = "https://monzo.com";

        assertFalse(crawler.hasBeenVisited(url));

        crawler.markVisited(url);

        assertTrue(crawler.hasBeenVisited(url));
    }

    @Test
    void testCrawlRegistersAndVisitsUrl() throws InterruptedException {
        String url = "https://monzo.com";

        crawler.crawl(url, 0);

        // Wait briefly to let the async task run
        Thread.sleep(100);

        assertTrue(crawler.hasBeenVisited(url));
    }

    @Test
    void testCompileCrawlLogSortsAndFilters() {
        String url1 = "https://b.com";
        String url2 = "https://a.com";
        String url3 = "https://c.com";

        crawler.markVisited(url1);
        crawler.markVisited(url2);
        crawler.markVisited(url3);

        when(filter.isAllowed(url1)).thenReturn(true);
        when(filter.isAllowed(url2)).thenReturn(false);
        when(filter.isAllowed(url3)).thenReturn(true);

        crawler.compileCrawlLog();

        List<String> log = crawler.getCrawlLog();
        assertEquals(2, log.size());
        assertEquals("https://b.com", log.get(0));
        assertEquals("https://c.com", log.get(1));
    }

    @Test
    void testCrawlDoesNotDuplicateUrls() throws InterruptedException {
        String url = "https://monzo.com";

        crawler.crawl(url, 0);
        crawler.crawl(url, 0);

        Thread.sleep(100);

        int visitedCount = (int) crawler.getVisited().stream().filter(u -> u.equals(url)).count();
        assertEquals(1, visitedCount, "URL should only be visited once");
    }

}
