package com.lexor.service.until;


import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.Environment;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.lexor.config.ConfigProperties;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class QBRequest implements IQBRequest{

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(QBRequest.class);
    private static final CloseableHttpClient CLIENT = HttpClientBuilder.create().build();
    /*
     * @param url
	 * @param accessToken
	 * @return
             */
    @Override
    public Object callAPIGET(String url, String accessToken) throws JSONException {

        HttpGet httprequest = new HttpGet(url);

        //add header
        httprequest.setHeader("Content-Type", "application/json");
        httprequest.setHeader("Accept", "application/json");
        if(accessToken != null && !accessToken.isEmpty()) {
            httprequest.setHeader("Authorization", "Bearer " + accessToken);
            httprequest.setHeader("Request-Id", UUID.randomUUID().toString().replace("-", ""));
        }

        try {
            HttpResponse response = CLIENT.execute(httprequest);

            if (response.getStatusLine().getStatusCode() == 401){

                //refresh token
                OAuth2Config oauth2Config = new OAuth2Config.OAuth2ConfigBuilder("AB21Nk3ZmmuwN6IO1AlOAfpH45kYwrDsZimAD4ZQQm1ARtczrl",
                       "UEPsBTa9ZHmv1En06Fk4Bo1ScIWGxjW21V1WoD6J")
                        .callDiscoveryAPI(Environment.SANDBOX)
                        .buildConfig();
                OAuth2PlatformClient client = new OAuth2PlatformClient(oauth2Config);
                BearerTokenResponse bearerTokenResponse;
                try {
                    bearerTokenResponse = client.refreshToken("AB116184621389GAhb1sjPMyEf6IV3UmIbtCmUVQmy8gxtzI9k");

                    httprequest.setHeader("Authorization", "Bearer " + bearerTokenResponse.getAccessToken());
                    httprequest.setHeader("Request-Id", UUID.randomUUID().toString().replace("-", ""));

                    //call API again
                    response = CLIENT.execute(httprequest);

                } catch (OAuthException e) {
                    LOG.error("Error calling API", e.getCause());
                    return new JSONObject().put("response","error calling API").toString();
                }
            }

            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201){

                LOG.info(response.toString());
                return response;

            } else {
                LOG.info("failed calling API");
                return new JSONObject().put("response","error calling API").toString();
            }

        } catch (Exception e) {
            LOG.error("Error calling API", e.getCause());
            return new JSONObject().put("response","error calling API").toString();
        }

    }
}
