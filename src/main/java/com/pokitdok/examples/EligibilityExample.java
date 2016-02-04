package com.pokitdok.examples;

import java.util.Map;
import com.pokitdok.PokitDok;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class EligibilityExample {
  public static void main(String argv[]) {
    try {
      PokitDok pd = new PokitDok("CLIENT_ID_GOES_HERE", "CLIENT_SECRET_GOES_HERE");

      StringBuffer eligibilityRequest = new StringBuffer();
      eligibilityRequest.append("{");
      eligibilityRequest.append("\"member\": {");
      eligibilityRequest.append("\"birth_date\": \"1970-01-01\",");
      eligibilityRequest.append("\"first_name\": \"Jane\",");
      eligibilityRequest.append("\"last_name\": \"Doe\",");
      eligibilityRequest.append("\"id\": \"W000000000\"");
      eligibilityRequest.append("},");
      eligibilityRequest.append("\"provider\": {");
      eligibilityRequest.append("\"first_name\": \"JEROME\",");
      eligibilityRequest.append("\"last_name\": \"AYA-AY\",");
      eligibilityRequest.append("\"npi\": \"1467560003\"");
      eligibilityRequest.append("},");
      eligibilityRequest.append("\"service_types\": [\"health_benefit_plan_coverage\"],");
      eligibilityRequest.append("\"trading_partner_id\": \"MOCKPAYER\"");
      eligibilityRequest.append("}");

      Map eligibilityQuery = (JSONObject) JSONValue.parse(eligibilityRequest.toString());
      Map<String, Object> response = pd.eligibility(eligibilityQuery);
      System.out.println(response);
    }
    catch (Exception e) {
      System.out.println("An exception occurred: " + e.toString());
    }
  }
}
