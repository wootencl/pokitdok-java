[![Build Status](https://travis-ci.org/pokitdok/pokitdok-java.svg?branch=master)](https://travis-ci.org/pokitdok/pokitdok-java)
[![Dependency Freshness](https://www.versioneye.com/user/projects/545bc23228766672b4000020/badge.svg)](https://www.versioneye.com/user/projects/545bc23228766672b4000020)


pokitdok-java
=============

PokitDok Platform API Client for Java

## Resources
* [Read the PokitDok API docs][apidocs]
* [Read the pokitdok-java Javadoc][javadoc]
* [View Source on GitHub][code]
* [Report Issues on GitHub][issues]

[apidocs]: https://platform.pokitdok.com/documentation/v4#/
[code]: https://github.com/PokitDok/pokitdok-java
[issues]: https://github.com/PokitDok/pokitdok-java/issues
[javadoc]: https://pokitdok.github.io/pokitdok-java/javadoc

## Installation

### Via Maven
pokitdok-java is pushed to Maven Central via the Sonatype OSS repository.

* Group ID: com.pokitdok
* Artifact ID: pokitdok-java
* Latest Version: 0.9

So, for example, in Gradle, you could add the dependency
```java
compile "com.pokitdok:pokitdok-java:0.9"
```

### Manual Install
You can download pokitdok-java-0.9.jar from [here](https://github.com/pokitdok/pokitdok-java/raw/master/build/libs/pokitdok-java-0.9.jar) and include it on your classpath, like any other JAR.
A few other JAR requirements need to be on your classpath as well. They are:
* Apache HTTPCore and HTTPClient, available from [http://hc.apache.org/downloads.cgi](http://hc.apache.org/downloads.cgi)
* Apache Commons Codec, available from [http://commons.apache.org/proper/commons-codec/download_codec.cgi](http://commons.apache.org/proper/commons-codec/download_codec.cgi)
* Apache Commons Logging, available from [http://commons.apache.org/proper/commons-logging/download_logging.cgi](http://commons.apache.org/proper/commons-logging/download_logging.cgi)
* json-simple, available from [https://code.google.com/p/json-simple/downloads/detail?name=json-simple-1.1.1.jar](https://code.google.com/p/json-simple/downloads/detail?name=json-simple-1.1.1.jar)

## Quick Start
```java
import com.pokitdok.PokitDok;
import org.json.simple.JSONValue;

public class PokitDokTest {
  public static void main(String args[]) {
    PokitDok pd = new PokitDok("your_client_id", "your_client_secret");
    pd.connect();

    /* Retrieve provider information by NPI */
    Map npiQuery = new HashMap<String, String>();
    npiQuery.put("npi", "1467560003");
    pd.providers(npiQuery);

    /* Search providers by name (individuals) */
    Map nameQuery = new HashMap<String, String>();
    nameQuery.put("first_name", "JEROME");
    nameQuery.put("last_name", "AYA-AY")
    pd.providers(nameQuery);

    /* Search providers by name (organizations) */
    Map orgQuery = new HashMap<String, String>();
    orgQuery.put("name", "Qliance");
    pd.providers(orgQuery);

    /* Search providers by location and/or specialty */
    Map providerQuery = new HashMap<String, String>();
    providerQuery.put("zipcode", "29307");
    providerQuery.put("radius", "10mi");
    providerQuery.put("specialty", "RHEUMATOLOGY");
    pd.providers(providerQuery);

    /*
      Eligibility endpoint example. This example uses the SimpleJSON library
      to parse a string called "eligibility", containing JSON, into a Map,
      which is then passed into the Eligibility endpoint. You can create this
      Map in any way you prefer - loading it from a JSON string is done for
      succintness of discussion. The contents of the string are as follows:

      {
        "member": {
            "birth_date": "1970-01-01",
            "first_name": "Jane",
            "last_name": "Doe",
            "id": "W000000000"
        },
        "provider": {
            "first_name": "JEROME",
            "last_name": "AYA-AY",
            "npi": "1467560003"
        },
        "service_types": ["health_benefit_plan_coverage"],
        "trading_partner_id": "MOCKPAYER"
      }
    */

    Map eligibilityQuery = JSONValue.parse(eligibility);
    pd.eligibility(eligibilityQuery);

    /*
      Claim endpoint example. This example parses a JSON string similarly to the
      above Eligibility example. The JSON is as follows:

      {
        "transaction_code": "chargeable",
        "trading_partner_id": "MOCKPAYER",
        "billing_provider": {
          "taxonomy_code": "207Q00000X",
          "first_name": "Jerome",
          "last_name": "Aya-Ay",
          "npi": "1467560003",
          "address": {
            "address_lines": ["8311 WARREN H ABERNATHY HWY"],
            "city": "SPARTANBURG",
            "state": "SC",
            "zipcode": "29301"
          },
          "tax_id": "123456789"
        },
        "subscriber": {
          "first_name": "Jane",
          "last_name": "Doe",
          "member_id": "W000000000",
          "address": {
            "address_lines": ["123 N MAIN ST"],
            "city": "SPARTANBURG",
            "state": "SC",
            "zipcode": "29301"
          },
          "birth_date": "1970-01-01",
          "gender": "female"
        },
        "claim": {
          "total_charge_amount": 60.0,
          "service_lines": [
            {
              "procedure_code": "99213",
              "charge_amount": 60.0,
              "unit_count": 1.0,
              "diagnosis_codes": ["487.1"],
              "service_date": "2014-06-01"
            }
          ]
        }
      }
    */

    Map claimJSON = JSONValue.parse(claim);
    pd.claim(claimJSON);

    /*
      Check the status of a Claim. The JSON looks like this:

      {
          "patient": {
              "birth_date": "1970-01-01",
              "first_name": "JANE",
              "last_name": "DOE",
              "id": "1234567890"
          },
          "provider": {
              "first_name": "Jerome",
              "last_name": "Aya-Ay",
              "npi": "1467560003",
          },
          "service_date": "2014-01-01",
          "trading_partner_id": "MOCKPAYER"
      }
    */

    Map claimStatusQuery = JSONValue.parse(claimStatus);
    pd.claimStatus(claimStatusQuery);

    /* Retrieve an index of activities */
    pd.activities();

    /* Check on a specific activity */
    Map activityQuery = new HashMap<String, String>();
    activityQuery.put("activity_id", "5362b5a064da150ef6f2526c");
    pd.activities(activityQuery);

    /* Check on a batch of activities */
    Map batchQuery = new HashMap<String, String>();
    batchQuery.put("parent_id", "537cd4b240b35755f5128d5c");
    pd.activities(batchQuery);

    /* Upload an EDI file */
    pd.files("trading_partner_id", "path/to/a_file.edi");

    /* Get cash prices */
    Map cashQuery = new HashMap<String, String>();
    cashQuery.put("cpt_code", "87799");
    cashQuery.put("zip_code", "75201");
    pd.cashPrices(cashQuery);

    /* Get insurance prices */
    Map insuranceQuery = new HashMap<String, String>();
    cashQuery.put("cpt_code", "87799");
    cashQuery.put("zip_code", "29403");
    pd.insurancePrices(insuranceQuery);
  }
}

```

## Supported Java versions
This library aims to support and is tested against these Java versions.

* Oracle: Java SE 8, Java SE 7, Java SE 6 (with caveat)
* OpenJDK: OpenJDK 7, OpenJDK 6

### Java SE 6 Caveat
Java SE 6, being rather old, doesn't support the more rigorous security settings employed by the PokitDok
Platform servers. If you use the library as-is, you'll receive an error along the lines of:

```
Exception in thread "main" javax.net.ssl.SSLException: java.lang.RuntimeException: Could not generate DH keypair
  at com.sun.net.ssl.internal.ssl.Alerts.getSSLException(Alerts.java:190)
  at com.sun.net.ssl.internal.ssl.SSLSocketImpl.fatal(SSLSocketImpl.java:1747)
  at com.sun.net.ssl.internal.ssl.SSLSocketImpl.fatal(SSLSocketImpl.java:1708)
  at com.sun.net.ssl.internal.ssl.SSLSocketImpl.handleException(SSLSocketImpl.java:1691)
  at com.sun.net.ssl.internal.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1222)
  at com.sun.net.ssl.internal.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1199)
  at org.apache.http.conn.ssl.SSLConnectionSocketFactory.createLayeredSocket(SSLConnectionSocketFactory.java:290)
  at org.apache.http.conn.ssl.SSLConnectionSocketFactory.connectSocket(SSLConnectionSocketFactory.java:259)
  at org.apache.http.impl.conn.HttpClientConnectionOperator.connect(HttpClientConnectionOperator.java:125)
  at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:319)
  at org.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:363)
  at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:219)
  at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:195)
  at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:86)
  at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:108)
  at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:184)
  at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:82)
  at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:106)
  at com.pokitdok.PokitDok.connect(PokitDok.java:78)
```

To work around this error, you may use the replacement security provider from BouncyCastle. Download
the JDK 1.5 - 1.7 security provider JAR from [http://www.bouncycastle.org/latest_releases.html](http://www.bouncycastle.org/latest_releases.html) (the filename will be _bcprov-jdk15on-VERSION.jar_) and put
it on your CLASSPATH. In your code, import the Java security class, and the new BouncyCastle provider:

```java
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
```

Then, before instantiating the PokitDok class, do the following:
```java
/* This line enables the replacement security provider. */
Security.addProvider(new BouncyCastleProvider());
```

Of course, the recommended fix is to upgrade to a supported JDK version, but if this is not possible, the
above fix should work for you.
## License
Copyright (c) 2014-2016 PokitDok Inc. See [LICENSE][] for details.

[license]: LICENSE
