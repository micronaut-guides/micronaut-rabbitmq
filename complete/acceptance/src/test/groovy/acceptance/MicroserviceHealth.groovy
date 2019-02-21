package acceptance

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.exceptions.HttpClientException

@CompileStatic
@Slf4j
trait MicroserviceHealth {

    abstract BlockingHttpClient getClient()

    boolean isUp(String url) {
        String microservicesUrl = url.endsWith('/health') ? url : "${url}/health"
        try {
            StatusResponse statusResponse = client.retrieve(HttpRequest.GET(microservicesUrl), StatusResponse)
            if ( statusResponse.status == 'UP' ) {
                return true
            }
        } catch (HttpClientException e) {
        }
        return false

    }
}