// src/test/java/utils/PageManager.java
package utils;

import com.microsoft.playwright.Page;

public class PageManager {

    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    public static Page getPage() {
        return page.get();
    }

    public static void setPage(Page p) {
        page.set(p);
    }

    public static void removePage() {
        page.remove();
    }
}