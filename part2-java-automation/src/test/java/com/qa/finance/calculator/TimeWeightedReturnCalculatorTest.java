package com.qa.finance.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.qa.finance.calculator.TestDataProvider.*;
import static org.assertj.core.api.Assertions.*;

class TimeWeightedReturnCalculatorTest {
    
    private TimeWeightedReturnCalculator calculator;
    private LocalDateTime jan1_2023;
    private LocalDateTime dec31_2023;
    private LocalDateTime midYear_2023;
    private LocalDateTime jun1_2023;
    private LocalDateTime mar31_2023;
    private LocalDateTime jun30_2023;
    private LocalDateTime sep30_2023;
    
    @BeforeEach
    void setUp() {
        calculator = new TimeWeightedReturnCalculator();
        jan1_2023 = LocalDateTime.of(2023, 1, 1, 0, 0);
        mar31_2023 = LocalDateTime.of(2023, 3, 31, 0, 0);
        jun1_2023 = LocalDateTime.of(2023, 6, 1, 0, 0);
        jun30_2023 = LocalDateTime.of(2023, 6, 30, 0, 0);
        sep30_2023 = LocalDateTime.of(2023, 9, 30, 0, 0);
        dec31_2023 = LocalDateTime.of(2023, 12, 31, 0, 0);
        midYear_2023 = LocalDateTime.of(2023, 6, 1, 12, 0);
    }
    
    @Test
    void shouldCalculate10PercentReturnWhenNoFlowsAndValueIncreasesFrom100kTo110k() {
        TestDataProvider.TestData data = basicTWRData(jan1_2023, jun1_2023, dec31_2023);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("0.10"), within(new BigDecimal("0.001")));
    }
    
    @Test
    void shouldCalculate20PercentReturnWith50kMidYearContribution() {
        TestDataProvider.TestData data = contributionData(jan1_2023, jun1_2023, midYear_2023, dec31_2023);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("0.205"), within(new BigDecimal("0.01")));
    }
    
    @Test
    void shouldCalculate13PercentReturnWith50kMidYearWithdrawal() {
        TestDataProvider.TestData data = withdrawalData(jan1_2023, jun1_2023, midYear_2023, dec31_2023);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("0.13"), within(new BigDecimal("0.01")));
    }
    
    @Test
    void shouldHandleMultipleCashFlowsWithContributionAndWithdrawal() {
        TestDataProvider.TestData data = multipleCashFlowsData(jan1_2023, mar31_2023, jun30_2023, sep30_2023, dec31_2023);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    void shouldAnnualize25PercentTwoYearReturnTo11Point8PercentPerYear() {
        LocalDateTime start = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        TestDataProvider.TestData data = annualizedReturnData(start, end);
        
        BigDecimal twrTotal = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        BigDecimal twrAnnualized = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, true);
        
        assertThat(twrTotal).isCloseTo(new BigDecimal("0.25"), within(new BigDecimal("0.001")));
        assertThat(twrAnnualized).isCloseTo(new BigDecimal("0.118"), within(new BigDecimal("0.01")));
        assertThat(twrAnnualized).isLessThan(twrTotal);
    }
    
    @Test
    void shouldReturnZeroWhenValueRemainsConstantAt100k() {
        TestDataProvider.TestData data = zeroReturnData(jan1_2023, dec31_2023);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldCalculateNegative15PercentWhenValueDropsFrom100kTo85k() {
        TestDataProvider.TestData data = negativeReturnData(jan1_2023, dec31_2023);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("-0.15"), within(new BigDecimal("0.001")));
    }
    
    @Test
    void shouldHandleRealPortfolioDataFrom2000To2003WithInitialInvestment() {
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2003, 1, 1, 0, 0);
        TestDataProvider.TestData data = realData2000to2003(start, end);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    void shouldHandleVolatile2018PeriodWith30MillionWithdrawal() {
        LocalDateTime start = LocalDateTime.of(2018, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2018, 5, 1, 0, 0);
        TestDataProvider.TestData data = realData2018Volatile(start, end);
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        
        assertThat(twr).isNotNull();
    }
    
    @Test
    void shouldCalculateRecoveryPeriodReturnFrom2020To2021WithMultipleFlows() {
        LocalDateTime start = LocalDateTime.of(2020, 9, 8, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 6, 8, 0, 0);
        TestDataProvider.TestData data = realData2020Recovery(start, end);
        
        BigDecimal twrTotal = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, false);
        BigDecimal twrAnnualized = calculator.calculateTimeWeightedReturn(
                data.cashFlows, data.navSeries, data.startDate, data.endDate, true);
        
        assertThat(twrTotal).isGreaterThan(BigDecimal.ZERO);
        assertThat(twrAnnualized).isGreaterThan(BigDecimal.ZERO);
    }
}
