package com.pokitdok.tests;

import java.io.*;
import java.util.*;
import com.pokitdok.*;
import com.pokitdok.tests.*;
import com.pokitdok.tests.categories.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.*;
import org.junit.*;
import org.junit.experimental.categories.*;
import org.junit.runner.*;
import org.junit.runners.Suite;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PokitDokUnitTests {
	private static final String AUTHORIZATIONS_JSON 	= "src/test/resources/authorizations.json";
	private static final String ELIGIBILITY_JSON 		= "src/test/resources/eligibility.json";
	private static final String CLAIMS_JSON 			= "src/test/resources/claim.json";
	private static final String CLAIMS_STATUS_JSON 		= "src/test/resources/claim_status.json";
	private static final String REFERRALS_JSON 			= "src/test/resources/referrals.json";
	private static final String BOOK_APPOINTMENT_JSON 	= "src/test/resources/book_appointment.json";
	private static final String OPEN_SLOTS_JSON 		= "src/test/resources/open_slots.json";
	private static final String UPDATE_APPOINTMENT_JSON = "src/test/resources/update_appointment.json";

	private static final String BLANK_JSON = "{ \"foo\": \"bar\"}";

	private PokitDok client;
	private PokitDokHTTPConnector mockConnector;

	@Before
	public void connect() throws Exception {
		mockConnector = mock(PokitDokHTTPConnector.class);
		when(mockConnector.get(anyString(), anyMap(), anyMap())).thenReturn(BLANK_JSON);
		when(mockConnector.post(anyString(), anyMap(), anyMap())).thenReturn(BLANK_JSON);

		client = new PokitDok("client_id", "client_secret", mockConnector);
	}

	/* The basics. */

	@Test
	@Category(UnitTests.class)
	public void shouldInstantiateTest() throws Exception {
		assertNotNull(client);
	}

	/* Utility API tests. */

	@Test
	@Category(UnitTests.class)
	public void activitiesTest() throws Exception {
		Map<String, Object> response = client.activities();

		verify(mockConnector).get("activities", null, null);
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void activitiesTestWithParams() throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("param1", "value1");
		Map<String, Object> response = client.activities(params);

		verify(mockConnector).get(eq("activities"), eq(params), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void payersTest() throws Exception {
		Map<String, Object> response = client.payers();

		verify(mockConnector).get("payers", null, null);
	}

	@Test
	@Category(UnitTests.class)
	public void payersTestWithParams() throws Exception {
		Map<String, Object> response = client.payers();
		Map<String, Object> params = new HashMap();
		params.put("param1", "value1");
		response = client.payers(params);

		verify(mockConnector).get(eq("payers"), eq(params), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void tradingPartnerTest() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("trading_partner_id", "MOCKPAYER");
		Map<String, Object> response = client.tradingPartners(params);

		verify(mockConnector).get("tradingpartners/MOCKPAYER", null, null);
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void tradingPartnersTest() throws Exception {
		Map<String, Object> response = client.tradingPartners();

		verify(mockConnector).get("tradingpartners/", null, null);
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void planTest() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", "TX");
		params.put("plan_type", "PPO");
		Map<String, Object> response = client.plans(params);

		verify(mockConnector).get(eq("plans"), eq(params), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void plansTest() throws Exception {
		Map<String, Object> response = client.plans();

		verify(mockConnector).get("plans", null, null);
		assertNotNull(response);
	}

	/* X12/clearinghouse tests. */

	@Test
	@Category(UnitTests.class)
	public void authorizationsTest() throws Exception {
		String authorizations = readEntireFile(AUTHORIZATIONS_JSON);
		Map query = (JSONObject) JSONValue.parse(authorizations);
		Map<String, Object> response = client.authorizations(query);

		verify(mockConnector).post(eq("authorizations/"), eq(query), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void eligibilityTest() throws Exception {
		String queryJSON = readEntireFile(ELIGIBILITY_JSON);
		Map<String, Object> query = (JSONObject) JSONValue.parse(queryJSON);
		Map<String, Object> response = client.eligibility(query);

		verify(mockConnector).post(eq("eligibility/"), eq(query), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void claimsTest() throws Exception {
		String queryJSON = readEntireFile(CLAIMS_JSON);
		Map<String, Object> query = (JSONObject) JSONValue.parse(queryJSON);
		Map<String, Object> response = client.claims(query);

		verify(mockConnector).post(eq("claims/"), eq(query), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void claimsStatusTest() throws Exception {
		String queryJSON = readEntireFile(CLAIMS_STATUS_JSON);
		Map<String, Object> query = (JSONObject) JSONValue.parse(queryJSON);
		Map<String, Object> response = client.claimsStatus(query);

		verify(mockConnector).post(eq("claims/status"), eq(query), anyMap());
		assertNotNull(response);
	}

	public void filesTest() throws Exception {
	}

	@Test
	@Category(UnitTests.class)
	public void referralsTest() throws Exception {
		String queryJSON = readEntireFile(REFERRALS_JSON);
		Map<String, Object> query = (JSONObject) JSONValue.parse(queryJSON);
		Map<String, Object> response = client.referrals(query);

		verify(mockConnector).post(eq("referrals/"), eq(query), anyMap());
		assertNotNull(response);
	}

	/* Data tests. */

	@Test
	@Category(UnitTests.class)
	public void cashPricesTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("cpt_code", "87799");
		query.put("zip_code", "75201");
		Map<String, Object> response = client.cashPrices(query);

		verify(mockConnector).get(eq("prices/cash"), eq(query), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void insurancePricesTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("cpt_code", "87799");
		query.put("zip_code", "29403");
		Map<String, Object> response = client.insurancePrices(query);

		verify(mockConnector).get(eq("prices/insurance"), eq(query), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void mpcTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("name", "surgery");
		Map<String, Object> response = client.mpc(query);

		verify(mockConnector).get(eq("mpc/"), eq(query), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void mpcsTest() throws Exception {
		String mpc = "99213";
		Map<String, Object> response = client.mpc(mpc, new HashMap<String, Object>());

		verify(mockConnector).get(eq("mpc/" + mpc), anyMap(), anyMap());
		assertNotNull(response);
	}

	@Test
	@Category(UnitTests.class)
	public void providersTest() throws Exception {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("npi", "1467560003");
		Map response = client.providers(query);

		verify(mockConnector).get(eq("providers"), eq(query), anyMap());
		assertNotNull(response);
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

    /** Test suite to assemble all unit tests */
    @RunWith(Categories.class)
    @Suite.SuiteClasses(PokitDokUnitTests.class)
    @Categories.IncludeCategory(UnitTests.class)
    public class PokitDokUnitTestSuite {}

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
