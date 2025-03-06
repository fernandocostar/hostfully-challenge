
# 📌 Project Structure and Test Organization

## `src/test/java/com.hostfully.api`

### `config/`
Contains basic configurations for the project, such as logging and test standards.

### `helpers/`
Provides utility methods to reduce code duplication and improve test maintainability.

### `tests/`
Organized into different test groups:

#### 📂 `booking/` - Tests related to booking properties:
- **`BookingCreationTests`** - Covers the creation of new bookings.
- **`BookingListingTests`** - Tests for retrieving a list of all bookings.
- **`BookingUpdateTests`** - Handles updates to bookings, including cancellation, rebooking, and guest information changes.

#### 📂 `property/` - Tests for property management and queries:
- **`PropertiesListingTests`** - Verifies listing all properties.
- **`PropertyCreationTests`** - Tests property creation functionality.
- **`PropertySearchTests`** - Tests searching for an individual property by its ID.

#### 📂 `utils/` - Utility classes to support test execution:
- **`DateUtils`** - Contains methods for handling date-related operations.
- **`FileUtils`** - Provides file-related utility methods.

#### 📂 `requests/` - DTO factories:
- Contains helper classes to create request payloads, reducing duplication and improving maintainability.

---

## 📂 `src/test/resources`

### 📂 `schemas/`
JSON schema definitions to validate API responses. Organized as:
- **`booking/`** - JSON schemas for booking validation.
- **`common/`** - Common validation schemas, such as error responses.
- **`property/`** - JSON schemas for property-related validation.

---

This structure ensures a well-organized and maintainable test suite, promoting reusability and consistency.

---

# ▶️ Setup and Test Execution with Allure

This guide will walk you through setting up the necessary environment, installing dependencies, running tests, generating an Allure report, and opening the report.

## Prerequisites

- **Java**: Version 21
- **Maven**: Version 3.9.5
- **Allure**: For generating and viewing test reports

## 1. Install Java

### On macOS/Linux:
To install Java, we recommend using **SDKMAN** (or **Homebrew** on macOS). 

#### Install SDKMAN (macOS/Linux):

```sh
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

Then install Java (e.g., OpenJDK 11):

```sh
sdk install java 21.0.3-tem
```

Verify the installation:

```sh
java -version
```

### On Windows:
Download Java from the [official website](https://adoptopenjdk.net/) and follow the installation instructions.

After installation, add the `bin` directory of the Java JDK to your system's `PATH` environment variable.

## 2. Install Maven

### On macOS/Linux:

#### Install using Homebrew (macOS):

```sh
brew install maven
```

#### Install using SDKMAN (Linux):

```sh
sdk install maven
```

Verify the installation:

```sh
mvn -v
```

### On Windows:
Download Maven from the [official website](https://maven.apache.org/download.cgi) and follow the installation instructions. Make sure to add Maven's `bin` directory to your system's `PATH`.

## 3. Install Allure Command-Line Tool

### On macOS/Linux:
Install Allure using Homebrew:

```sh
brew install allure
```

Alternatively, on Linux, you can download the Allure command-line tool from the [Allure releases page](https://github.com/allure-framework/allure2/releases) and extract it to a directory in your `PATH`.

To verify the installation:

```sh
allure --version
```

### On Windows:
Download Allure from the [Allure releases page](https://github.com/allure-framework/allure2/releases) and follow the instructions for setting up Allure on Windows. Add the `bin` folder to your system `PATH`.

## 4. Install Project Dependencies Without Running Tests

Once you have Java and Maven set up, navigate to your project directory and run:

```sh
mvn install -DskipTests
```

This command will download all the required dependencies and install them without running the tests.

## 5. Run the Tests

To run your tests with Maven, use:

```sh
mvn test
```

This will execute the tests in your project.

## 6. Generate the Allure Report

After running your tests, you can generate the Allure report by running:

```sh
allure generate allure-results -o allure-report --clean
```

This will generate the Allure report in the `allure-results` directory.

An alternative is to generate a single file format, which results in an html file and does not depend on allure to open - just open the file itself is enough
```sh
allure generate allure-results -o allure-report --clean --single-file
```

## 7. Open the Allure Report

Once the report is generated, you can open it using the following command:

```sh
allure open allure-report
```

This will automatically start a web server and open the Allure report in your default browser.

Alternatively, you can manually open the report by navigating to the `target/site/allure-maven-plugin` folder in your project and opening the `index.html` file in a web browser.

## Troubleshooting

- **Java not found**: Ensure that Java is correctly installed and the `JAVA_HOME` environment variable is set.
- **Maven not found**: Ensure that Maven is correctly installed and the `MAVEN_HOME` environment variable is set.
- **Allure not found**: Ensure that Allure is installed and available in your `PATH`.
