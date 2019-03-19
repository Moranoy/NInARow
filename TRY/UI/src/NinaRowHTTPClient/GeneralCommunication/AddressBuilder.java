package NinaRowHTTPClient.GeneralCommunication;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AddressBuilder {
    private String mScheme;
    private String mHost;
    private String mPath;
    private Map<String, String> mParametersMap = new HashMap<>();

    public void setmScheme(String mScheme) {
        this.mScheme = mScheme;
    }

    public void setmHost(String mHost) {
        this.mHost = mHost;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public void addParameter(String key, String value) {
        this.mParametersMap.put(key, value);
    }

    public String build() {
        // Init uri builder.
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(this.mScheme)
                .setHost(this.mHost)
                .setPath(this.mPath);

        // Add params
        this.mParametersMap
                .entrySet()
                .forEach(
                        (paramKeyValue) -> uriBuilder.setParameter(paramKeyValue.getKey(), paramKeyValue.getValue())
                );

        String address = null;

        try {
            // Exception should'nt happen. Print if does happen and fix.
            address = uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return address;
    }
}
