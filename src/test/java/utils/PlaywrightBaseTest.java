package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PlaywrightBaseTest
 * ------------------
 * Base class for all Playwright tests.
 * Handles: Playwright/Browser/Page lifecycle, Extent reporting, screenshots on failure.
 *
 * Extend this class in your Playwright test classes.
 *
 * KEY DIFFERENCE FROM SELENIUM:
 *   Playwright manages its own browser binaries -- no WebDriverManager needed.
 *   One Playwright instance per class, one Page per test method.
 */
public class PlaywrightBaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected ExtentTest test;

    @BeforeClass
    public void setUpClass() {
        playwright = Playwright.create();

        // Launch Chromium in headless mode
        // TIP: Change to playwright.firefox() or playwright.webkit() to test other browsers!
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)          // set false for local debugging
                        .setSlowMo(50)              // slows down actions by 50ms -- great for learning
        );
    }

    @BeforeMethod
    public void setUp(Method method) {
        // Each test gets its own isolated browser context (like a fresh incognito window)
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
        );
        page = context.newPage();

        test = ExtentReportManager.createTest(
                "[Playwright] " + method.getName(),
                "Playwright test"
        );
        test.info("Browser started -> Chromium (Playwright)");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = takeScreenshot(result.getName());
            test.fail("Test FAILED: " + result.getThrowable().getMessage());
            if (screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test PASSED [OK]");
        } else {
            test.skip("Test SKIPPED");
        }

        if (context != null) context.close();
        test.info("Browser context closed");
    }

    @AfterClass
    public void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        ExtentReportManager.flush();
    }

    // -- Helpers --------------------------------------------------------------

    protected void logInfo(String message) {
        test.log(Status.INFO, message);
    }

    protected void logPass(String message) {
        test.log(Status.PASS, message);
    }

    protected void logFail(String message) {
        test.log(Status.FAIL, message);
    }

    private String takeScreenshot(String testName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String screenshotsDir = System.getProperty("user.dir") + java.io.File.separator
                    + "test-output" + java.io.File.separator + "screenshots";
            Path dest = Paths.get(screenshotsDir, testName + "_" + timestamp + ".png");
            java.nio.file.Files.createDirectories(dest.getParent());
            page.screenshot(new Page.ScreenshotOptions().setPath(dest).setFullPage(true));
            return dest.toString();
        } catch (Exception e) {
            System.err.println("Could not take screenshot: " + e.getMessage());
            return null;
        }
    }
}
