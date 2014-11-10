package com.pokitdok.tests;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.Recorder;
import com.pokitdok.PokitDok;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PokitDokTest {
	private static final String AUTHORIZATIONS_JSON = "src/test/resources/authorizations.json";
	private static final String ELIGIBILITY_JSON = "src/test/resources/eligibility.json";
	private static final String CLAIMS_JSON = "src/test/resources/claim.json";
	private static final String CLAIMS_STATUS_JSON = "src/test/resources/claim_status.json";
	private static final String REFERRALS_JSON = "src/test/resources/referrals.json";
	private static PokitDok client;

	@Rule
	public Recorder recorder = new Recorder();

	@BeforeClass
	public static void setup() throws Exception {
		// ACHTUNG: For your own testing, you'll need to replace the client ID and secret with your own
		client = new PokitDok("fY4mRbR0Z1Jt4ncQxzoS", "v0rS5gimKU6j2IbWQL6hOM4IMdWqTtTfNMXfjzPH");
		PokitDok spy = spy(client);
		when(spy.apiBase()).thenReturn("http://me.pokitdok.com:5002");
		client = spy;
	}

	@Test
	public void shouldCreateTest() throws Exception {
		assertNotNull(client);
	}

	@Test
	@Betamax(tape = "authorizations")
	public void authorizationsTest() throws Exception {
		String authorizations = readEntireFile(AUTHORIZATIONS_JSON);
		Map query = (JSONObject) JSONValue.parse(authorizations);

		Map<String, Object> response = client.authorizations(query);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "cash_prices")
	public void cashPricesTest() throws Exception {
		Map<String, Object> cashQuery = new HashMap<>();
		cashQuery.put("cpt_code", "87799");
		cashQuery.put("zip_code", "75201");

		Map<String, Object> response = client.cashPrices(cashQuery);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "insurance_prices")
	public void insurancePricesTest() throws Exception {
		Map<String, Object> insuranceQuery = new HashMap<>();
		insuranceQuery.put("cpt_code", "87799");
		insuranceQuery.put("zip_code", "29403");

		Map<String, Object> response = client.insurancePrices(insuranceQuery);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "providers")
	public void providersTest() throws Exception {
		Map<String, Object> query = new HashMap<>();
		query.put("npi", "1467560003");

		Map response = client.providers(query);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "eligibility")
	public void eligibilityTest() throws Exception {
		String eligibility = readEntireFile(ELIGIBILITY_JSON);

		Map<String, Object> eligibilityQuery = (JSONObject) JSONValue.parse(eligibility);
		Map<String, Object> response = client.eligibility(eligibilityQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "claims")
	public void claimsTest() throws Exception {
		String claim = readEntireFile(CLAIMS_JSON);

		Map<String, Object> claimQuery = (JSONObject) JSONValue.parse(claim);
		Map<String, Object> response = client.eligibility(claimQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "claims_status")
	public void claimsStatusTest() throws Exception {
		String claimStatus = readEntireFile(CLAIMS_STATUS_JSON);

		Map<String, Object> claimStatusQuery = (JSONObject) JSONValue.parse(claimStatus);
		Map<String, Object> response = client.eligibility(claimStatusQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	public void filesTest() throws Exception {
		// TODO: Add a test for the /files API endpoint
	}

	@Test
	@Betamax(tape = "activities")
	public void activitiesTest() throws Exception {
		Map<String, Object> response = client.activities();
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "payers")
	public void payersTest() throws Exception {
		Map<String, Object> response = client.payers();
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "trading_partners_index")
	public void tradingPartnersIndexTest() throws Exception {
		Map<String, Object> response = client.tradingPartners(new HashMap<String, Object>());
		assertDataAndMeta(response);
		assertTrue(response.get("data") instanceof org.json.simple.JSONArray);
	}

	@Test
	@Betamax(tape = "trading_partners_get")
	public void tradingPartnersGetTest() throws Exception {
		Map<String, Object> query = new HashMap<>();
		query.put("trading_partner_id", "MOCKPAYER");

		Map<String, Object> response = client.tradingPartners(query);
		assertDataAndMeta(response);
		assertTrue(response.get("data") instanceof org.json.simple.JSONObject);
	}

	@Test
	@Betamax(tape = "plans_index")
	public void plansIndexTest() throws Exception {
		Map<String, Object> query = new HashMap<>();
		Map<String, Object> response = client.plans(query);

		assertDataAndMeta(response);
		assertHasDataArray(response);
	}

	@Test
	@Betamax(tape = "plans_get")
	public void plansGetTest() throws Exception {
		Map<String, Object> query = new HashMap<>();
		query.put("state", "TX");
		query.put("plan_type", "PPO");

		Map<String, Object> response = client.plans(query);
		assertDataAndMeta(response);
		assertHasDataArray(response);
	}

	@Test
	@Betamax(tape = "referrals")
	public void referralsTest() throws Exception {
		String referrals = readEntireFile(REFERRALS_JSON);

		Map<String, Object> referralsQuery = (JSONObject) JSONValue.parse(referrals);
		Map<String, Object> response = client.referrals(referralsQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	/**
	 * *************************************************************************
	 * Beyond lie utility methods for testing purposes. Nothing to see here.
	 * **************************************************************************
	 */

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

	/**
	 * Utility method for loading up JSON scaffolds. Don't do this.
	 */
	private String readEntireFile(String filename) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(filename)) {
			return IOUtils.toString(inputStream);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
}
