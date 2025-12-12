package com.monzo.crawler.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlFetcherTest {
    private static final String A_HREF = "a[href]";
    private HtmlFetcher fetcher;
    private HttpClient mockClient;
    private HttpResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockClient = mock(HttpClient.class);
        fetcher = new HtmlFetcher(mockClient);
        mockResponse = mock(HttpResponse.class);
    }

    @Test
    void testFetchshouldReturnDocumentWhenStatus200() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("<html><body><a href='page.html'>link</a></body></html>");

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Document doc = fetcher.fetch("https://monzo.com");

        assertNotNull(doc);
        assertEquals("https://monzo.com", doc.baseUri());
        assertEquals(1, doc.select(A_HREF).size());
    }

    @Test
    void testFetchshouldReturnEmptyDocumentWhenStatusNot200() throws Exception {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn("");

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Document doc = fetcher.fetch("https://monzo.com");

        assertNotNull(doc);
        assertTrue(doc.select(A_HREF).isEmpty());
    }

    @Test
    void testFetchReturnEmptyDocument_onException() throws Exception {
        when(mockClient.send(any(), any()))
                .thenThrow(new RuntimeException("error"));

        Document doc = fetcher.fetch("https://monzo.com");

        assertNotNull(doc);
        assertTrue(doc.select(A_HREF).isEmpty());
    }
}
