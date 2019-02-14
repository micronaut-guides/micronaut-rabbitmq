package analytics;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AnalyticsService {

    private final Map<Book, Long> bookAnalytics = new ConcurrentHashMap<>();

    public void updateBookAnalytics(Book book) {
        bookAnalytics.computeIfPresent(book, (k, v) -> v + 1);
        bookAnalytics.putIfAbsent(book, 1L);
    }
}
