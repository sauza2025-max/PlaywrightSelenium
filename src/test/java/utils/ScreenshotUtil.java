package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

// src/main/java/utils/ScreenshotUtil.java
public class ScreenshotUtil {

    public static String capture(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dir = "target/screenshots/";

        try {
            Files.createDirectories(Paths.get(dir));
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Paths.get(dir, testName + "_" + timestamp + ".png");
            Files.copy(src.toPath(), dest);
            return dest.toAbsolutePath().toString();   // Path, so toAbsolutePath() works
        } catch (IOException e) {
            System.err.println("Could not take screenshot: " + e.getMessage());
            return null;
        }
    }
}
