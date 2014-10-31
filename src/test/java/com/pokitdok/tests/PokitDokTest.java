package com.pokitdok.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
//import co.freeside.betamax.Recorder;
import org.json.simple.parser.ParseException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.pokitdok.PokitDok;
import org.json.simple.JSONArray;

public class PokitDokTest {
  //private Recorder recorder;
  private PokitDok pd;

  @BeforeSuite
  public void setup() throws Exception {
    //recorder = new Recorder();

    /*
      For your own testing, you'll need to replace this client id and secret
      with your own.
    */
    pd = new PokitDok("fi5HZVH3gif94QvdIXeI", "Iu7YEVLQRzNPOcqEb4no4kNgvCov28bCguNFxiMl");
  }

  @Test
  public void shouldCreateTest() throws Exception {
    assertNotNull(pd);
  }

  @Test
  public void cashPricesTest() throws Exception {
    Map cashQuery = new HashMap<String, String>();
    cashQuery.put("cpt_code", "87799");
    cashQuery.put("zip_code", "75201");

    Map<String, Object> response = pd.cashPrices(cashQuery);
    assertDataAndMeta(response);
  }

  @Test
  public void insurancePricesTest() throws Exception {
    Map insuranceQuery = new HashMap<String, String>();
    insuranceQuery.put("cpt_code", "87799");
    insuranceQuery.put("zip_code", "29403");

    Map<String, Object> response = pd.insurancePrices(insuranceQuery);
    assertDataAndMeta(response);
  }

  @Test
  public void providersTest() throws Exception {
    Map query = new HashMap<String, String>();
    query.put("npi", "1467560003");

    Map response = pd.providers(query);
    assertDataAndMeta(response);
  }

  @Test
  public void eligibilityTest() throws Exception {
    String eligibility = readEntireFile("src/test/scaffold/eligibility.json");

    Map eligibilityQuery = (JSONObject) JSONValue.parse(eligibility);
    Map<String, Object> response = pd.eligibility(eligibilityQuery);

    assertDataAndMeta(response);
    assertHasData(response);
  }

  @Test
  public void claimsTest() throws Exception {
    String claim = readEntireFile("src/test/scaffold/claim.json");

    Map claimQuery = (JSONObject) JSONValue.parse(claim);
    Map<String, Object> response = pd.eligibility(claimQuery);

    assertDataAndMeta(response);
    assertHasData(response);
  }

  @Test
  public void claimsStatusTest() throws Exception {
    String claimStatus = readEntireFile("src/test/scaffold/claim_status.json");

    Map claimStatusQuery = (JSONObject) JSONValue.parse(claimStatus);
    Map<String, Object> response = pd.eligibility(claimStatusQuery);

    assertDataAndMeta(response);
    assertHasData(response);
  }

  @Test
  public void filesTest() throws Exception {

  }

  @Test
  public void activitiesTest() throws Exception {
    Map<String, Object> response = pd.activities();
    assertDataAndMeta(response);
  }

  @Test
  public void payersTest() throws Exception {
    Map<String, Object> response = pd.payers();
    assertDataAndMeta(response);
  }

  @Test
  public void tradingPartnersIndexTest() throws Exception {
    Map<String, Object> response = pd.tradingPartners(new HashMap<String, Object>());
    assertDataAndMeta(response);
    assertTrue(response.get("data") instanceof org.json.simple.JSONArray);
  }

  @Test
  public void tradingPartnersGetTest() throws Exception {
    Map query = new HashMap<String, String>();
    query.put("trading_partner_id", "MOCKPAYER");
    Map<String, Object> response = pd.tradingPartners(query);
    assertDataAndMeta(response);
    assertTrue(response.get("data") instanceof org.json.simple.JSONObject);
  }

  @Test
  public void plansTestNoArgs() throws Exception {
    Map query = new HashMap<String, String>();
    Map<String, Object> response = pd.plans(query);
    assertDataAndMeta(response);
    assertHasDataArray(response);
  }

  @Test
  public void plansTest() throws Exception {
    Map query = new HashMap<String, String>();
    query.put("state", "TX");
    query.put("plan_type", "PPO");
    Map<String, Object> response = pd.plans(query);
    assertDataAndMeta(response);
    assertHasDataArray(response);
  }

  /****************************************************************************
    Beyond lie utility methods for testing purposes. Nothing to see here.
  ****************************************************************************/

  private void assertDataAndMeta(Map response) {
    assertNotNull(response);
    assertTrue(response.containsKey("meta"));
    assertTrue(response.containsKey("data"));
  }

  private void assertHasData(Map response) {
    Map data = (Map) response.get("data");
    assertNotNull(data);
    assertTrue(data.size() > 0);
  }

  private void assertHasDataArray(Map response) {
    JSONArray data = (JSONArray) response.get("data");
    assertNotNull(data);
    assertTrue(data.size() > 0);
  }

  /** Utility method for loading up JSON scaffolds. Don't do this. */
  private String readEntireFile(String filename) {
    String retval = null;

    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      byte[] data = new byte[(int)file.length()];
      fis.read(data);
      fis.close();

      retval = new String(data, "UTF-8");
    }
    catch (IOException ie) {}

    return retval;
  }
}
