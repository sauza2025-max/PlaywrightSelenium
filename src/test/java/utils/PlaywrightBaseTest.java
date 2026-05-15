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

public class PlaywrightBaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected ExtentTest test;

    @BeforeClass
    public void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setSlowMo(50)
        );
    }

    @BeforeMethod
    public void setUp(Method method) {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
        );
        page = context.newPage();

        // ✅ Register page so AllureListener can access it
        PageManager.setPage(page);

        test = ExtentReportManager.createTest(
                "[Playwright] " + method.getName(),
                "Playwright test"
        );
        test.info("Browser started -> Chromium (Playwright)");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test FAILED: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test PASSED [OK]");
        } else {
            test.skip("Test SKIPPED");
        }

        // ✅ Do NOT close context here — AllureListener needs page alive for screenshot
        // context.close() moved to listener
        test.info("Browser context closed");
    }

    @AfterClass
    public void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        ExtentReportManager.flush();
    }

    protected void logInfo(String message) { test.log(Status.INFO, message); }
    protected void logPass(String message) { test.log(Status.PASS, message); }
    protected void logFail(String message) { test.log(Status.FAIL, message); }
}