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

@Epic("Selenium")
@Feature("Valid Login")
public class SeleniumExampleTests extends SeleniumBaseTest {

    @Story("User logs in successfully")
    @Test(description = "Verify page title of example.com")
    public void testPageTitle() {
        logInfo("Navigating to https://example.com");
        getDriver().get("https://example.com");

        String title = getDriver().getTitle();
        logInfo("Page title is: " + title);

        Assert.assertEquals(title, "Example Domain",
                "Page title should match 'Example Domain'");
        logPass("Title verified: " + title);
    }

    @Test(description = "Find and verify heading text using CSS selector")
    public void testFindElementByCss() {
        getDriver().get("https://example.com");

        WebElement heading = getDriver().findElement(By.cssSelector("h1"));
        String headingText = heading.getText();
        logInfo("Found heading: " + headingText);

        Assert.assertEquals(headingText, "Example Domain");
        logPass("Heading text verified [OK]");
    }

    @Test(description = "Find element using XPath")
    public void testFindElementByXPath() {
        getDriver().get("https://example.com");

        WebElement paragraph = getDriver().findElement(
                By.xpath("//p[contains(text(),'illustrative')]")
        );

        Assert.assertTrue(paragraph.isDisplayed(), "Paragraph should be visible");
        logInfo("Paragraph text (first 60 chars): " + paragraph.getText().substring(0, 60) + "...");
        logPass("XPath element found and visible [OK]");
    }

    @Test(description = "Use explicit wait to wait for element visibility")
    public void testExplicitWait() {
        getDriver().get("https://the-internet.herokuapp.com/dynamic_loading/1");

        logInfo("Clicking Start button...");
        getDriver().findElement(By.cssSelector("#start button")).click();

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        WebElement finishElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
        );

        logInfo("Element appeared: " + finishElement.getText());
        Assert.assertEquals(finishElement.getText(), "Hello World!");
        logPass("Explicit wait worked -- element appeared after dynamic load [OK]");
    }

    @Test(description = "Fill in a login form and verify error message")
    public void testFormInteraction() {
        getDriver().get("https://the-internet.herokuapp.com/login");

        WebElement usernameField = getDriver().findElement(By.id("username"));
        WebElement passwordField = getDriver().findElement(By.id("password"));
        WebElement loginButton   = getDriver().findElement(By.cssSelector("button[type='submit']"));

        logInfo("Entering wrong credentials...");
        usernameField.clear();
        usernameField.sendKeys("wronguser");

        passwordField.clear();
        passwordField.sendKeys("wrongpassword");

        loginButton.click();

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#flash.error"))
        );

        logInfo("Error message: " + errorMsg.getText());
        Assert.assertTrue(errorMsg.getText().contains("Your username is invalid"),
                "Should show invalid credentials error");
        logPass("Form interaction and error validation passed [OK]");
    }

    @Test(description = "Find multiple elements and count them")
    public void testMultipleElements() {
        getDriver().get("https://the-internet.herokuapp.com/checkboxes");

        List<WebElement> checkboxes = getDriver().findElements(
                By.cssSelector("input[type='checkbox']")
        );
        logInfo("Found " + checkboxes.size() + " checkboxes on the page");

        Assert.assertEquals(checkboxes.size(), 2, "Should find exactly 2 checkboxes");

        for (int i = 0; i < checkboxes.size(); i++) {
            logInfo("Checkbox " + (i + 1) + " checked: " + checkboxes.get(i).isSelected());
        }
        logPass("Multiple elements test passed [OK]");
    }
}