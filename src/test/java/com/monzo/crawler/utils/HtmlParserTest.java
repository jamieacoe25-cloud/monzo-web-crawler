package com.monzo.crawler.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlParserTest {

  private LinkExtractor parser;

  @BeforeEach
  void setUp() {
    parser = new LinkExtractor();
  }

  @Test
  void extractLinks_shouldReturnAllAbsoluteLinks() {
    String html = """
        <html>
          <body>
            <a href="https://monzo.com/page1">Page 1</a>
            <a href="/page2">Page 2</a>
            <a>Missing href</a>
          </body>
        </html>
        """;

    Document doc = Jsoup.parse(html, "https://monzo.com");

    List<String> links = parser.extractLinks(doc);

    assertEquals(2, links.size(), "Should extract only valid absolute links");
    assertTrue(links.contains("https://monzo.com/page1"));
    assertTrue(links.contains("https://monzo.com/page2"));
  }

  @Test
  void extractLinks_emptyDocument_shouldReturnEmptyList() {
    Document doc = Jsoup.parse("<html></html>", "https://monzo.com");
    List<String> links = parser.extractLinks(doc);
    assertTrue(links.isEmpty(), "Empty document should return empty list");
  }
}
