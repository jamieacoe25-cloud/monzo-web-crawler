package com.monzo.crawler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.monzo.crawler.config.CrawlConfig;
import com.monzo.crawler.utils.DomainFilter;
import com.monzo.crawler.utils.HtmlFetcher;
import com.monzo.crawler.utils.LinkExtractor;

class CrawlTaskTest {

    private Crawler crawler;
    private HtmlFetcher fetcher;
    private LinkExtractor extractor;
    private DomainFilter filter;
    private CrawlConfig config;

    @BeforeEach
    void setUp() {
        fetcher = mock(HtmlFetcher.class);
        extractor = mock(LinkExtractor.class);
        filter = mock(DomainFilter.class);
        config = mock(CrawlConfig.class);

        when(config.getMaxDepth()).thenReturn(2);
        when(filter.isAllowed(anyString())).thenReturn(true);

        crawler = mock(Crawler.class);

        // Provide access to mocked methods used in CrawlTask
        when(crawler.getFetcher()).thenReturn(fetcher);
        when(crawler.getLinkExtractor()).thenReturn(extractor);
        when(crawler.getDomainFilter()).thenReturn(filter);
        when(crawler.getConfig()).thenReturn(config);
        when(crawler.hasBeenVisited(anyString())).thenReturn(false);
    }

    @Test
    void testrunAllMethodsAreCalledWhenUrlHasChildren() {
        String url = "https://monzo.com";
        int depth = 0;

        Document doc = mock(Document.class);
        when(fetcher.fetch(url)).thenReturn(doc);

        // Pretend page has two links
        List<String> links = List.of("https://monzo.com/page1", "https://monzo.com/page2");
        when(extractor.extractLinks(doc)).thenReturn(links);

        CrawlTask task = new CrawlTask(url, depth, crawler);
        task.run();

        // Verify fetcher and parser were called
        verify(fetcher).fetch(url);
        verify(extractor).extractLinks(doc);
    }

    @Test
    void testRunSkipUrlIfNotAllowed() {
        String url = "https://invalid.com";
        when(filter.isAllowed(url)).thenReturn(false);

        CrawlTask task = new CrawlTask(url, 0, crawler);
        task.run();

        verify(fetcher, never()).fetch(anyString());
        verify(extractor, never()).extractLinks(any());
    }

    @Test
    void TestRunSkipUrlIfAlreadyVisited() {
        String url = "https://visitedUrl.com";
        when(crawler.hasBeenVisited(url)).thenReturn(true);

        CrawlTask task = new CrawlTask(url, 0, crawler);
        task.run();

        verify(crawler, never()).crawl(anyString(), anyInt());
    }
}
