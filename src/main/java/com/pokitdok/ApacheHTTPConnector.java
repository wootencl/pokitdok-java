package com.pokitdok;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ApacheHTTPConnector implements PokitDokHTTPConnector {
    private HttpClientBuilder       builder;
    private JSONParser              parser;
    private boolean                 failedOnceAlready;
    private final String            apiBase;
    private Map<String, String>     defaultHeaders;
    private String                  clientId;
    private String                  clientSecret;
    private OAuthAccessToken        accessToken;
    private String                  authCode;


    public ApacheHTTPConnector(String clientId, String clientSecret, Map<String, String> defaultHeaders, String apiBase) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.defaultHeaders = defaultHeaders;
        this.apiBase = apiBase;
        this.accessToken = new OAuthAccessToken();
    }

    public void authenticate() {
        try {
            if (this.authCode != null) {
                //auth code grant type
            } else {
                AuthenticateClientCredentials();
            }
        } catch (Exception exc) {
            if (exc instanceof IOException) {
                System.err.println("IOException: " + exc.getMessage());
            } else if (exc instanceof ParseException) {
                System.err.println("ParseException: " + exc.getMessage());
            }
        }

    }

    public void AuthenticateClientCredentials() throws IOException, ParseException {
        parser = new JSONParser();
        builder = HttpClientBuilder.create();
        builder.useSystemProperties();

        HttpPost request = new HttpPost(apiBase + "/oauth2/token");
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        request.setEntity(new UrlEncodedFormEntity(urlParameters));

        String auth = this.clientId + ":" + this.clientSecret;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);

        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        setDefaultHeaders(request);

        CloseableHttpClient client = builder.build();
        try {
            HttpResponse response = client.execute(request);
            Map<String, Object> parsedResponse = (JSONObject) parser.parse(
                EntityUtils.toString(response.getEntity()));

            accessToken.access_token = (String) parsedResponse.get("access_token");
        }
        finally {
            if (client != null) client.close();
        }
    }

    private String execute(HttpRequestBase request, String scopeName, Map<String, String> headers)
    throws IOException, ParseException, UnauthorizedException {
        String accessToken = this.accessToken.access_token;
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        setDefaultHeaders(request);
        CloseableHttpClient client = builder.build();
        HttpResponse response = client.execute(request);

        String res = EntityUtils.toString(response.getEntity());
        Map<String, Object> parsedResponse = (JSONObject) parser.parse(res);

        return res;
    }

    public String get(String url, Map<String, Object> params, Map<String, String> headers)
    throws IOException, ParseException, UnauthorizedException {
      return get(url, params, headers, PokitDok.DEFAULT_SCOPE);
    }

    public String get(String url, Map<String, Object> params, Map<String, String> headers, String scope)
    throws IOException, ParseException, UnauthorizedException {
	HttpGet getRequest = new HttpGet(PokitDok.apiUrl(apiBase, url, params));

      return execute(getRequest, scope, headers);
    }

    public String post(String url, Map<String, Object> params, Map<String, String> headers)
    throws IOException, ParseException, UnauthorizedException {
      return post(url, params, headers, PokitDok.DEFAULT_SCOPE);
    }

    public String post(String url, Map<String, Object> params, Map<String, String> headers, String scope)
    throws IOException, ParseException, UnauthorizedException {
	HttpPost postRequest = new HttpPost(PokitDok.apiUrl(apiBase, url, null));

      String json = JSONValue.toJSONString(params);
      StringEntity entity = new StringEntity(json);
      entity.setContentEncoding(HTTP.UTF_8);
      entity.setContentType("application/json");
      postRequest.setEntity(entity);

      return execute(postRequest, scope, headers);
    }

    public String put(String url, Map<String, Object> params, Map<String, String> headers)
    throws IOException, ParseException, UnauthorizedException {
      return put(url, params, headers, PokitDok.DEFAULT_SCOPE);
    }

    public String put(String url, Map<String, Object> params, Map<String, String> headers, String scope)
    throws IOException, ParseException, UnauthorizedException {

        HttpPut putRequest = new HttpPut(PokitDok.apiUrl(apiBase, url, null));
	String json = JSONValue.toJSONString(params);
	StringEntity entity = new StringEntity(json);
        entity.setContentEncoding(HTTP.UTF_8);
        entity.setContentType("application/json");

        putRequest.setEntity(entity);

      return execute(putRequest, scope, headers);
    }

    public String delete(String url, Map<String, Object> params, Map<String, String> headers)
    throws IOException, ParseException, UnauthorizedException {
      return delete(url, params, headers, PokitDok.DEFAULT_SCOPE);
    }

    public String delete(String url, Map<String, Object> params, Map<String, String> headers, String scope)
    throws IOException, ParseException, UnauthorizedException {
	HttpDelete deleteRequest = new HttpDelete(PokitDok.apiUrl(apiBase,url, params));

      return execute(deleteRequest, scope, headers);
    }

    private boolean isUnauthorized(Map<String, Object> response, boolean throwOnUnauthorized) throws UnauthorizedException {
      boolean unauthorized = false;
      if (response != null) {
        if (response.containsKey("message")) {
          if ("Unauthorized".equals(response.get("message"))) {
            unauthorized = true;
          }
        }
      }

      if (throwOnUnauthorized && unauthorized) {
        throw new UnauthorizedException();
      }

      return unauthorized;
    }

    private void setDefaultHeaders(HttpRequestBase request) {
        for (Map.Entry<String, String> entry: defaultHeaders.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
    }

    // Inner Classes/Objects
    private class OAuthAccessToken {
        public String access_token;
        public String refresh_token;
        public String token_type;
        public int expires;
        public int expires_in;


        public OAuthAccessToken() {
            this.access_token = "";
            this.refresh_token = "";
            this.token_type = "Bearer";
            this.expires = 0;
            this.expires_in = 3600;
        }

    }
}
