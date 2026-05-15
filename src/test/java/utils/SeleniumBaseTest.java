package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.AfterSuite;
import java.io.*;
import java.util.Properties;

import java.lang.reflect.Method;

public class SeleniumBaseTest {

    public ExtentTest test;

    // ✅ Always get driver from DriverManager
    public org.openqa.selenium.WebDriver getDriver() {
        return DriverManager.getDriver();
    }

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

        // ✅ Store driver in DriverManager so listener can access it
        DriverManager.setDriver(new ChromeDriver(options));
        DriverManager.getDriver().manage().window().maximize();

        test = ExtentReportManager.createTest(
                "[Selenium] " + method.getName(),
                "Selenium WebDriver test"
        );
        test.info("Browser started -> Chrome");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // ✅ Leave empty — AllureListener will quit driver after screenshot
    }

    @AfterClass
    public void tearDownClass() {
        ExtentReportManager.flush();
    }


        @AfterSuite
        public void setAllureEnvironment() throws IOException {
            Properties props = new Properties();
            props.setProperty("Browser", "Chrome");
            props.setProperty("Browser.Version", "120.0");
            props.setProperty("Environment", "QA");
            props.setProperty("Base.URL", "https://your-app.com");
            props.setProperty("OS", System.getProperty("os.name"));

            File allureResultsDir = new File("allure-results");
            if (!allureResultsDir.exists()) allureResultsDir.mkdirs();

            try (FileOutputStream fos = new FileOutputStream(
                    new File(allureResultsDir, "environment.properties"))) {
                props.store(fos, "Allure Environment");
            }
        }

    protected void logInfo(String message) { test.log(Status.INFO, message); }
    protected void logPass(String message) { test.log(Status.PASS, message); }
    protected void logFail(String message) { test.log(Status.FAIL, message); }
}