package com.pokitdok.tests;

import com.pokitdok.tests.categories.*;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.Categories;
import org.junit.runner.*;
import org.junit.runners.Suite;
import static org.mockito.Mockito.*;

public class PokitDokIntegrationTests {
    @Test
    @Category(IntegrationTests.class)
    public void failHard() throws Exception {
		assertTrue(false);
	}

    /** Test suite to assemble all integration tests */
    @RunWith(Categories.class)
    @Suite.SuiteClasses(PokitDokIntegrationTests.class)
    @Categories.IncludeCategory(IntegrationTests.class)
    public class PokitDokIntegrationTestSuite {}
}
