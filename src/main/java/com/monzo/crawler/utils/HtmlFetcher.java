package com.monzo.crawler.utils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * HtmlFetcher for fetching Document of websites.
 */
public class HtmlFetcher {

    private HttpClient httpClient;

    public HtmlFetcher() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }

    public HtmlFetcher(final HttpClient client) {
        this.httpClient = client;
    }

    public Document fetch(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("Failed to fetch URL: " + url + " with status code: " + response.statusCode());
                return Jsoup.parse("", url);
            }
            return Jsoup.parse(response.body(), url);

        } catch (Exception e) {
            System.err.println("Error fetching URL: " + url + " - " + e.getMessage());
            return Jsoup.parse("", url);
        }
    }
}
