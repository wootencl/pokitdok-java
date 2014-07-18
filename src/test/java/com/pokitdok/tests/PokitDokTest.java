package com.pokitdok.tests;
 
import org.testng.annotations.*;
import com.pokitdok.PokitDok;

public class PokitDokTest {
  @BeforeClass
  public void setUp() {
   // code that will be invoked when this test is instantiated
  }

  @Test(groups = { "basic" })
  public void shouldCreateTest() throws Exception {
    PokitDok pd = new PokitDok("2MBlqahR2xiaBtVSS50n", "FJaN1fyB1V5q7qPLNrb2F6yV1Xkaui0OB6eXotOS");
  }
}
