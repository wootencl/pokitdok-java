/**	
	Client access library to the PokitDok APIs.
*/
import java.util.HashMap;
import java.util.Map;
import org.apache.oltu.oauth2.client.*;
import org.apache.oltu.oauth2.client.request.*;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

public class PokitDok {
	private String clientId;
	private String clientSecret;

	private String API_URL = "https://platform.pokitdok.com";

	public PokitDok(String clientId, String clientSecret) {
		this.clientId 		= clientId;
		this.clientSecret = clientSecret;

		/* Connect */
		connect();
	}

	private void connect() throws Exception {
			OAuthClientRequest request = OAuthClientRequest
				.authorizationLocation(API_URL + "/oauth2/token")
        .setClientId(this.clientId)
        .setClientSecret(this.clientSecret)
        //.setRedirectURI("http://www.example.com/redirect")
        //.setCode(code)
        .buildQueryMessage();
        
        OAuthJSONAccessTokenResponse oAuthResponse = request.accessToken(request, OAuthJSONAccessTokenResponse.class);
 
        String accessToken = oAuthResponse.getAccessToken();
        long expiresIn = oAuthResponse.getExpiresIn();

        System.out.println("Got a access token " + accessToken);
	}

	/** Invokes the activities endpoint, with a HashMap of parameters. */
	public Map activities(Map params) { return new HashMap(); }

	/** Invokes the cash prices endpoint, with a HashMap of parameters. */
	public Map cashPrices(Map params) { return new HashMap(); }
	 
  /** Invokes the insurance prices endpoint, with a HashMap of parameters. */
  public Map insurancePrices(Map params) { return new HashMap(); }

  /** Invokes the claim status endpoint, with a HashMap of parameters. */
  public Map claimStatus(Map params) { return new HashMap(); }
  
  /** Invokes the claims endpoint, with a HashMap of parameters. */
  public Map claims(Map params) { return new HashMap(); }

  /** Invokes the eligibility endpoint, with a HashMap of parameters. */
  public Map eligibility(Map params) { return new HashMap(); }

  /** Invokes the enrollment endpoint, with a HashMap of parameters. */
  public Map enrollment(Map params) { return new HashMap(); }

  /** 
	  Uploads an EDI file to the files endpoint.
	  
	  @param trading_partner_id the trading partner to transmit to
	  
	  @param filename the path to the file to transmit
	*/
  public Map files(String tradingPartnerId, String filename) {return new HashMap(); }

  /** Invokes the payers endpoint, with a HashMap of parameters. */
  public Map payers(Map params) { return new HashMap(); }

  /** Invokes the providers endpoint, with a HashMap of parameters. */
  public Map providers(Map params) { return new HashMap(); }
}