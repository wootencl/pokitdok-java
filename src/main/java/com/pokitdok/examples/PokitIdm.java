package com.pokitdok.examples;

import com.pokitdok.PokitDok;
import com.pokitdok.UnauthorizedException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;


/**
 * PokitDok!
 *
 * Purpose of this example is to provide a sample maven project, that will provide all the jar files (in target/PokitDok/lib)
 * as well as provide a rudimentary example of the identity api.
 *
 * To build:
 * mvn package
 *
 * To run:
 * cd to target/PokitDok
 * PD_CLIENT_ID=[CLIENT_ID] PD_CLIENT_SECRET=[CLIENT_SECRET] java -jar PokitDok.jar
 *
 * You can get CLIENT_ID and CLIENT_SECRET by signing up FREE for our platform
 *
 */
public class PokitIdm
{
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static PokitDok pd;

    private static String IDM_STRING = "{\n" +
            "  \"prefix\": \"Ms\",\n" +
            "  \"first_name\": \"Peg\",\n" +
            "  \"last_name\": \"PokitDok\",\n" +
            "  \"gender\": \"female\",\n" +
            "  \"birth_date\": \"1991-05-19\",\n" +
            "  \"email\": \"peggy@pokitdok.com\",\n" +
            "  \"address\": {\n" +
            "    \"address_lines\": [\"1542 Anywhere Avenue\"],\n" +
            "    \"city\": \"Charleston\",\n" +
            "    \"state\": \"SC\",\n" +
            "    \"zipcode\": \"29407\"\n" +
            "  }\n" +
            "}";

    private static String IDM_GET_STRING = "{\n" +
        "  \"prefix\": \"Ms\",\n" +
        "  \"first_name\": \"Peg\",\n" +
        "  \"last_name\": \"PokitDok\",\n" +
        "  \"gender\": \"female\",\n" +
        "  \"birth_date\": \"1991-05-19\",\n" +
        "  \"email\": \"peggy@pokitdok.com\",\n" +
        "}";

    private static String IDM_UPDATE_STRING = "{\n" +
            "  \"prefix\": \"Ms\",\n" +
            "  \"first_name\": \"Peggy\",\n" +
            "  \"last_name\": \"PokitDok\",\n" +
            "  \"gender\": \"female\",\n" +
            "  \"birth_date\": \"1991-05-23\",\n" +
            "  \"email\": \"peggy_email2@pokitdok.com\"\n" +
            "}";

    public static void main( String[] args )
    {
        Map<String, String> env = System.getenv();
        CLIENT_ID = "9P10N4H2F7ZbaAU6RYct";
        CLIENT_SECRET = "gOFzgJiIUoqnUhjaZezDxUf7ugPF6FsRAPy2tWDT";
        if ((CLIENT_ID == null) || (CLIENT_SECRET == null)) {
            System.out.println("Please provide a PokitDok client ID and secret in the environment variables PD_CLIENT_ID and PD_CLIENT_SECRET.");
        }

        // create connection to PokitDok platform with client credentials
        try {
            pd = new PokitDok(CLIENT_ID, CLIENT_SECRET, "http://localhost:5002");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // the create identity object
        Map<String, Object> postObject = (JSONObject) JSONValue.parse(IDM_STRING);
        // the query identity parameters object
        Map<String, Object> getObject = (JSONObject) JSONValue.parse(IDM_GET_STRING);
        // update string object
        Map<String, Object> updateObject = (JSONObject) JSONValue.parse(IDM_UPDATE_STRING);
        try {
            // create identity
            Map<String, Object> response = pd.createIdentity(postObject);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }
    }

}
