package NinaRowHTTPClient.GeneralCommunication;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// General communication class - Send simple Get request. Return response body or response error code.
public class CommunicationHandler {
    private static final int MINIMUM_SUCCESS_CODE = 200;
    private static final int MAXIMUM_SUCCESS_CODE = 300;
    private static final String CONTEXT_PATH = "NinaRowWeb";

    private AddressBuilder mAddressBuilder;
    private String mBody = null;

    public CommunicationHandler() {
        this.initAddressBuilder();
    }

    private void initAddressBuilder() {
        this.mAddressBuilder = new AddressBuilder();
        this.mAddressBuilder.setmScheme("http");
        this.mAddressBuilder.setmHost("localhost:8080");
    }

    public void setPath(String mPath) {
        this.mAddressBuilder.setmPath(CONTEXT_PATH + "/" + mPath);
    }

    public void addParameter(String key, String value) {
        this.mAddressBuilder.addParameter(key, value);
    }

    public void doGet(Consumer<String> onSuccess, BiConsumer<String, Integer> onFailure) {
        // Perform http request in a different thread.
        new Thread(() -> {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUriRequest httpGet = this.getHttpRequest();
            System.out.println("Sending request: " + httpGet);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println("Received response: " + response);
                this.handleResponse(response, onSuccess, onFailure);
            } catch (IOException exception) {
                System.out.println("Exception with message: " + exception.getMessage());
            }
        }).start();
    }

    public void doGet(Runnable onFinish) {
        // Perform http request in a different thread.
        new Thread(() -> {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpUriRequest httpGet = this.getHttpRequest();

            System.out.println("Sending request: " + httpGet);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println("Received response: " + response);
            } catch (IOException exception) {
                System.out.println("Exception with message: " + exception.getMessage());
            } finally {
                onFinish.run();
            }
        }).start();
    }

    private HttpUriRequest getHttpRequest() {
        HttpUriRequest request;

        if(this.mBody != null && !this.mBody.isEmpty()) {
            request = new HttpGetWithBody(this.mAddressBuilder.build(), this.mBody);
        } else {
            request = new HttpGet(this.mAddressBuilder.build());
        }

        return request;
    }

    private void handleResponse(CloseableHttpResponse response, Consumer<String> onSuccess, BiConsumer<String, Integer> onFailure) throws IOException {
        System.out.println(response.toString());

        if(response.getStatusLine().getStatusCode() >= MINIMUM_SUCCESS_CODE && response.getStatusLine().getStatusCode() <= MAXIMUM_SUCCESS_CODE) {
            String responseBody = EntityUtils.toString(response.getEntity());
            onSuccess.accept(responseBody);
        } else {
            // Error occurred, response body now contains error message.
            String errorMessage = EntityUtils.toString(response.getEntity());
            onFailure.accept(errorMessage, response.getStatusLine().getStatusCode());
        }
    }

    public void addBody(String body) {
        this.mBody = body;
    }

    // A way to send a get request with a body.
    private class HttpGetWithBody extends HttpPost {
        public final static String METHOD_NAME = "GET";

        public HttpGetWithBody(String url, String body) {
            super(url);
            super.setEntity(new StringEntity(body, ContentType.TEXT_PLAIN));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }
}
