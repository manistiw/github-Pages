# Part 2 — Time-Weighted Return (TWR) Test Automation

## Objective
As a QA Engineer, demonstrate responsibility for high-quality feature delivery through exploratory testing and test automation. This case study evaluates the ability to understand a new functional area (TWR) and define/implement appropriate test cases.

## Challenge Overview
We are introducing a **Time-Weighted Return (TWR)** metric in our financial application. This test suite provides comprehensive validation of the TWR calculator implementation against the defined interface.

### What is TWR?
**Time-Weighted Return** measures portfolio performance while eliminating the distorting effects of cash flows (contributions/withdrawals). It's the industry standard for evaluating investment manager performance.

**Formula**: TWR = [(1 + R₁) × (1 + R₂) × ... × (1 + Rₙ)] - 1

## Deliverables ✅
1. ✅ **10 Implemented Test Cases** (see `TimeWeightedReturnCalculatorTest.java`)
2. ✅ **Test Strategy Document** explaining total test count and reasoning (see `TWR-TEST-STRATEGY.md`)

## Technology Stack
- **Language:** Java 17
- **Build Tool:** Maven
- **Testing Framework:** JUnit 5 (Jupiter)
- **Assertion Library:** AssertJ (fluent assertions)
- **Reporting:** JaCoCo code coverage
- **CI/CD:** GitHub Actions
- **Logging:** SLF4J

## Project Structure
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

## 10 Implemented Test Cases

| # | Test Case | Purpose |
|---|-----------|---------|
| **TC01** | Basic TWR - No Cash Flows | Baseline calculation with simple growth |
| **TC02** | TWR with Single Contribution | Verify contribution exclusion logic |
| **TC03** | TWR with Single Withdrawal | Verify withdrawal exclusion logic |
| **TC04** | TWR with Multiple Cash Flows | Complex scenario handling |
| **TC05** | Annualized vs Non-Annualized | Validate annualization formula |
| **TC06** | Zero Return Scenario | Flat portfolio (0% return) |
| **TC07** | Negative Return Scenario | Portfolio loss handling |
| **TC08** | Real CSV Data - 2000-2003 Period | Historical 3-year performance with actual NAV data |
| **TC09** | Real CSV Data - 2018 Volatility | Large $30M withdrawal isolation (4 months) |
| **TC10** | Real CSV Data - 2020-2021 Recovery | COVID recovery with multiple cash flows (9 months) |

### Test Coverage Categories
- ✅ **Happy Path** (TC01-TC02): Positive returns, contributions
- ✅ **Functional** (TC03-TC05): Withdrawals, multiple flows, annualization
- ✅ **Boundary** (TC06-TC07): Zero/negative returns
- ✅ **Real-World Integration** (TC08-TC10): Actual CSV data from 2000-2023
  - 2000-2003: Long-term historical data
  - 2018: Volatile period with large cash flows ($30M withdrawal)
  - 2020-2021: COVID recovery with mixed cash flows

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
