package acceptance

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.uri.UriTemplate
import org.junit.Assume
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class AcceptanceSpec extends Specification implements MicroserviceHealth {

    @Shared
    HttpClient httpClient = HttpClient.create(new URL(booksUrl()))

    @Override
    BlockingHttpClient getClient() {
        httpClient.toBlocking()
    }

    def "verifies rabbit mq integration works"() {
        given:
        [booksUrl(), analyticsUrl()].each { String url ->
            Assume.assumeTrue(isUp(url))
        }

        when:
        HttpRequest analyticsRequest = HttpRequest.GET("${analyticsUrl()}/analytics")
        List<BookAnalytics> analytics = client.retrieve(analyticsRequest, Argument.of(List, BookAnalytics))

        then:
        !analytics

        when:
        String isbn = '1491950358'
        String uri = new UriTemplate(booksUrl() + '/books/{isbn}').expand(['isbn': isbn])
        HttpRequest bookRequest = HttpRequest.GET(uri)
        HttpResponse response = client.exchange(bookRequest)

        then:
        response.status() == HttpStatus.OK

        when:
        response = client.exchange(bookRequest)

        then:
        response.status() == HttpStatus.OK

        when:
        analyticsRequest = HttpRequest.GET("${analyticsUrl()}/analytics")

        then:
        new PollingConditions().eventually {
            client.retrieve(analyticsRequest, Argument.of(List, BookAnalytics))
        }

        when:
        analytics = client.retrieve(analyticsRequest, Argument.of(List, BookAnalytics))

        then:
        analytics.find { analytic -> analytic.bookIsbn == isbn}
        analytics.find { analytic -> analytic.bookIsbn == isbn}.count == 2
    }

    String booksUrl() {
        'http://localhost:8080'
    }

    String analyticsUrl() {
        'http://localhost:8081'
    }
}
