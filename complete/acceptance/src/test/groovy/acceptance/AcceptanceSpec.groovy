package acceptance

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.uri.UriTemplate
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class AcceptanceSpec extends Specification {

    static final String BOOKS_URL = 'http://localhost:8080'
    static final String ANALYTICS_URL = 'http://localhost:8081'

    @Shared
    HttpClient httpClient = HttpClient.create(new URL(BOOKS_URL))

    @Shared
    BlockingHttpClient client = httpClient.toBlocking()

    @Requires( {
        Closure isUp = { client, url ->
            String microservicesUrl = url.endsWith('/health') ? url : "${url}/health"
            try {
                StatusResponse statusResponse = client.retrieve(HttpRequest.GET(microservicesUrl), StatusResponse)
                if ( statusResponse.status == 'UP' ) {
                    return true
                }
            } catch (HttpClientException e) {
                println "HTTP Client exception for $microservicesUrl $e.message"
            }
            return false
        }
        BlockingHttpClient booksClient = HttpClient.create(new URL(BOOKS_URL)).toBlocking()
        BlockingHttpClient analyticsClient = HttpClient.create(new URL(ANALYTICS_URL)).toBlocking()
        return isUp(booksClient, BOOKS_URL) && isUp(analyticsClient, ANALYTICS_URL)
    })
    def "verifies rabbit mq integration works"() {
        when:
        HttpRequest analyticsRequest = HttpRequest.GET("${ANALYTICS_URL}/analytics")
        List<BookAnalytics> analytics = client.retrieve(analyticsRequest, Argument.of(List, BookAnalytics))

        then:
        !analytics

        when:
        String isbn = '1491950358'
        String uri = new UriTemplate(BOOKS_URL + '/books/{isbn}').expand(['isbn': isbn])
        HttpRequest bookRequest = HttpRequest.GET(uri)
        HttpResponse response = client.exchange(bookRequest)

        then:
        response.status() == HttpStatus.OK

        when:
        response = client.exchange(bookRequest)

        then:
        response.status() == HttpStatus.OK

        when:
        analyticsRequest = HttpRequest.GET("${ANALYTICS_URL}/analytics")

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
}
