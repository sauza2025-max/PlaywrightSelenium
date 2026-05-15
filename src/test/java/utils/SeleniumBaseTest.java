package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class SeleniumBaseTest {

    protected WebDriver driver;
    protected ExtentTest test;

    @BeforeClass
    public void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp(Method method) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        test = ExtentReportManager.createTest(
                "[Selenium] " + method.getName(),
                "Selenium WebDriver test"
        );
        test.info("Browser started -> Chrome");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Capture screenshot for every test and store the returned path
        String screenshotPath = ScreenshotUtil.capture(driver, result.getMethod().getMethodName());

        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test FAILED: " + result.getThrowable().getMessage());
            if (screenshotPath != null) {
                try {
                    test.fail("Failure screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } catch (Exception e) {
                    test.fail("Could not attach screenshot: " + e.getMessage());
                }
            }

        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test PASSED [OK]");
            if (screenshotPath != null) {
                try {
                    test.pass("Pass screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } catch (Exception e) {
                    test.warning("Could not attach screenshot: " + e.getMessage());
                }
            }

        } else {
            test.skip("Test SKIPPED");
        }

        if (driver != null) {
            driver.quit();
            test.info("Browser closed");
        }
    }

    @AfterClass
    public void flushReport() {
        ExtentReportManager.flush();
    }

    // -- Helpers --------------------------------------------------------------

    protected void logInfo(String message)  { test.log(Status.INFO,  message); }
    protected void logPass(String message)  { test.log(Status.PASS,  message); }
    protected void logFail(String message)  { test.log(Status.FAIL,  message); }
}