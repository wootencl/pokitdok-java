package com.pokitdok.examples;

import com.pokitdok.PokitDok;

public class EligibilityExample {
  public static void main(String argv[]) {
    try {
      PokitDok pd = new PokitDok("2MBlqahR2xiaBtVSS50n", "FJaN1fyB1V5q7qPLNrb2F6yV1Xkaui0OB6eXotOS");
    }
    catch (Exception e) {
      System.out.println("An exception occurred: " + e.toString());
    }
  }
}
