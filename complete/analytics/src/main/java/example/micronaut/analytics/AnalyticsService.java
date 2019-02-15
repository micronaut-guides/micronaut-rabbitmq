package example.micronaut.analytics;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class AnalyticsService {

    private final Map<Book, Long> bookAnalytics = new ConcurrentHashMap<>(); // <1>

    public void updateBookAnalytics(Book book) { // <2>
        bookAnalytics.computeIfPresent(book, (k, v) -> v + 1);
        bookAnalytics.putIfAbsent(book, 1L);
    }

    public List<BookAnalytics> listAnalytics() { // <3>
        return bookAnalytics
                .entrySet()
                .stream()
                .map(e -> new BookAnalytics(e.getKey().getIsbn(), e.getValue()))
                .collect(Collectors.toList());
    }
}
