package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SeleniumBaseTest
 * ----------------
 * Base class for all Selenium tests.
 * Handles: WebDriver setup/teardown, Extent reporting, screenshots on failure.
 *
 * Extend this class in your Selenium test classes.
 */
public class SeleniumBaseTest {

    protected WebDriver driver;
    protected ExtentTest test;

    @BeforeClass
    public void setUpClass() {
        // WebDriverManager automatically downloads the correct ChromeDriver
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp(Method method) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");   // remove for local debugging
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Create an Extent test node for this method
        test = ExtentReportManager.createTest(
                "[Selenium] " + method.getName(),
                "Selenium WebDriver test"
        );
        test.info("Browser started -> Chrome");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Capture screenshot and attach to report
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
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String screenshotsDir = System.getProperty("user.dir") + File.separator
                    + "test-output" + File.separator + "screenshots";
            Path dest = Paths.get(screenshotsDir, testName + "_" + timestamp + ".png");
            Files.createDirectories(dest.getParent());
            Files.copy(src.toPath(), dest);
            return dest.toString();
        } catch (IOException e) {
            System.err.println("Could not take screenshot: " + e.getMessage());
            return null;
        }
    }
}
