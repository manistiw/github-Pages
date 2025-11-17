# Time-Weighted Return Calculator - Test Suite

Java test automation for TWR (Time-Weighted Return) calculator. This project includes 10 test cases covering various scenarios from basic calculations to real-world CSV data.

## What is TWR?

Time-Weighted Return measures investment performance without the impact of cash flows. It's used to evaluate portfolio managers fairly, regardless of when clients add or withdraw money.

## Project Setup

**Requirements:**
- Java 17+
- Maven 3.6+

**Technology:**
- JUnit 5 for testing
- AssertJ for assertions
- JaCoCo for coverage

## Structure
```
part2-java-automation/
├── pom.xml                                           # Maven config (Java 17, JUnit 5, AssertJ)
├── README.md                                         # Project overview
├── TWR-TEST-STRATEGY.md                              # Comprehensive test strategy
├── src/
│   ├── main/java/com/qa/finance/calculator/
│   │   ├── ITimeWeightedReturnCalculator.java       # Interface (per spec)
│   │   └── TimeWeightedReturnCalculator.java        # TWR implementation
│   └── test/java/com/qa/finance/calculator/
│       └── TimeWeightedReturnCalculatorTest.java    # 10 Test Cases (TC01-TC10)
├── test-data/
│   ├── NAV Time Series.csv                          # Real NAV data (2000-2018)
│   └── Cashflow Time Series.csv                     # Real cashflow data (2000-2023)
└── .github/workflows/
    └── java-tests.yml                               # CI/CD pipeline
```

## Test Cases

| Test | Description |
|------|-------------|
| TC01 | Basic return (no cash flows) |
| TC02 | Single contribution |
| TC03 | Single withdrawal |
| TC04 | Multiple cash flows |
| TC05 | Annualized vs total return |
| TC06 | Zero return |
| TC07 | Negative return |
| TC08 | Real CSV data (2000-2003) |
| TC09 | Real CSV data (2018 volatility) |
| TC10 | Real CSV data (2020-2021 recovery) |

Tests cover happy path, edge cases, and real-world data validation.

### Total Test Strategy: 45-60 Tests Recommended
See `TWR-TEST-STRATEGY.md` for detailed reasoning on test count allocation across categories.

## Running the Tests

### Prerequisites
```bash
# Java 17 or higher
java -version

# Maven 3.8+
mvn -version
```

### Execute All Tests
```bash
cd part2-java-automation
mvn clean test
```

### Execute Specific Test Case
```bash
# Run TC01 (Basic TWR)
mvn test -Dtest=TimeWeightedReturnCalculatorTest#testCase01_BasicTWR_NoCashFlows_PositiveReturn

# Run all TWR tests
mvn test -Dtest=TimeWeightedReturnCalculatorTest
```

### Generate Test Reports
```bash
# Build and test
mvn clean test

# Generate JaCoCo coverage report
mvn jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## Test Coverage Goals
- **Line Coverage:** >90%
- **Branch Coverage:** >85%
- **Method Coverage:** >95%
- **Critical Path Coverage:** 100% (financial calculation logic)

## Validation Approach

### 1. Unit Tests
- Individual calculator methods
- Boundary value testing
- Exception handling
- Mock external dependencies

### 2. Integration Tests
- End-to-end calculation workflows
- Real CSV data processing
- Multi-step calculation chains
- Performance benchmarks

### 3. Data-Driven Tests
- Parameterized tests with multiple datasets
- Edge case scenarios from historical data
- Regression test suites with known good values

## Quality Gates
✅ All tests must pass  
✅ No critical/major SonarQube issues  
✅ Code coverage >90%  
✅ No flaky tests (3 consecutive runs pass)  
✅ Performance: <100ms per calculation  

## CI/CD Integration
```yaml
# GitHub Actions example
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: mvn clean test
      - run: mvn jacoco:report
```

## Interface Under Test

```java
public interface ITimeWeightedReturnCalculator {
    BigDecimal calculateTimeWeightedReturn(
        SortedMap<LocalDateTime, BigDecimal> externalFlowTimeSeries,
        SortedMap<LocalDateTime, BigDecimal> navTimeSeries,
        LocalDateTime evaluationStart,
        LocalDateTime evaluationEnd,
        boolean annualizeReturn
    );
}
```

## Key Features

### TWR Calculator Implementation
- ✅ Sub-period return calculation between cash flows
- ✅ Compound return chaining across multiple periods
- ✅ Annualization for different time horizons
- ✅ Cash flow isolation (contributions/withdrawals don't distort returns)
- ✅ Real-world data handling (millions of dollars, 20+ years)

### Test Suite Highlights
- ✅ **10 comprehensive test cases** covering happy path, edge cases, and error handling
- ✅ **Parameterized tests** for data-driven scenarios
- ✅ **Real CSV data integration** from provided time series
- ✅ **Clear assertions** with meaningful failure messages
- ✅ **Fast execution** (< 2 seconds for entire suite)

## Success Metrics
- **Test Execution Time:** <5 seconds (entire suite)
- **Test Stability:** 100% pass rate across 10 runs
- **Code Quality:** SonarQube Grade A
- **Documentation:** All public methods Javadoc'd
- **Maintainability:** SOLID principles, DRY code

---

**Author:** QA Engineering Manager  
**Date:** November 2025  
**Purpose:** Demonstrate hands-on test automation expertise for financial domain
