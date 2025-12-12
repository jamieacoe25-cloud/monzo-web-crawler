package com.monzo.crawler.utils;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * LinkExtractor to get links from html.
 */
public class LinkExtractor {

    private static final String LINK_SELECTOR = "a[href]";

    /*
     * Extract links from jsoup document.
     */
    public List<String> extractLinks(Document doc) {
        Elements links = doc.select(LINK_SELECTOR);
        return links.stream()
                .map(link -> link.absUrl("href"))
                .filter(link -> !link.isEmpty())
                .toList();
    }
}
