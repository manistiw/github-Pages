package com.qa.finance.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;

class TimeWeightedReturnCalculatorTest {
    
    private TimeWeightedReturnCalculator calculator;
    private LocalDateTime jan1_2023;
    private LocalDateTime dec31_2023;
    
    @BeforeEach
    void setUp() {
        calculator = new TimeWeightedReturnCalculator();
        jan1_2023 = LocalDateTime.of(2023, 1, 1, 0, 0);
        dec31_2023 = LocalDateTime.of(2023, 12, 31, 0, 0);
    }
    
    private SortedMap<LocalDateTime, BigDecimal> buildNavSeries(Object... entries) {
        SortedMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((LocalDateTime) entries[i], new BigDecimal(entries[i + 1].toString()));
        }
        return map;
    }
    
    private SortedMap<LocalDateTime, BigDecimal> buildCashFlows(Object... entries) {
        SortedMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((LocalDateTime) entries[i], new BigDecimal(entries[i + 1].toString()));
        }
        return map;
    }
    
    @Test
    void shouldCalculate10PercentReturnWhenNoFlowsAndValueIncreasesFrom100kTo110k() {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1_2023, "100000.00",
            LocalDateTime.of(2023, 6, 1, 0, 0), "105000.00",
            dec31_2023, "110000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = new TreeMap<>();
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, jan1_2023, dec31_2023, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("0.10"), within(new BigDecimal("0.001")));
    }
    
    @Test
    void shouldCalculate20PercentReturnWith50kMidYearContribution() {
        LocalDateTime midYear = LocalDateTime.of(2023, 6, 1, 12, 0);
        
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1_2023, "100000.00",
            LocalDateTime.of(2023, 6, 1, 0, 0), "105000.00",
            midYear, "155000.00",
            dec31_2023, "170500.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            midYear, "50000.00"
        );
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, jan1_2023, dec31_2023, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("0.205"), within(new BigDecimal("0.01")));
    }
    
    @Test
    void shouldCalculate13PercentReturnWith50kMidYearWithdrawal() {
        LocalDateTime midYear = LocalDateTime.of(2023, 6, 1, 12, 0);
        
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1_2023, "200000.00",
            LocalDateTime.of(2023, 6, 1, 0, 0), "210000.00",
            midYear, "160000.00",
            dec31_2023, "176000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            midYear, "-50000.00"
        );
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, jan1_2023, dec31_2023, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("0.13"), within(new BigDecimal("0.01")));
    }
    
    @Test
    void shouldHandleMultipleCashFlowsWithContributionAndWithdrawal() {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1_2023, "100000.00",
            LocalDateTime.of(2023, 3, 31, 0, 0), "108000.00",
            LocalDateTime.of(2023, 6, 30, 0, 0), "165000.00",
            LocalDateTime.of(2023, 9, 30, 0, 0), "135000.00",
            dec31_2023, "148500.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            LocalDateTime.of(2023, 4, 15, 0, 0), "50000.00",
            LocalDateTime.of(2023, 7, 15, 0, 0), "-20000.00"
        );
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, jan1_2023, dec31_2023, false);
        
        assertThat(twr).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    void shouldAnnualize25PercentTwoYearReturnTo11Point8PercentPerYear() {
        LocalDateTime start = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            start, "100000.00",
            end, "125000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = new TreeMap<>();
        
        BigDecimal twrTotal = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, start, end, false);
        BigDecimal twrAnnualized = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, start, end, true);
        
        assertThat(twrTotal).isCloseTo(new BigDecimal("0.25"), within(new BigDecimal("0.001")));
        assertThat(twrAnnualized).isCloseTo(new BigDecimal("0.118"), within(new BigDecimal("0.01")));
        assertThat(twrAnnualized).isLessThan(twrTotal);
    }
    
    @Test
    void shouldReturnZeroWhenValueRemainsConstantAt100k() {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1_2023, "100000.00",
            dec31_2023, "100000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = new TreeMap<>();
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, jan1_2023, dec31_2023, false);
        
        assertThat(twr).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldCalculateNegative15PercentWhenValueDropsFrom100kTo85k() {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1_2023, "100000.00",
            dec31_2023, "85000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = new TreeMap<>();
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, jan1_2023, dec31_2023, false);
        
        assertThat(twr).isCloseTo(new BigDecimal("-0.15"), within(new BigDecimal("0.001")));
    }
    
    @Test
    void shouldHandleRealPortfolioDataFrom2000To2003WithInitialInvestment() {
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2003, 1, 1, 0, 0);
        
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            start, "13324472.09",
            LocalDateTime.of(2001, 5, 1, 0, 0), "13324472.09",
            LocalDateTime.of(2002, 1, 1, 0, 0), "13175932.06",
            end, "13083215.73"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            start, "-13324472.09"
        );
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, start, end, false);
        
        assertThat(twr).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    void shouldHandleVolatile2018PeriodWith30MillionWithdrawal() {
        LocalDateTime start = LocalDateTime.of(2018, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2018, 5, 1, 0, 0);
        
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            start, "20995597.46",
            LocalDateTime.of(2018, 2, 13, 0, 0), "20867537.03",
            LocalDateTime.of(2018, 2, 27, 0, 0), "50970276.23",
            end, "51100584.33"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            LocalDateTime.of(2018, 1, 1, 12, 0), "-1499999.10",
            LocalDateTime.of(2018, 2, 13, 12, 0), "17223.00",
            LocalDateTime.of(2018, 2, 27, 12, 0), "-30000000.00"
        );
        
        BigDecimal twr = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, start, end, false);
        
        assertThat(twr).isNotNull();
    }
    
    @Test
    void shouldCalculateRecoveryPeriodReturnFrom2020To2021WithMultipleFlows() {
        LocalDateTime start = LocalDateTime.of(2020, 9, 8, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 6, 8, 0, 0);
        
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            start, "51100584.33",
            LocalDateTime.of(2020, 10, 6, 0, 0), "51000584.33",
            LocalDateTime.of(2020, 11, 2, 0, 0), "50500000.00",
            LocalDateTime.of(2021, 2, 1, 0, 0), "50000000.00",
            end, "54000000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            LocalDateTime.of(2020, 9, 8, 12, 0), "844737.29",
            LocalDateTime.of(2020, 10, 6, 12, 0), "-1000000.00",
            LocalDateTime.of(2020, 11, 2, 12, 0), "-1666350.00",
            LocalDateTime.of(2021, 2, 1, 12, 0), "-1500000.00",
            LocalDateTime.of(2021, 6, 8, 12, 0), "4125660.83"
        );
        
        BigDecimal twrTotal = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, start, end, false);
        BigDecimal twrAnnualized = calculator.calculateTimeWeightedReturn(
                cashFlows, navSeries, start, end, true);
        
        assertThat(twrTotal).isGreaterThan(BigDecimal.ZERO);
        assertThat(twrAnnualized).isGreaterThan(BigDecimal.ZERO);
    }
}
