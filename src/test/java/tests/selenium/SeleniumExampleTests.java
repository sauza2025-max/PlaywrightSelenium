package tests.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.SeleniumBaseTest;

import java.time.Duration;
import java.util.List;
import io.qameta.allure.*;
/**
 * SeleniumExampleTests
 * ====================
 * Learning examples covering the most common Selenium patterns.
 * Uses https://example.com and https://the-internet.herokuapp.com (free test sites).
 *
 * TOPICS COVERED:
 *  1. Basic navigation & title assertion
 *  2. Finding elements by ID, CSS selector, XPath
 *  3. Explicit waits (best practice over Thread.sleep)
 *  4. Interacting with forms (input, button click)
 *  5. Handling multiple elements (lists)
 *  6. Verifying element visibility
 */
@Epic("Selenium")
@Feature("Valid Login")
public class SeleniumExampleTests extends SeleniumBaseTest {

    // -- Test 1: Basic Navigation ---------------------------------------------
    /**
     * LESSON: Always start with navigating to a URL and checking the title.
     * driver.get()    -> opens the URL
     * getTitle()      -> returns the page <title> tag text
     */
    @Story("User logs in successfully")
    @Test(description = "Verify page title of example.com")
    public void testPageTitle() {
        logInfo("Navigating to https://example.com");
        driver.get("https://example.com");

        String title = driver.getTitle();
        logInfo("Page title is: " + title);

        Assert.assertEquals(title, "Example Domain",
                "Page title should match 'Example Domain'");
        logPass("Title verified: " + title);
    }

    // -- Test 2: Find Element by CSS Selector ---------------------------------
    /**
     * LESSON: CSS selectors are the most readable way to locate elements.
     * By.cssSelector("h1")        -> finds <h1>
     * By.cssSelector(".class")    -> finds by class
     * By.cssSelector("#id")       -> finds by ID
     * element.getText()           -> reads visible text
     */
    @Test(description = "Find and verify heading text using CSS selector")
    public void testFindElementByCss() {
        driver.get("https://example.com");

        WebElement heading = driver.findElement(By.cssSelector("h1"));
        String headingText = heading.getText();
        logInfo("Found heading: " + headingText);

        Assert.assertEquals(headingText, "Example Domain");
        logPass("Heading text verified [OK]");
    }

    // -- Test 3: Find Element by XPath ----------------------------------------
    /**
     * LESSON: XPath is powerful for complex queries.
     * //tag[@attribute='value']   -> exact attribute match
     * //tag[contains(text(),'x')] -> partial text match
     * Use XPath when CSS selectors aren't enough.
     */
    @Test(description = "Find element using XPath")
    public void testFindElementByXPath() {
        driver.get("https://example.com");

        // Find the paragraph containing "illustrative" text
        WebElement paragraph = driver.findElement(
                By.xpath("//p[contains(text(),'illustrative')]")
        );

        Assert.assertTrue(paragraph.isDisplayed(), "Paragraph should be visible");
        logInfo("Paragraph text (first 60 chars): " + paragraph.getText().substring(0, 60) + "...");
        logPass("XPath element found and visible [OK]");
    }

    // -- Test 4: Explicit Wait -------------------------------------------------
    /**
     * LESSON: Never use Thread.sleep(). Always use WebDriverWait.
     * WebDriverWait polls every 500ms until condition is true or timeout.
     *
     * Common ExpectedConditions:
     *   visibilityOfElementLocated   -> element exists AND is visible
     *   elementToBeClickable         -> element is visible AND enabled
     *   presenceOfElementLocated     -> element exists in DOM (may be hidden)
     *   titleContains                -> page title contains string
     */
    @Test(description = "Use explicit wait to wait for element visibility")
    public void testExplicitWait() {
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");

        logInfo("Clicking Start button...");
        driver.findElement(By.cssSelector("#start button")).click();

        // Wait up to 10 seconds for the finish text to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement finishElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
        );

        logInfo("Element appeared: " + finishElement.getText());
        Assert.assertEquals(finishElement.getText(), "Hello World!");
        logPass("Explicit wait worked -- element appeared after dynamic load [OK]");
    }

    // -- Test 5: Form Interaction ----------------------------------------------
    /**
     * LESSON: Interacting with form fields.
     * element.sendKeys("text")  -> types into an input
     * element.clear()           -> clears existing text first
     * element.click()           -> clicks buttons, checkboxes, links
     */
    @Test(description = "Fill in a login form and verify error message")
    public void testFormInteraction() {
        driver.get("https://the-internet.herokuapp.com/login");

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton   = driver.findElement(By.cssSelector("button[type='submit']"));

        logInfo("Entering wrong credentials...");
        usernameField.clear();
        usernameField.sendKeys("wronguser");

        passwordField.clear();
        passwordField.sendKeys("wrongpassword");

        loginButton.click();

        // Verify error message appears
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#flash.error"))
        );

        logInfo("Error message: " + errorMsg.getText());
        Assert.assertTrue(errorMsg.getText().contains("Your username is invalid"),
                "Should show invalid credentials error");
        logPass("Form interaction and error validation passed [OK]");
    }

    // -- Test 6: Multiple Elements ---------------------------------------------
    /**
     * LESSON: findElements() returns a List -- use when you expect many matches.
     * Useful for tables, lists, dropdowns, repeated components.
     */
    @Test(description = "Find multiple elements and count them")
    public void testMultipleElements() {
        driver.get("https://the-internet.herokuapp.com/checkboxes");

        List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
        logInfo("Found " + checkboxes.size() + " checkboxes on the page");

        Assert.assertEquals(checkboxes.size(), 2, "Should find exactly 2 checkboxes");

        // Check state of each
        for (int i = 0; i < checkboxes.size(); i++) {
            logInfo("Checkbox " + (i + 1) + " checked: " + checkboxes.get(i).isSelected());
        }
        logPass("Multiple elements test passed [OK]");
    }
}
