/**
	Client access library to the PokitDok APIs.
*/

package com.pokitdok;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokitDok {
  private String            clientId;
  private String            clientSecret;
  private String            apiVersion;
  private String            accessToken;
  private HttpClientBuilder builder;
  private JSONParser        parser;
  private boolean           failedOnceAlready;

  public PokitDok(String clientId, String clientSecret)
    throws IOException, ParseException {
    this(clientId, clientSecret, "v4");
  }

  public PokitDok(String clientId, String clientSecret, String apiVersion)
  throws IOException, ParseException {
    this.clientId     = clientId;
    this.clientSecret = clientSecret;
    this.apiVersion   = apiVersion;

    parser = new JSONParser();
    failedOnceAlready = false;
  }

  public void connect() throws IOException, ParseException {
    builder = HttpClientBuilder.create();
    builder.useSystemProperties();
    
    HttpPost request = new HttpPost(apiBase() + "/oauth2/token");
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
    request.setEntity(new UrlEncodedFormEntity(urlParameters));

    String auth = clientId + ":" + clientSecret;
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
    String authHeader = "Basic " + new String(encodedAuth);

    request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    setDefaultHeaders(request);

    try (CloseableHttpClient client = builder.build()) {
      HttpResponse response = client.execute(request);
      Map<String, Object> parsedResponse = (JSONObject) parser.parse(
              EntityUtils.toString(response.getEntity()));

      accessToken = (String) parsedResponse.get("access_token");
    }
  }

  private void setDefaultHeaders(HttpRequestBase request) {
    request.setHeader(HttpHeaders.USER_AGENT, "pokitdok-java 0.6.1 jvm version " + System.getProperty("java.version"));
  }

  private Map<String, Object> executeAndParse(HttpRequestBase request)
  throws IOException, UnauthorizedException {
    request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
    setDefaultHeaders(request);
    CloseableHttpClient client = builder.build();
    HttpResponse response = client.execute(request);

    Map<String, Object> parsedResponse = null;
    try {
      String res = EntityUtils.toString(response.getEntity());
      parsedResponse = (JSONObject) parser.parse(res);

      /* Check for, and recover from, potential token timeout */
      if (isUnauthorized(parsedResponse, failedOnceAlready)) {
        failedOnceAlready = true;

        connect();
        parsedResponse = executeAndParse(request);

        /* If we fall through, all is okay. */
        failedOnceAlready = false;
      }

    }
    catch (ParseException pe) {
      System.out.println("Error while parsing response: " + pe.toString());
    }
    finally {
      client.close();
    }

    return parsedResponse;
  }

  public String apiBase() {
    return "https://platform.pokitdok.com";
  }

  private String apiUrl(String endpoint, Map<String, Object> params) {
    String uri = apiBase() + "/api/" + apiVersion + "/" + endpoint;

    if ((params != null) && (!params.isEmpty())) {
      try {
        URIBuilder uriWithParams = new URIBuilder(uri);

        for(String key: params.keySet()) {
          uriWithParams.addParameter(key, (String) params.get(key));
        }

        uri = uriWithParams.build().toString();
      }
      catch (URISyntaxException use) {
        uri = null;
      }
    }

    return uri;
  }

  private String apiUrl(String endpoint) {
    return apiUrl(endpoint, null);
  }

  private Map<String, Object> get(String url, Map<String, Object> params)
  throws IOException, UnauthorizedException {
    HttpGet getRequest = new HttpGet(apiUrl(url, params));

    return executeAndParse(getRequest);
  }

  private Map<String, Object> post(String url, Map<String, Object> params)
  throws IOException, UnauthorizedException {
    HttpPost postRequest = new HttpPost(apiUrl(url));

    String json = JSONValue.toJSONString(params);
    StringEntity entity = new StringEntity(json);
    entity.setContentEncoding(HTTP.UTF_8);
    entity.setContentType("application/json");
    postRequest.setEntity(entity);

    return executeAndParse(postRequest);
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

  /** Invokes the activities endpoint, with a HashMap of parameters. */
  public Map<String, Object> activities(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return get("activities", params);
  }

  public Map<String, Object> activities() throws IOException, UnauthorizedException {
    return activities(null);
  }

  /** Invokes the authorizations endpoint, with a HashMap of parameters. */
  public Map<String, Object> authorizations(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return post("authorizations/", params);
  }

  /** Invokes the cash prices endpoint, with a HashMap of parameters. */
  public Map<String, Object> cashPrices(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return get("prices/cash", params);
  }

  /** Invokes the claims endpoint, with a HashMap of parameters. */
  public Map<String, Object> claims(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return post("claims/", params);
  }

  /** Invokes the claim status endpoint, with a HashMap of parameters. */
  public Map<String, Object> claimStatus(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return post("claims/status", params);
  }

  /** Invokes the eligibility endpoint, with a HashMap of parameters. */
  public Map<String, Object> eligibility(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return post("eligibility/", params);
  }

  /** Invokes the enrollment endpoint, with a HashMap of parameters. */
  public Map<String, Object> enrollment(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return post("enrollment", params);
  }

  /**
    Uploads an EDI file to the files endpoint.

    @param tradingPartnerId the trading partner to transmit to
    @param filename the path to the file to transmit
  */
  public Map<String, Object> files(String tradingPartnerId, String filename)
  throws IOException, UnauthorizedException {
    return files(tradingPartnerId, new File(filename));
  }

    /**
    Uploads an EDI file to the files endpoint.

    @param tradingPartnerId the trading partner to transmit to
    @param filename the file to transmit
  */
  public Map<String, Object> files(String tradingPartnerId, File filename)
  throws IOException, UnauthorizedException {
    return new HashMap();
  }

  /** Invokes the insurance prices endpoint, with a HashMap of parameters. */
  public Map<String, Object> insurancePrices(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return get("prices/insurance", params);
  }

  /** Invokes the payers endpoint, with a HashMap of parameters. */
  public Map<String, Object> payers(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return get("payers", params);
  }

  /** Invokes the payers endpoint, with no parameters. */
  public Map<String, Object> payers() throws IOException, UnauthorizedException {
    return payers(null);
  }

  /** Invokes the plans endpoint, with no parameters. */
  public Map<String, Object> plans() throws IOException, UnauthorizedException {
    return get("plans", null);
  }

  /** Invokes the plans endpoint, with a HashMap of parameters. */
  public Map<String, Object> plans(Map<String, Object> params) throws IOException, UnauthorizedException {
    return get("plans", params);
  }

  /** Invokes the providers endpoint, with a HashMap of parameters. */
  public Map<String, Object> providers(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    return get("providers", params);
  }

  /** Invokes the providers endpoint, with no parameters. */
  public Map<String, Object> providers() throws IOException, UnauthorizedException {
    return providers(null);
  }

  /** Invokes the referrals endpoint, with a HashMap of parameters. */
  public Map<String, Object> referrals(Map<String, Object> params) throws IOException, UnauthorizedException {
    return post("referrals/", params);
  }

  /** Invokes the trading partners endpoint, with a HashMap of parameters. */
  public Map<String, Object> tradingPartners(Map<String, Object> params)
  throws IOException, UnauthorizedException {
    if (params.containsKey("trading_partner_id")) {
      String tradingPartnerId = (String) params.remove("trading_partner_id");
      return get("tradingpartners/" + tradingPartnerId, params);
    }
    else {
      return get("tradingpartners/", params);
    }
  }
}
