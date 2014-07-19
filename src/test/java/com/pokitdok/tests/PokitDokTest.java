package com.pokitdok.tests;
 
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.*;
import com.pokitdok.PokitDok;

public class PokitDokTest {
  //private PokitDok pd;

  @Test(groups = { "basic" })
  public void shouldCreateTest() throws Exception {
    PokitDok pd = new PokitDok("2MBlqahR2xiaBtVSS50n", "FJaN1fyB1V5q7qPLNrb2F6yV1Xkaui0OB6eXotOS");
    assertNotNull(pd);
  }

  @Test(groups = { "providers" })
  public void providersTest() throws Exception {
    PokitDok pd = new PokitDok("2MBlqahR2xiaBtVSS50n", "FJaN1fyB1V5q7qPLNrb2F6yV1Xkaui0OB6eXotOS");
    Map query = new HashMap<String, String>();
    query.put("npi", "1467560003");

    Map response = pd.providers(query);
    assertDataAndMeta(response);
    
  }

  private void assertDataAndMeta(Map response) {
    assertNotNull(response);
    assert(response.containsKey("meta"));
    assert(response.containsKey("data"));
  }
}
