# SauceDemo Selenium Automation Framework

A UI test automation framework for [saucedemo.com](https://www.saucedemo.com/), built with
**Selenium WebDriver, Java, TestNG, and Maven**, following the **Page Object Model (POM)**.
Includes a SQL-based backend validation suite using an embedded H2 database.

## Tech Stack

| Layer              | Tool / Library                          |
|--------------------|------------------------------------------|
| Language           | Java 17                                   |
| UI Automation      | Selenium WebDriver 4.21                   |
| Test Framework     | TestNG 7.10                               |
| Build Tool         | Maven                                     |
| Driver Management  | WebDriverManager (Bonigarcia)             |
| Database (for validation demo) | H2 (embedded, in-memory)        |

## Project Structure

```
src/main/java/pages/     Page Object classes (Login, Inventory, Cart, Checkout...)
src/main/java/utils/     DriverFactory - browser/driver setup
src/main/java/db/        DatabaseHelper - JDBC + SQL validation helper
src/test/java/base/      BaseTest - TestNG setup/teardown hooks
src/test/java/tests/     Test classes (Login, Inventory, Checkout, Database)
src/test/resources/      testng.xml suite file
```

## What This Project Demonstrates

- **Page Object Model** — every screen (Login, Inventory, Cart, Checkout Steps,
  Confirmation) is its own class; tests never touch locators directly.
- **Explicit waits** — all interactions go through `BasePage` helper methods
  (`waitForVisible`, `waitForClickable`) instead of hardcoded sleeps, to handle
  dynamic page state reliably.
- **Data-driven testing** — `LoginTest` uses a TestNG `@DataProvider` to run the
  same test logic against multiple invalid-credential combinations.
- **End-to-end flow testing** — `CheckoutTest` walks the full journey from login
  through order confirmation, and also verifies validation errors on missing input.
- **SQL / backend validation** — `DatabaseValidationTest` exercises full CRUD
  (Create, Read via JOIN, Update, Delete) against an embedded H2 database, modeling
  how order data captured during a UI checkout would be cross-checked against
  backend records in a real system.
- **CI-friendly design** — headless mode is a runtime flag (`-Dheadless=true`),
  not a code change, so the same suite runs locally or in a pipeline.

## Running the Tests

Prerequisites: Java 17+, Maven, and Google Chrome installed.

```bash
# Clone the repo
git clone https://github.com/<your-username>/saucedemo-automation-framework.git
cd saucedemo-automation-framework

# Run the full suite (visible browser)
mvn clean test

# Run headless (e.g. in CI or without a display)
mvn clean test -Dheadless=true
```

Test results are generated under `target/surefire-reports/` and `test-output/`
(TestNG's own HTML/XML report).

To generate a clean HTML summary report:

```bash
mvn surefire-report:report-only
```

This creates `target/site/surefire-report.html` — open it in a browser for a
readable pass/fail summary with timings, instead of parsing terminal output
or raw XML.

## Test Scenarios Covered

**Login**
- Valid login reaches the inventory page
- Locked-out user sees the correct error
- Multiple invalid credential combinations are rejected (data-driven)

**Inventory & Cart**
- Adding one item updates the cart badge
- Adding multiple items updates the cart badge correctly
- Cart page item count matches items added

**Checkout**
- Full end-to-end checkout flow reaches order confirmation
- Checkout blocks progress when required fields are missing

**Database Validation**
- Order total correctly equals price × quantity (JOIN query)
- Order status updates correctly after confirmation (UPDATE)
- Cancelled orders can be deleted (DELETE)

## Continuous Integration

A GitHub Actions workflow (`.github/workflows/tests.yml`) runs the full suite
headlessly on every push and pull request to `main`/`master`, and uploads both
the raw surefire results and the HTML report as build artifacts. Trigger it
manually any time from the **Actions** tab via `workflow_dispatch`.

## Possible Next Steps

- Extend with Appium for mobile web/app coverage
- Parameterize base URL/environment for cross-environment runs
- Add Allure for even richer, interactive HTML reporting

---
Built as a personal practice project to demonstrate UI automation, framework
design, and SQL validation skills for QA/Automation Test Engineer roles.
