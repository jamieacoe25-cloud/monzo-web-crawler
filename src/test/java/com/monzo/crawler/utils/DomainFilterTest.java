package com.monzo.crawler.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DomainFilterTest {

    @Test
    void testIsAllowedReturnTrueForExactDomain() {
        DomainFilter filter = new DomainFilter("monzo.com");

        assertTrue(filter.isAllowed("https://monzo.com"));
        assertTrue(filter.isAllowed("http://monzo.com"));
        assertTrue(filter.isAllowed("https://monzo.com/page1"));
    }

    @Test
    void testIsAllowedReturnTrueForSubdomain() {
        DomainFilter filter = new DomainFilter("monzo.com");

        assertTrue(filter.isAllowed("https://sub.monzo.com"));
        assertTrue(filter.isAllowed("https://www.monzo.com/page2"));
    }

    @Test
    void testIsAllowedReturnFalseForOtherDomains() {
        DomainFilter filter = new DomainFilter("monzo.com");

        assertFalse(filter.isAllowed("https://facebook.com"));
        assertFalse(filter.isAllowed("https://notmonzo.com"));
        assertFalse(filter.isAllowed("https://moonzo.org"));
        assertFalse(filter.isAllowed("https://malicious.com/monzo.com"));
    }

    @Test
    void testIsAllowedReturnFalseForNullHost() {
        DomainFilter filter = new DomainFilter("monzo.com");

        // URI without host (e.g., relative URL)
        assertFalse(filter.isAllowed("/page1"));
    }
}
