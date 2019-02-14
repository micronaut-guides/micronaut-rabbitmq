package example.micronaut.books;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import java.util.Optional;

@Filter("/books/?*")
public class AnalyticsFilter implements HttpServerFilter {

    private final AnalyticsClient analyticsClient;

    public AnalyticsFilter(AnalyticsClient analyticsClient) {
        this.analyticsClient = analyticsClient;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return Flowable
                .fromPublisher(chain.proceed(request))
                .doOnNext(response -> {
                    Optional<Book> book = response.getBody(Book.class);
                    book.ifPresent(analyticsClient::updateAnalytics);
                });
    }
}
