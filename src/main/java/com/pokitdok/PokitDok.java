/**
	Client access library to the PokitDok APIs.
*/

package com.pokitdok;

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
  public static final String DEFAULT_API_BASE = "https://platform.pokitdok.com";
  public static final String DEFAULT_SCOPE = "default";
  public static final String USER_SCHEDULE_SCOPE = "user_schedule";

  private String              apiBase;
  private String              clientId;
  private String              clientSecret;
  private String              apiVersion;
  private HttpClientBuilder   builder;
  private JSONParser          parser;
  private boolean             failedOnceAlready;
  private Map<String, String> scopeCodes;
  private Map<String, String> scopeTokens;

  public PokitDok(String clientId, String clientSecret)
    throws IOException, ParseException {
    this(clientId, clientSecret, "v4");
  }

  public PokitDok(String clientId, String clientSecret, String apiVersion)
  throws IOException, ParseException {
    this.apiBase      = DEFAULT_API_BASE;
    this.clientId     = clientId;
    this.clientSecret = clientSecret;
    this.apiVersion   = apiVersion;

    parser = new JSONParser();
    failedOnceAlready = false;
    scopeCodes = new HashMap<String, String>();
    scopeTokens = new HashMap<String, String>();
  }

  public void connect(String scopeName) throws IOException, ParseException {
    builder = HttpClientBuilder.create();
    builder.useSystemProperties();
    
    HttpPost request = new HttpPost(getAPIBase() + "/oauth2/token");
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
    request.setEntity(new UrlEncodedFormEntity(urlParameters));

    String auth = clientId + ":" + clientSecret;
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
    String authHeader = "Basic " + new String(encodedAuth);

    request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    setDefaultHeaders(request);
  
    CloseableHttpClient client = builder.build();
    try {
      HttpResponse response = client.execute(request);
      Map<String, Object> parsedResponse = (JSONObject) parser.parse(
        EntityUtils.toString(response.getEntity()));

      scopeTokens.put(scopeName, (String) parsedResponse.get("access_token"));
    }
    finally {
      if (client != null) client.close();
    }
  }

  public void connect() throws IOException, ParseException {
    connect(DEFAULT_SCOPE);
  }

  private String getAccessTokenForScope(String scopeName)
  throws IOException, ParseException {
    if (!scopeTokens.containsKey(scopeName)) {
      connect(scopeName);
    }

    return scopeTokens.get(scopeName);
  }

  private void setDefaultHeaders(HttpRequestBase request) {
    request.setHeader(HttpHeaders.USER_AGENT, "pokitdok-java 0.7 jvm version " + System.getProperty("java.version"));
  }

  private Map<String, Object> executeAndParse(HttpRequestBase request, String scopeName)
  throws IOException, UnauthorizedException, ParseException {
    String accessToken = getAccessTokenForScope(scopeName);
    request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
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
        parsedResponse = executeAndParse(request, scopeName);

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

  public String getAPIBase() {
    return this.apiBase;
  }

  public void setAPIBase(String apiBase) {
    this.apiBase = apiBase;
  }

  private String apiUrl(String endpoint, Map<String, Object> params) {
    String uri = getAPIBase() + "/api/" + apiVersion + "/" + endpoint;

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

  private String getScopeCode(String name) {
    return scopeCodes.get(name);
  }

  private String getScopeToken(String name) {
    if (scopeTokens.containsKey(name)) {
      return scopeTokens.get(name);
    }
    else {
      return null;
    }
  }

  private Map<String, Object> get(String url, Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get(url, params, DEFAULT_SCOPE);
  }

  private Map<String, Object> get(String url, Map<String, Object> params, String scope)
  throws IOException, ParseException, UnauthorizedException {
    HttpGet getRequest = new HttpGet(apiUrl(url, params));

    return executeAndParse(getRequest, scope);
  }

  private Map<String, Object> post(String url, Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return post(url, params, DEFAULT_SCOPE);
  }

  private Map<String, Object> post(String url, Map<String, Object> params, String scope)
  throws IOException, ParseException, UnauthorizedException {
    HttpPost postRequest = new HttpPost(apiUrl(url));

    String json = JSONValue.toJSONString(params);
    StringEntity entity = new StringEntity(json);
    entity.setContentEncoding(HTTP.UTF_8);
    entity.setContentType("application/json");
    postRequest.setEntity(entity);

    return executeAndParse(postRequest, scope);
  }

  private Map<String, Object> put(String url, Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return put(url, params, DEFAULT_SCOPE);
  }

  private Map<String, Object> put(String url, Map<String, Object> params, String scope)
  throws IOException, ParseException, UnauthorizedException {
    HttpPut putRequest = new HttpPut(apiUrl(url, params));

    return executeAndParse(putRequest, scope);
  }

  private Map<String, Object> delete(String url, Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return delete(url, params, DEFAULT_SCOPE);
  }

  private Map<String, Object> delete(String url, Map<String, Object> params, String scope)
  throws IOException, ParseException, UnauthorizedException {
    HttpDelete deleteRequest = new HttpDelete(apiUrl(url, params));

    return executeAndParse(deleteRequest, scope);
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
  throws IOException, ParseException, UnauthorizedException {
    return get("activities", params);
  }

  public Map<String, Object> activities() throws IOException, ParseException, UnauthorizedException {
    return activities(null);
  }

  /** Invokes the authorizations endpoint, with a HashMap of parameters. */
  public Map<String, Object> authorizations(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return post("authorizations/", params);
  }

  /** Invokes the cash prices endpoint, with a HashMap of parameters. */
  public Map<String, Object> cashPrices(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("prices/cash", params);
  }

  /** Invokes the claims endpoint, with a HashMap of parameters. */
  public Map<String, Object> claims(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return post("claims/", params);
  }

  /** Invokes the claim status endpoint, with a HashMap of parameters. */
  public Map<String, Object> claimStatus(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return post("claims/status", params);
  }

  /** Invokes the eligibility endpoint, with a HashMap of parameters. */
  public Map<String, Object> eligibility(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return post("eligibility/", params);
  }

  /** Invokes the enrollment endpoint, with a HashMap of parameters. */
  public Map<String, Object> enrollment(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return post("enrollment", params);
  }

  /**
    Uploads an EDI file to the files endpoint.

    @param tradingPartnerId the trading partner to transmit to
    @param filename the path to the file to transmit
  */
  public Map<String, Object> files(String tradingPartnerId, String filename)
  throws IOException, ParseException, UnauthorizedException {
    return files(tradingPartnerId, new File(filename));
  }

    /**
    Uploads an EDI file to the files endpoint.

    @param tradingPartnerId the trading partner to transmit to
    @param filename the file to transmit
  */
  public Map<String, Object> files(String tradingPartnerId, File filename)
  throws IOException, ParseException, UnauthorizedException {
    return new HashMap();
  }

  /** Invokes the insurance prices endpoint, with a HashMap of parameters. */
  public Map<String, Object> insurancePrices(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("prices/insurance", params);
  }

  /** Invokes the payers endpoint, with a HashMap of parameters. */
  public Map<String, Object> payers(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("payers", params);
  }

  /** Invokes the payers endpoint, with no parameters. */
  public Map<String, Object> payers() throws IOException, ParseException, UnauthorizedException {
    return payers(null);
  }

  /** Invokes the plans endpoint, with no parameters. */
  public Map<String, Object> plans() throws IOException, ParseException, UnauthorizedException {
    return get("plans", null);
  }

  /** Invokes the plans endpoint, with a HashMap of parameters. */
  public Map<String, Object> plans(Map<String, Object> params) throws IOException, ParseException, UnauthorizedException {
    return get("plans", params);
  }

  /** Invokes the providers endpoint, with a HashMap of parameters. */
  public Map<String, Object> providers(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("providers", params);
  }

  /** Invokes the providers endpoint, with no parameters. */
  public Map<String, Object> providers() throws IOException, ParseException, UnauthorizedException {
    return providers(null);
  }

  /** Invokes the referrals endpoint, with a HashMap of parameters. */
  public Map<String, Object> referrals(Map<String, Object> params) throws IOException, ParseException, UnauthorizedException {
    return post("referrals/", params);
  }

  /** Invokes the trading partners endpoint, with a HashMap of parameters. */
  public Map<String, Object> tradingPartners(Map<String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    if (params.containsKey("trading_partner_id")) {
      String tradingPartnerId = (String) params.remove("trading_partner_id");
      return get("tradingpartners/" + tradingPartnerId, params);
    }
    else {
      return get("tradingpartners/", params);
    }
  }

  /* Scheduling endpoints */

  /** Invokes the appointments endpoint, for a singular appointment,
      with a HashMap of parameters. */
  public Map<String, Object> appointment(String uuid, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("appointments/" + uuid, params, USER_SCHEDULE_SCOPE);
  }

  /** Invokes the appointments endpoint, with a HashMap of parameters. */
  public Map<String, Object> appointments(Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("appointments/", params, USER_SCHEDULE_SCOPE);
  }

  /** Invokes the appointment types endpoint, for a singular appointment
      type, with a HashMap of parameters. */
  public Map<String, Object> appointmentType(String type, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("appointment_types/" + type, params);
  }

  /** Invokes the appointment types endpoint with a HashMap of parameters. */
  public Map<String, Object> appointmentTypes(Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("appointment_types/", params);
  }

  /** Invokes the appointments endpoint to book a new appointment 
      with a HashMap of parameters. */
  public Map<String, Object> bookAppointment(String uuid, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return put("appointments/" + uuid, params, USER_SCHEDULE_SCOPE);  
  }

  /** Invokes the appointments endpoint to cancel a new appointment 
      with a HashMap of parameters. */
  public Map<String, Object> cancelAppointment(String uuid, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return delete("appointments/" + uuid, params, USER_SCHEDULE_SCOPE);
  }

  /** Invokes the schedulers endpoint, for a singular scheduler,
      with a HashMap of parameters. */
  public Map<String, Object> scheduler(String schedulerId, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("schedulers/" + schedulerId, params);  
  }

  /** Invokes the schedulers endpoint with a HashMap of parameters. */
  public Map<String, Object> schedulers(Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("schedulers/", params);
  }

  /* Scheduling endpoints */
  public Map<String, Object> updateAppointment(String uuid, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return put("appointments/" + uuid, params, USER_SCHEDULE_SCOPE);
  }

  /* Medical procedure code endpoints */
  public Map<String, Object> mpc(Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("mpc/", params);
  }

  public Map<String, Object> mpc(String code, Map <String, Object> params)
  throws IOException, ParseException, UnauthorizedException {
    return get("mpc/" + code, params);
  }

  public void addScopeCode(String scopeName, String scopeCode) {
    this.scopeCodes.put(scopeName, scopeCode);
  }
}
