package com.monzo.crawler.utils;

import java.net.URI;

/**
 * Domain filter for filtering out invalid domains.
 */
public class DomainFilter {

    private final String allowedDomain;

    public DomainFilter(String allowedDomain) {
        this.allowedDomain = allowedDomain;
    }

    /*
     * Check if URL is allowed.
     */
    public boolean isAllowed(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        try {
            URI uri = URI.create(url);
            String host = uri.getHost();

            if (host == null) {
                return false;
            }

            host = host.toLowerCase();
            return host.equals(allowedDomain) || host.endsWith("." + allowedDomain);
        } catch (IllegalArgumentException e) {
            System.err.println("error checking url" + url + "against host due to " + e.getMessage());
            return false;
        }
    }
}
