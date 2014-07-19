package com.pokitdok.tests;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.pokitdok.PokitDok;

public class PokitDokTest {
  private PokitDok pd;

  @BeforeSuite
  public void setup() throws Exception {
    pd = new PokitDok("2MBlqahR2xiaBtVSS50n", "FJaN1fyB1V5q7qPLNrb2F6yV1Xkaui0OB6eXotOS");
  }

  @Test(groups = { "basic" })
  public void shouldCreateTest() throws Exception {
    assertNotNull(pd);
  }

  @Test(groups = { "providers" })
  public void providersTest() throws Exception {
    Map query = new HashMap<String, String>();
    query.put("npi", "1467560003");

    Map response = pd.providers(query);
    assertDataAndMeta(response);
    assertSomeData(response);
  }

  @Test(groups = { "eligibility" })
  public void eligibilityTest() throws Exception {
    String eligibility = readEntireFile("src/test/scaffold/eligibility.json");

    Map eligibilityQuery = (JSONObject) JSONValue.parse(eligibility);
    Map<String, Object> response = pd.eligibility(eligibilityQuery);

    assertDataAndMeta(response);
    assertSomeData(response);
  }

  private void assertDataAndMeta(Map response) {
    assertNotNull(response);
    assert(response.containsKey("meta"));
    assert(response.containsKey("data"));
  }

  private void assertSomeData(Map response) {
    assert(((Map) response.get("data")).size() > 0);
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
