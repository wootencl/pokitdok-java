package com.pokitdok.tests;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.Recorder;
import com.pokitdok.PokitDok;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
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

public class PokitDokTest {
	private static final String AUTHORIZATIONS_JSON = "src/test/resources/authorizations.json";
	private static final String ELIGIBILITY_JSON = "src/test/resources/eligibility.json";
	private static final String CLAIMS_JSON = "src/test/resources/claim.json";
	private static final String CLAIMS_STATUS_JSON = "src/test/resources/claim_status.json";
	private static final String REFERRALS_JSON = "src/test/resources/referrals.json";
	private static final String BOOK_APPOINTMENT_JSON = "src/test/resources/book_appointment.json";
	private static final String OPEN_SLOTS_JSON = "src/test/resources/open_slots.json";
	private static final String UPDATE_APPOINTMENT_JSON = "src/test/resources/update_appointment.json";

	@Rule
	public static Recorder recorder = new Recorder();

	private PokitDok connect() throws Exception {
		// For your own testing, you'll need to replace the client ID and secret with your own
		PokitDok client = new PokitDok("EY9GpOXS42EUIBQCd1Ft", "OQcdiZNjOHmS2L2mAUXm7xwX5dQBeJBk9mwwcTaF");
		client.setAPIBase("http://me.pokitdok.com:5002");
		client.connect();

		return client;
	}

	@Test
	@Betamax(tape = "authentication")
	public void shouldCreateTest() throws Exception {
		assertNotNull(connect());
	}

	@Test
	@Betamax(tape = "authorizations")
	public void authorizationsTest() throws Exception {
		String authorizations = readEntireFile(AUTHORIZATIONS_JSON);
		Map query = (JSONObject) JSONValue.parse(authorizations);
		PokitDok client = connect();
		Map<String, Object> response = client.authorizations(query);

		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "cash_prices")
	public void cashPricesTest() throws Exception {
		Map<String, Object> cashQuery = new HashMap<String, Object>();
		cashQuery.put("cpt_code", "87799");
		cashQuery.put("zip_code", "75201");

		PokitDok client = connect();
		Map<String, Object> response = connect().cashPrices(cashQuery);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "insurance_prices")
	public void insurancePricesTest() throws Exception {
		Map<String, Object> insuranceQuery = new HashMap<String, Object>();
		insuranceQuery.put("cpt_code", "87799");
		insuranceQuery.put("zip_code", "29403");

		Map<String, Object> response = connect().insurancePrices(insuranceQuery);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "providers")
	public void providersTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("npi", "1467560003");

		Map response = connect().providers(query);
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "eligibility")
	public void eligibilityTest() throws Exception {
		String eligibility = readEntireFile(ELIGIBILITY_JSON);

		Map<String, Object> eligibilityQuery = (JSONObject) JSONValue.parse(eligibility);
		Map<String, Object> response = connect().eligibility(eligibilityQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "claims")
	public void claimsTest() throws Exception {
		String claim = readEntireFile(CLAIMS_JSON);

		Map<String, Object> claimQuery = (JSONObject) JSONValue.parse(claim);
		Map<String, Object> response = connect().eligibility(claimQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "claims_status")
	public void claimsStatusTest() throws Exception {
		String claimStatus = readEntireFile(CLAIMS_STATUS_JSON);

		Map<String, Object> claimStatusQuery = (JSONObject) JSONValue.parse(claimStatus);
		Map<String, Object> response = connect().eligibility(claimStatusQuery);

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
		Map<String, Object> response = connect().activities();
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "payers")
	public void payersTest() throws Exception {
		Map<String, Object> response = connect().payers();
		assertDataAndMeta(response);
	}

	@Test
	@Betamax(tape = "trading_partners_index")
	public void tradingPartnersIndexTest() throws Exception {
		Map<String, Object> response = connect().tradingPartners(new HashMap<String, Object>());
		assertDataAndMeta(response);
		assertTrue(response.get("data") instanceof org.json.simple.JSONArray);
	}

	@Test
	@Betamax(tape = "trading_partners_get")
	public void tradingPartnersGetTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("trading_partner_id", "MOCKPAYER");

		Map<String, Object> response = connect().tradingPartners(query);
		assertDataAndMeta(response);
		assertTrue(response.get("data") instanceof org.json.simple.JSONObject);
	}

	@Test
	@Betamax(tape = "plans_index")
	public void plansIndexTest() throws Exception {

		Map<String, Object> response = connect().plans();

		assertDataAndMeta(response);
		assertHasDataArray(response);
	}

	@Test
	@Betamax(tape = "plans_get")
	public void plansGetTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("state", "TX");
		query.put("plan_type", "PPO");

		Map<String, Object> response = connect().plans(query);
		assertDataAndMeta(response);
		assertHasDataArray(response);
	}

	@Test
	@Betamax(tape = "referrals")
	public void referralsTest() throws Exception {
		String referrals = readEntireFile(REFERRALS_JSON);

		Map<String, Object> referralsQuery = (JSONObject) JSONValue.parse(referrals);
		Map<String, Object> response = connect().referrals(referralsQuery);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	/*******************
	 * Scheduling tests.
	 *******************/
	@Test
	@Betamax(tape = "scheduling")
	public void listSchedulersTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> response = connect().schedulers(query);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void showSchedulerTest() throws Exception {
		String id = "";

		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> response = connect().scheduler(id, query);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void listAppointmentTypesTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> response = connect().appointmentTypes(query);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void showAppointmentTypeTest() throws Exception {
		String id = "";

		Map<String, Object> response =
			connect().appointmentType(id, new HashMap<String, Object>());

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void bookAppointmentTest() throws Exception {
		String id = "";

		String bookAppointmentJSON = readEntireFile(BOOK_APPOINTMENT_JSON);

		Map<String, Object> query = (JSONObject) JSONValue.parse(bookAppointmentJSON);
		Map<String, Object> response = connect().bookAppointment(id, query);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void updateAppointmentTest() throws Exception {
		String queryJSON = readEntireFile(UPDATE_APPOINTMENT_JSON);
		String id = "";

		Map<String, Object> query = (JSONObject) JSONValue.parse(queryJSON);
		Map<String, Object> response = connect().updateAppointment(id, query);

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void cancelAppointmentTest() throws Exception {
		String id = "";

		Map<String, Object> response =
			connect().cancelAppointment(id, new HashMap<String, Object>());

		assertDataAndMeta(response);
		assertHasData(response);
	}

	@Test
	@Betamax(tape = "scheduling")
	public void failWithoutScopeCodeTest() throws Exception {
	}

	@Test
	@Betamax(tape = "mpc")
	public void mpcTest() throws Exception {
		Map<String, Object> response =
			connect().mpc(new HashMap<String, Object>());

		assertDataAndMeta(response);
		assertHasDataArray(response);
	}

	@Test
	@Betamax(tape = "mpc")
	public void singleMPCTest() throws Exception {
		String mpc = "99213";
		Map<String, Object> response =
			connect().mpc(mpc, new HashMap<String, Object>());

		assertDataAndMeta(response);
		assertHasData(response);
	}	

	/***********************************************************************
	 * Beyond lie utility methods for testing purposes. Nothing to see here.
	 ***********************************************************************/

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
		FileInputStream inputStream = new FileInputStream(filename);
		try {
			return IOUtils.toString(inputStream);
		}
		catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
		finally {
			if (inputStream != null) inputStream.close();
		}
	}
}
