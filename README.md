# Java Web Crawler

A Java web crawler which can loop through monzo crawl site.  Here are some of the features.

## Features
- **Concurrent crawling** using `ExecutorService` and `AtomicInteger` for active task tracking.  
- **Domain filtering** to restrict crawling to a specific domain.  
- **HTML fetching** via `HtmlFetcher` with `HttpClient`.  
- **Link extraction** using Jsoup (`LinkExtractor`).    
- **Thread-safe URL tracking** using `ConcurrentHashMap` to avoid duplicates.  
- **Crawl log output** written to `crawler_output.txt`.  

## Steps to run web-crawler
- Go to root directory
- run `java -cp "target\classes;lib/jsoup-1.21.2.jar" com.monzo.crawler.Main`

### **Crawler.java**
- **Purpose:** Main engine for the crawler.  
- Manages the URL queue, concurrency, and active tasks.  
- Submits `CrawlTask`s to fetch and process URLs.  
- Tracks visited URLs and generates the crawl log.

### **CrawlTask.java**
- **Purpose:** Runnable task for crawling a single URL.  
- Fetches the HTML content of the URL.  
- Extracts links using `LinkExtractor`.  
- Submits new URLs to `Crawler` if they haven’t been visited and depth limit allows.

### **Main.java**
- **Purpose:** Entry point to start the crawler.  
- Initializes `CrawlConfig`, `DomainFilter`, `HtmlFetcher`, and `LinkExtractor`.  
- Starts crawling from a given URL and measures execution time.

### **CrawlConfig.java**
- **Purpose:** Configuration for the crawler.  
- Holds `maxDepth`, `timeoutInMs`, `threads`, and `maxQueueSize`.  
- Used by `Crawler` to control crawling behavior.

### **DomainFilter.java**
- **Purpose:** Restricts crawling to allowed domains.  
- Check if a URL belongs to the allowed domain.  

### **HtmlFetcher.java**
- **Purpose:** Fetches HTML content from URLs.  
- Uses `HttpClient` to fetch content synchronously.  
- Returns a `Document` (Jsoup) for parsing.

### **LinkExtractor.java**
- **Purpose:** Extracts links from HTML documents.  
- Uses Jsoup to select all `<a href>` elements.  
- Returns a list of absolute URLs, ignoring empty links.

## Future Enhancements
- Add robots.txt compliance
- Implement rate limiting and polite crawling
- Support asynchronous fetch with CompletableFuture for larger sites
- Store logs in database or CSV for analysis