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

        try {
            // create identity
            Map<String, Object> response = pd.activities();
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
