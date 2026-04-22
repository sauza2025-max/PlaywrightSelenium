package tests.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.PlaywrightBaseTest;

import java.util.List;
import io.qameta.allure.*;
/**
 * PlaywrightExampleTests
 * ======================
 * Learning examples covering the most common Playwright patterns.
 * Uses the same test sites as the Selenium examples so you can compare both approaches.
 *
 * TOPICS COVERED:
 *  1. Basic navigation & title assertion
 *  2. Locators (Playwright's modern way to find elements)
 *  3. Auto-waiting (Playwright's superpower -- no explicit waits needed!)
 *  4. Form interaction
 *  5. Multiple elements
 *  6. Network interception (unique to Playwright)
 *
 * KEY PLAYWRIGHT CONCEPTS vs SELENIUM:
 *  +-------------------------+------------------------------------------+
 *  | Selenium                | Playwright                               |
 *  +-------------------------+------------------------------------------+
 *  | driver.findElement()    | page.locator()                           |
 *  | WebDriverWait           | Built-in auto-wait (no code needed!)     |
 *  | driver.get(url)         | page.navigate(url)                       |
 *  | element.getText()       | locator.textContent() / innerText()      |
 *  | element.click()         | locator.click()                          |
 *  | element.sendKeys()      | locator.fill()                           |
 *  +-------------------------+------------------------------------------+
 */
@Epic("Playwright")
@Feature("Valid Login")
public class PlaywrightExampleTests extends PlaywrightBaseTest {

    // -- Test 1: Basic Navigation ---------------------------------------------
    /**
     * LESSON: Navigate to a URL and check the title.
     * page.navigate()  -> opens URL (waits for load automatically)
     * page.title()     -> returns <title> tag text
     */
    @Test(description = "Verify page title using Playwright")
    public void testPageTitle() {
        logInfo("Navigating to https://example.com");
        page.navigate("https://example.com");

        String title = page.title();
        logInfo("Page title: " + title);

        Assert.assertEquals(title, "Example Domain");
        logPass("Title verified [OK]");
    }

    // -- Test 2: Locators -----------------------------------------------------
    /**
     * LESSON: Playwright's Locator is smarter than Selenium's WebElement.
     * It retries automatically until the element is ready.
     *
     * Common locator strategies:
     *   page.locator("css selector")         -> CSS (same as Selenium)
     *   page.locator("//xpath")              -> XPath
     *   page.getByText("visible text")       -> find by visible text
     *   page.getByRole(AriaRole.BUTTON)      -> find by ARIA role (accessibility)
     *   page.getByLabel("Email")             -> find form field by its label
     *   page.getByPlaceholder("Search...")  -> find input by placeholder
     */
    @Test(description = "Use different locator strategies")
    public void testLocators() {
        page.navigate("https://example.com");

        // CSS locator
        String headingText = page.locator("h1").innerText();
        logInfo("h1 text: " + headingText);
        Assert.assertEquals(headingText, "Example Domain");

        // getByText -- finds any element with this visible text
        Locator moreInfoLink = page.getByText("More information");
        Assert.assertTrue(moreInfoLink.isVisible(), "More information link should be visible");
        logInfo("Found 'More information' link via getByText [OK]");

        logPass("All locator strategies worked [OK]");
    }

    // -- Test 3: Auto-waiting (Playwright's Superpower) -----------------------
    /**
     * LESSON: Playwright auto-waits for elements to be ready before acting.
     * You don't need WebDriverWait or Thread.sleep().
     * Playwright waits for: visible, stable, enabled, not obscured.
     *
     * This test uses the same dynamic loading page as the Selenium example.
     * Notice: NO explicit wait code needed here!
     */
    @Test(description = "Auto-wait for dynamically loaded element (no explicit wait needed)")
    public void testAutoWaiting() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        logInfo("Clicking Start -- Playwright will auto-wait for the result...");
        page.locator("#start button").click();

        // Playwright automatically waits for #finish h4 to be visible
        // Default timeout is 30 seconds -- no extra code needed!
        String resultText = page.locator("#finish h4").innerText();

        logInfo("Result appeared: " + resultText);
        Assert.assertEquals(resultText, "Hello World!");
        logPass("Auto-wait worked -- no WebDriverWait needed [OK]");
    }

    // -- Test 4: Form Interaction ----------------------------------------------
    /**
     * LESSON: Playwright's form methods are cleaner than Selenium.
     * locator.fill("text")    -> clears and types (better than sendKeys)
     * locator.click()         -> clicks
     * locator.check()         -> checks a checkbox
     * locator.selectOption()  -> selects dropdown option
     */
    @Test(description = "Fill login form and verify error message")
    public void testFormInteraction() {
        page.navigate("https://the-internet.herokuapp.com/login");

        logInfo("Filling in login form with wrong credentials...");
        page.locator("#username").fill("wronguser");
        page.locator("#password").fill("wrongpassword");
        page.locator("button[type='submit']").click();

        // Auto-waits for flash error to appear
        String errorText = page.locator("#flash.error").innerText();
        logInfo("Error message: " + errorText);

        Assert.assertTrue(errorText.contains("Your username is invalid"));
        logPass("Form interaction test passed [OK]");
    }

    // -- Test 5: Multiple Elements ---------------------------------------------
    /**
     * LESSON: locator.all() returns a List<Locator> for multiple matches.
     * locator.count()   -> number of matching elements
     * locator.nth(0)    -> get element at index
     */
    @Test(description = "Work with multiple elements using Playwright")
    public void testMultipleElements() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");

        Locator checkboxes = page.locator("input[type='checkbox']");
        int count = checkboxes.count();
        logInfo("Found " + count + " checkboxes");

        Assert.assertEquals(count, 2);

        List<Locator> checkboxList = checkboxes.all();
        for (int i = 0; i < checkboxList.size(); i++) {
            logInfo("Checkbox " + (i + 1) + " checked: " + checkboxList.get(i).isChecked());
        }
        logPass("Multiple elements test passed [OK]");
    }

    // -- Test 6: Network Interception (Playwright-only feature!) --------------
    /**
     * LESSON: Playwright can intercept and mock network requests.
     * This is NOT possible with Selenium -- it's a major Playwright advantage.
     *
     * Use cases:
     *  - Mock API responses to test UI without a real backend
     *  - Block ads or analytics scripts to speed up tests
     *  - Test error states (simulate 500 errors)
     */
    @Test(description = "Intercept and mock a network request (Playwright-only)")
    public void testNetworkInterception() {
        // Block all image requests to speed up page load
        page.route("**/*.png", route -> {
            logInfo("Intercepted image request: " + route.request().url());
            route.abort(); // block the request
        });

        page.navigate("https://example.com");
        logInfo("Page loaded with images blocked");

        // Page should still load and work without images
        Assert.assertEquals(page.title(), "Example Domain");
        logPass("Network interception worked -- images blocked but page loaded [OK]");
    }

    // -- Test 7: Page Assertions with soft assertions --------------------------
    /**
     * LESSON: Playwright has built-in assertions that auto-wait.
     * assertThat(locator).isVisible()    -> waits until visible or fails
     * assertThat(locator).hasText("x")  -> waits until text matches
     * assertThat(page).hasTitle("x")    -> waits until title matches
     */
    @Test(description = "Use Playwright built-in assertions")
    public void testPlaywrightAssertions() {
        page.navigate("https://example.com");

        // Playwright assertions auto-wait -- much better than Assert.assertEquals
        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(page)
                .hasTitle("Example Domain");

        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(
                page.locator("h1")
        ).isVisible();

        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(
                page.locator("h1")
        ).hasText("Example Domain");

        logPass("All Playwright built-in assertions passed [OK]");
    }
}
