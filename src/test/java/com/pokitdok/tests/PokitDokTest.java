package com.pokitdok.tests;

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

	private PokitDok client;

	@Before
	public void connect() throws Exception {
		System.out.println("Connect");
		client = new PokitDok("client_id", "client_secret");
	}

	@Test
	public void shouldCreateTest() throws Exception {
		assertNotNull(client);
	}

	public void authorizationsTest() throws Exception {
		String authorizations = readEntireFile(AUTHORIZATIONS_JSON);
		Map query = (JSONObject) JSONValue.parse(authorizations);
		Map<String, Object> response = client.authorizations(query);
	}

	public void cashPricesTest() throws Exception {
		Map<String, Object> cashQuery = new HashMap<String, Object>();
		cashQuery.put("cpt_code", "87799");
		cashQuery.put("zip_code", "75201");

		Map<String, Object> response = client.cashPrices(cashQuery);
	}

	public void insurancePricesTest() throws Exception {
		Map<String, Object> insuranceQuery = new HashMap<String, Object>();
		insuranceQuery.put("cpt_code", "87799");
		insuranceQuery.put("zip_code", "29403");

		Map<String, Object> response = client.insurancePrices(insuranceQuery);
	}

	public void providersTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("npi", "1467560003");

		Map response = client.providers(query);
	}

	public void eligibilityTest() throws Exception {
		String eligibility = readEntireFile(ELIGIBILITY_JSON);

		Map<String, Object> eligibilityQuery = (JSONObject) JSONValue.parse(eligibility);
		Map<String, Object> response = client.eligibility(eligibilityQuery);
	}

	public void claimsTest() throws Exception {
		String claim = readEntireFile(CLAIMS_JSON);

		Map<String, Object> claimQuery = (JSONObject) JSONValue.parse(claim);
		Map<String, Object> response = client.eligibility(claimQuery);
	}

	public void claimsStatusTest() throws Exception {
		String claimStatus = readEntireFile(CLAIMS_STATUS_JSON);

		Map<String, Object> claimStatusQuery = (JSONObject) JSONValue.parse(claimStatus);
		Map<String, Object> response = client.eligibility(claimStatusQuery);
	}

	@Test
	public void filesTest() throws Exception {
		// TODO: Add a test for the /files API endpoint
	}

	public void activitiesTest() throws Exception {
		Map<String, Object> response = client.activities();
	}

	public void payersTest() throws Exception {
		Map<String, Object> response = client.payers();
	}

	public void tradingPartnersIndexTest() throws Exception {
		Map<String, Object> response = client.tradingPartners(new HashMap<String, Object>());
	}

	public void tradingPartnersGetTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("trading_partner_id", "MOCKPAYER");

		Map<String, Object> response = client.tradingPartners(query);
	}

	public void plansIndexTest() throws Exception {
		Map<String, Object> response = client.plans();
	}

	public void plansGetTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("state", "TX");
		query.put("plan_type", "PPO");

		Map<String, Object> response = client.plans(query);
	}

	public void referralsTest() throws Exception {
		String referrals = readEntireFile(REFERRALS_JSON);

		Map<String, Object> referralsQuery = (JSONObject) JSONValue.parse(referrals);
		Map<String, Object> response = client.referrals(referralsQuery);
	}

	/*******************
	 * Scheduling tests.
	 *******************/
	public void listSchedulersTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> response = client.schedulers(query);
	}

	public void showSchedulerTest() throws Exception {
		String id = "";

		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> response = client.scheduler(id, query);
	}

	public void listAppointmentTypesTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> response = client.appointmentTypes(query);
	}

	public void showAppointmentTypeTest() throws Exception {
		String id = "";

		Map<String, Object> response =
			client.appointmentType(id, new HashMap<String, Object>());
	}

	public void bookAppointmentTest() throws Exception {
		String id = "";

		String bookAppointmentJSON = readEntireFile(BOOK_APPOINTMENT_JSON);

		Map<String, Object> query = (JSONObject) JSONValue.parse(bookAppointmentJSON);
		Map<String, Object> response = client.bookAppointment(id, query);
	}

	public void updateAppointmentTest() throws Exception {
		String queryJSON = readEntireFile(UPDATE_APPOINTMENT_JSON);
		String id = "";

		Map<String, Object> query = (JSONObject) JSONValue.parse(queryJSON);
		Map<String, Object> response = client.updateAppointment(id, query);
	}

	public void cancelAppointmentTest() throws Exception {
		String id = "";

		Map<String, Object> response =
			client.cancelAppointment(id, new HashMap<String, Object>());
	}

	public void failWithoutScopeCodeTest() throws Exception {
	}

	public void mpcTest() throws Exception {
		Map<String, Object> response =
			client.mpc(new HashMap<String, Object>());
	}

	public void singleMPCTest() throws Exception {
		String mpc = "99213";
		Map<String, Object> response =
			client.mpc(mpc, new HashMap<String, Object>());
	}

	/***********************************************************************
	 * Beyond lie utility methods for testing purposes. Nothing to see here.
	 ***********************************************************************/

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
