package com.pokitdok;

import java.io.IOException;
import java.util.Map;
import org.json.simple.parser.ParseException;

public interface PokitDokHTTPConnector {
    public void authenticate() throws IOException, ParseException, UnauthorizedException;

    public String get(String url, Map<String, Object> params, Map<String, String> headers)
        throws IOException, ParseException, UnauthorizedException;

    public String get(String url, Map<String, Object> params, Map<String, String> headers, String scope)
        throws IOException, ParseException, UnauthorizedException;

    public String post(String url, Map<String, Object> params, Map<String, String> headers)
        throws IOException, ParseException, UnauthorizedException;

    public String post(String url, Map<String, Object> params, Map<String, String> headers, String scope)
        throws IOException, ParseException, UnauthorizedException;

    public String put(String url, Map<String, Object> params, Map<String, String> headers)
        throws IOException, ParseException, UnauthorizedException;

    public String put(String url, Map<String, Object> params, Map<String, String> headers, String scope)
        throws IOException, ParseException, UnauthorizedException;

    public String delete(String url, Map<String, Object> params, Map<String, String> headers)
        throws IOException, ParseException, UnauthorizedException;

    public String delete(String url, Map<String, Object> params, Map<String, String> headers, String scope)
        throws IOException, ParseException, UnauthorizedException;
}
