# Part 2 — Time-Weighted Return (TWR) Test Automation

## Objective
As a QA Engineer, this test suite demonstrates the ability to understand new functional areas (TWR calculation) and define and implement appropriate test cases to ensure high-quality feature delivery.

## What is Time-Weighted Return (TWR)?

**Time-Weighted Return (TWR)** is a financial metric that measures the compound rate of growth in a portfolio. It's particularly valuable because it **eliminates the distorting effects of cash flows** (contributions and withdrawals), making it ideal for evaluating investment manager performance.

### TWR Formula
```
TWR = [(1 + R₁) × (1 + R₂) × ... × (1 + Rₙ)] - 1
```

Where each R is the return for a sub-period between cash flows:
```
R = (Ending NAV - Beginning NAV - Net Cash Flow) / Beginning NAV
```

### Why TWR Matters
- **Fair Performance Measurement**: Isolates manager skill from client cash flow timing
- **Industry Standard**: Used by GIPS (Global Investment Performance Standards)
- **Comparability**: Enables apples-to-apples comparison across portfolios

---

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

**Parameters:**
- `externalFlowTimeSeries`: Cash flows (+ = contributions, - = withdrawals)
- `navTimeSeries`: Net Asset Values at various dates
- `evaluationStart`: Start date for TWR calculation
- `evaluationEnd`: End date for TWR calculation
- `annualizeReturn`: If true, returns annualized TWR; else total TWR

---

## Deliverable 1: 10 Implemented Test Cases

### Test Case Summary

| # | Test Case | Category | Purpose |
|---|-----------|----------|---------|
| **TC01** | Basic TWR - No Cash Flows (Positive) | Happy Path | Baseline calculation with simple growth |
| **TC02** | TWR with Single Contribution | Functional | Verify cash flow exclusion (contribution) |
| **TC03** | TWR with Single Withdrawal | Functional | Verify cash flow exclusion (withdrawal) |
| **TC04** | TWR with Multiple Cash Flows | Functional | Complex scenario with mixed flows |
| **TC05** | Annualized vs Non-Annualized TWR | Functional | Verify annualization logic |
| **TC06** | Zero Return Scenario | Boundary | Flat portfolio (0% return) |
| **TC07** | Negative Return Scenario | Boundary | Portfolio loss handling |
| **TC08** | Real-World Data (CSV) | Integration | Actual financial data validation |
| **TC09** | Error Handling - Null Inputs | Negative | Input validation (null safety) |
| **TC10** | Error Handling - Invalid Dates | Negative | Date range validation |

### Detailed Test Case Breakdown

#### **TC01: Basic TWR - No Cash Flows (Positive Return)**
- **Scenario**: Portfolio grows from $100K to $110K over 1 year, no cash flows
- **Expected**: TWR = 10% [(110K/100K) - 1]
- **Validates**: Core calculation logic without cash flow complexity

#### **TC02: TWR with Single Contribution**
- **Scenario**: $100K → $105K (Q1), +$50K contribution, $155K → $170.5K (Q2)
- **Expected**: TWR ≈ 15.5% [(1.05 × 1.10) - 1]
- **Validates**: Contribution doesn't inflate returns

#### **TC03: TWR with Single Withdrawal**
- **Scenario**: $200K → $210K (Q1), -$50K withdrawal, $160K → $176K (Q2)
- **Expected**: TWR ≈ 15.5% [(1.05 × 1.10) - 1]
- **Validates**: Withdrawal doesn't deflate returns

#### **TC04: TWR with Multiple Cash Flows**
- **Scenario**: Multiple contributions and withdrawals across 4 quarters
- **Expected**: TWR correctly chains sub-period returns
- **Validates**: Complex cash flow handling

#### **TC05: Annualized vs Non-Annualized**
- **Scenario**: 25% total return over 2 years
- **Expected**: Non-annualized = 25%, Annualized ≈ 11.8% [√1.25 - 1]
- **Validates**: Annualization formula

#### **TC06: Zero Return**
- **Scenario**: Portfolio remains at $100K for entire period
- **Expected**: TWR = 0%
- **Validates**: Boundary condition (no change)

#### **TC07: Negative Return**
- **Scenario**: Portfolio declines from $100K to $85K
- **Expected**: TWR = -15%
- **Validates**: Loss handling

#### **TC08: Real CSV Data - 2000-2003 Period**
- **Scenario**: 3-year historical period with actual NAV data from CSV
- **Data**: NAV 13324472.09 (Jan 2000) → 13083215.73 (Jan 2003)
- **Expected**: Negative TWR showing portfolio decline over 36 months
- **Validates**: Long-term calculation with real historical data

#### **TC09: Real CSV Data - 2018 Volatility**
- **Scenario**: 4-month volatile period with large $30M withdrawal
- **Data**: NAV and cashflow from Feb-May 2018 CSV entries
- **Expected**: Positive TWR despite massive withdrawal (cash flow isolation)
- **Validates**: TWR eliminates cash flow distortion in volatile periods

#### **TC10: Real CSV Data - 2020-2021 Recovery**
- **Scenario**: 9-month COVID recovery with 5 mixed cash flows
- **Data**: NAV 51100584.33 → 54000000.00, flows ranging $1.5M-$2.1M
- **Expected**: Annualized TWR showing recovery rate
- **Validates**: Multiple flows during market recovery, annualization logic

---

## Deliverable 2: Total Test Case Count & Reasoning

### Recommended Total: **45-60 Test Cases**

#### Breakdown by Category

| Category | Count | Examples |
|----------|-------|----------|
| **Happy Path** | 8-10 | Various growth scenarios, different time periods |
| **Cash Flow Scenarios** | 12-15 | Single/multiple flows, same-day flows, large flows |
| **Boundary Conditions** | 8-10 | Zero return, 100% loss, extreme values, single-day period |
| **Edge Cases** | 6-8 | Missing NAV dates, sparse data, leap years, DST transitions |
| **Negative Tests** | 6-8 | Null inputs, empty series, invalid dates, zero NAV |
| **Performance Tests** | 3-5 | Large datasets (10K+ entries), memory efficiency |
| **Integration Tests** | 5-8 | Real CSV data, cross-validation with finance libraries |

### Reasoning for Test Coverage

#### 1. **Functional Coverage (20-25 tests)**
- **Why**: TWR calculation is mathematically complex with sub-period chaining
- **Focus**: All parameter combinations, various cash flow patterns
- **Example**: Daily/monthly/quarterly cash flows, inflows vs outflows

#### 2. **Boundary Testing (8-10 tests)**
- **Why**: Financial calculations are sensitive to edge values
- **Focus**: Zero, negative, maximum values; minimum time periods
- **Example**: Zero NAV (should error), 100% portfolio loss, 1-day TWR

#### 3. **Real-World Scenarios (5-8 tests)**
- **Why**: Financial data has peculiarities (market holidays, corporate actions)
- **Focus**: Actual historical data, large portfolios, missing dates
- **Example**: Provided CSV data, S&P 500 historical returns

#### 4. **Error Handling (6-8 tests)**
- **Why**: Production code must fail gracefully
- **Focus**: Invalid inputs, data quality issues
- **Example**: Null series, unsorted maps, NAV < 0

#### 5. **Performance Testing (3-5 tests)**
- **Why**: High-frequency trading requires fast calculations
- **Focus**: Large time series (1000+ data points), memory leaks
- **Example**: 10-year daily NAV series, concurrent calculations

#### 6. **Annualization Edge Cases (3-5 tests)**
- **Why**: Annualization formula differs for short vs long periods
- **Focus**: < 1 year, > 10 years, leap years
- **Example**: 3-month annualized TWR, 20-year annualized TWR

### Test Priority (Risk-Based Approach)

| Priority | Risk | Test Count | Rationale |
|----------|------|------------|-----------|
| **P0** | Critical | 15 | Core logic, common scenarios, regulatory compliance |
| **P1** | High | 20 | Cash flow variations, annualization, boundary values |
| **P2** | Medium | 15 | Edge cases, performance, uncommon scenarios |
| **P3** | Low | 5-10 | Extreme outliers, theoretical cases |

### Why NOT More Tests?

**Diminishing Returns**: Beyond 60 tests, cost of maintenance exceeds benefit
- **Redundancy**: Over-testing similar scenarios adds little value
- **Fragility**: Too many tests make refactoring painful
- **Execution Time**: Large suites slow down CI/CD pipelines

**Balance**: Focus on **high-value tests** that catch real bugs efficiently

---

## Running the Tests

### Execute All Tests
```bash
cd part2-java-automation
mvn clean test
```

### Execute Specific Test
```bash
mvn test -Dtest=TimeWeightedReturnCalculatorTest#testCase01_BasicTWR_NoCashFlows_PositiveReturn
```

### View Test Results
```bash
# Console output shows ✅ for each passing test
# Detailed reports: target/surefire-reports/
```

### Generate Coverage Report
```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

---

## Test Data

### Simplified Test Data (Used in TC01-TC07)
- **Purpose**: Clear, predictable scenarios for unit testing
- **Values**: Round numbers ($100K, $200K) for easy mental math
- **Benefit**: Fast debugging when tests fail

### Real-World Test Data (Used in TC08)
- **Source**: Provided `NAV Time Series.csv` and `Cashflow Time Series.csv`
- **Values**: Actual financial data (2000-2023)
- **Benefit**: Validates production-scale accuracy

---

## Technology Stack

- **Language**: Java 17
- **Build**: Maven
- **Testing**: JUnit 5 (Jupiter)
- **Assertions**: AssertJ (fluent, readable)
- **Coverage**: JaCoCo (90% threshold)
- **CI/CD**: GitHub Actions

---

## Test Quality Metrics

### Coverage Goals
- **Line Coverage**: > 95% (core calculator logic)
- **Branch Coverage**: > 90% (all conditional paths)
- **Method Coverage**: 100% (all public methods tested)

### Success Criteria
- ✅ All 10 test cases pass
- ✅ No flaky tests (100% stable across 10 runs)
- ✅ Execution time < 2 seconds (entire suite)
- ✅ Clear failure messages (debugging < 5 minutes)

---

## Key Insights

### What These Tests Validate
1. **Mathematical Correctness**: TWR formula implementation
2. **Cash Flow Isolation**: Contributions/withdrawals don't skew returns
3. **Sub-Period Chaining**: Correct multiplication of period returns
4. **Annualization**: Proper time-scaling for comparability
5. **Robustness**: Graceful error handling for invalid inputs

### What These Tests DON'T Cover (Out of Scope)
- ❌ **UI Testing**: Interface interaction (not part of calculator logic)
- ❌ **Concurrency**: Thread-safety (single-threaded calculation assumed)
- ❌ **Database**: Data persistence (in-memory calculation only)
- ❌ **External APIs**: Market data integration (uses provided data)

---

## Author
**QA Engineering Manager**  
**Date**: November 2025  
**Purpose**: Demonstrate QA expertise for TWR feature testing

---

## Appendix: TWR Calculation Example

### Manual Calculation Walkthrough

**Scenario**: Portfolio with one contribution

| Date | Event | NAV Before | Cash Flow | NAV After |
|------|-------|------------|-----------|-----------|
| 2023-01-01 | Start | $100,000 | - | $100,000 |
| 2023-06-01 | Before CF | $105,000 | - | $105,000 |
| 2023-06-01 | Contribution | $105,000 | +$50,000 | $155,000 |
| 2023-12-31 | End | $170,500 | - | $170,500 |

**Calculation**:
1. **Period 1** (Jan 1 - Jun 1): R₁ = (105K - 100K) / 100K = **5%**
2. **Period 2** (Jun 1 - Dec 31): R₂ = (170.5K - 155K) / 155K = **10%**
3. **TWR** = (1.05 × 1.10) - 1 = **15.5%**

**Why TWR?** Money-Weighted Return would show 14.2%, penalizing the investor for mid-year timing. TWR correctly shows **15.5%** manager performance.
