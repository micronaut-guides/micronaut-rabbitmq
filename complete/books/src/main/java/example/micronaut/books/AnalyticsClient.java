package example.micronaut.books;

import io.micronaut.configuration.rabbitmq.annotation.Binding;
import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;

@RabbitClient("micronaut") // <1>
public interface AnalyticsClient {

    @Binding("analytics") // <2>
    void updateAnalytics(Book book); // <3>
}
