# Playwright + Selenium Learning Project (Java)

A hands-on learning project comparing **Playwright** and **Selenium** side-by-side in Java,
with **TestNG** for test execution and **ExtentReports** for HTML reports.

---

## Project Structure

```
playwright-selenium-learning/
├── pom.xml
└── src/test/java/
    ├── utils/
    │   ├── ExtentReportManager.java   ← Shared HTML report (singleton)
    │   ├── SeleniumBaseTest.java      ← Selenium setup/teardown base class
    │   └── PlaywrightBaseTest.java    ← Playwright setup/teardown base class
    └── tests/
        ├── selenium/
        │   └── SeleniumExampleTests.java   ← 6 Selenium learning tests
        └── playwright/
            └── PlaywrightExampleTests.java ← 7 Playwright learning tests
```

---

## Prerequisites

| Tool      | Version  | Download                          |
|-----------|----------|-----------------------------------|
| Java JDK  | 17+      | https://adoptium.net              |
| Maven     | 3.8+     | https://maven.apache.org          |
| Chrome    | Latest   | https://www.google.com/chrome     |

---

## Setup & Run

### 1. Install Playwright browsers (first time only)
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

### 2. Run all tests
```bash
mvn test
```

### 3. Run only Selenium tests
```bash
mvn test -Dgroups=selenium
```

### 4. Run only Playwright tests
```bash
mvn test -Dgroups=playwright
```

### 5. View the HTML report
Open `test-output/ExtentReport.html` in your browser after the run.

---

## What Each Test Teaches

### Selenium Tests (`SeleniumExampleTests.java`)

| Test | Concept Learned |
|------|----------------|
| `testPageTitle` | `driver.get()`, `getTitle()` |
| `testFindElementByCss` | `By.cssSelector()`, `getText()` |
| `testFindElementByXPath` | `By.xpath()`, XPath syntax |
| `testExplicitWait` | `WebDriverWait`, `ExpectedConditions` |
| `testFormInteraction` | `sendKeys()`, `click()`, `clear()` |
| `testMultipleElements` | `findElements()`, `List<WebElement>` |

### Playwright Tests (`PlaywrightExampleTests.java`)

| Test | Concept Learned |
|------|----------------|
| `testPageTitle` | `page.navigate()`, `page.title()` |
| `testLocators` | `locator()`, `getByText()`, `getByRole()` |
| `testAutoWaiting` | Auto-wait (no explicit wait needed!) |
| `testFormInteraction` | `fill()`, `click()` |
| `testMultipleElements` | `locator.all()`, `locator.count()` |
| `testNetworkInterception` | `page.route()` — mock/block requests |
| `testPlaywrightAssertions` | `assertThat()` built-in assertions |

---

## Selenium vs Playwright — Key Differences

| Feature | Selenium | Playwright |
|---|---|---|
| Wait strategy | Manual `WebDriverWait` | Auto-wait built in |
| Element finding | `driver.findElement(By.*)` | `page.locator()`, `getByText()`, etc. |
| Network mocking | ❌ Not possible | ✅ `page.route()` |
| Multi-browser | Chrome, Firefox, Safari, Edge | Chromium, Firefox, WebKit |
| Speed | Slower (HTTP protocol) | Faster (CDP protocol) |
| Setup | Needs driver (WebDriverManager) | Downloads own browsers |
| Language support | Java, Python, C#, JS, Ruby | Java, Python, C#, JS, TypeScript |

---

## Tips for Learning

1. **Run one test at a time** — set `headless(false)` in base classes to watch the browser
2. **Compare side by side** — the Selenium and Playwright tests do the same things
3. **Add `slowMo`** — Playwright's `setSlowMo(500)` slows actions so you can follow along
4. **Check the report** — open `test-output/ExtentReport.html` to see pass/fail with screenshots
