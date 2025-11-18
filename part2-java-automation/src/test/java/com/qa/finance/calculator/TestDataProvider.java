package com.qa.finance.calculator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

public class TestDataProvider {
    
    public static SortedMap<LocalDateTime, BigDecimal> buildNavSeries(Object... entries) {
        SortedMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((LocalDateTime) entries[i], new BigDecimal(entries[i + 1].toString()));
        }
        return map;
    }
    
    public static SortedMap<LocalDateTime, BigDecimal> buildCashFlows(Object... entries) {
        SortedMap<LocalDateTime, BigDecimal> map = new TreeMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((LocalDateTime) entries[i], new BigDecimal(entries[i + 1].toString()));
        }
        return map;
    }
    
    public static SortedMap<LocalDateTime, BigDecimal> emptyCashFlows() {
        return new TreeMap<>();
    }
    
    public static class TestData {
        public SortedMap<LocalDateTime, BigDecimal> navSeries;
        public SortedMap<LocalDateTime, BigDecimal> cashFlows;
        public LocalDateTime startDate;
        public LocalDateTime endDate;
        
        public TestData(SortedMap<LocalDateTime, BigDecimal> navSeries, 
                       SortedMap<LocalDateTime, BigDecimal> cashFlows,
                       LocalDateTime startDate,
                       LocalDateTime endDate) {
            this.navSeries = navSeries;
            this.cashFlows = cashFlows;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
    
    public static TestData basicTWRData(LocalDateTime jan1, LocalDateTime jun1, LocalDateTime dec31) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1, "100000.00",
            jun1, "105000.00",
            dec31, "110000.00"
        );
        return new TestData(navSeries, emptyCashFlows(), jan1, dec31);
    }
    
    public static TestData contributionData(LocalDateTime jan1, LocalDateTime jun1, 
                                           LocalDateTime midYear, LocalDateTime dec31) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1, "100000.00",
            jun1, "105000.00",
            midYear, "155000.00",
            dec31, "170500.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            midYear, "50000.00"
        );
        return new TestData(navSeries, cashFlows, jan1, dec31);
    }
    
    public static TestData withdrawalData(LocalDateTime jan1, LocalDateTime jun1, 
                                         LocalDateTime midYear, LocalDateTime dec31) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1, "200000.00",
            jun1, "210000.00",
            midYear, "160000.00",
            dec31, "176000.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            midYear, "-50000.00"
        );
        return new TestData(navSeries, cashFlows, jan1, dec31);
    }
    
    public static TestData multipleCashFlowsData(LocalDateTime jan1, LocalDateTime mar31, 
                                                LocalDateTime jun30, LocalDateTime sep30, 
                                                LocalDateTime dec31) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1, "100000.00",
            mar31, "108000.00",
            jun30, "165000.00",
            sep30, "135000.00",
            dec31, "148500.00"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            LocalDateTime.of(2023, 4, 15, 0, 0), "50000.00",
            LocalDateTime.of(2023, 7, 15, 0, 0), "-20000.00"
        );
        return new TestData(navSeries, cashFlows, jan1, dec31);
    }
    
    public static TestData annualizedReturnData(LocalDateTime start, LocalDateTime end) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            start, "100000.00",
            end, "125000.00"
        );
        return new TestData(navSeries, emptyCashFlows(), start, end);
    }
    
    public static TestData zeroReturnData(LocalDateTime jan1, LocalDateTime dec31) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1, "100000.00",
            dec31, "100000.00"
        );
        return new TestData(navSeries, emptyCashFlows(), jan1, dec31);
    }
    
    public static TestData negativeReturnData(LocalDateTime jan1, LocalDateTime dec31) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            jan1, "100000.00",
            dec31, "85000.00"
        );
        return new TestData(navSeries, emptyCashFlows(), jan1, dec31);
    }
    
    public static TestData realData2000to2003(LocalDateTime start, LocalDateTime end) {
        SortedMap<LocalDateTime, BigDecimal> navSeries = buildNavSeries(
            start, "13324472.09",
            LocalDateTime.of(2001, 5, 1, 0, 0), "13324472.09",
            LocalDateTime.of(2002, 1, 1, 0, 0), "13175932.06",
            end, "13083215.73"
        );
        SortedMap<LocalDateTime, BigDecimal> cashFlows = buildCashFlows(
            start, "-13324472.09"
        );
        return new TestData(navSeries, cashFlows, start, end);
    }
    
    public static TestData realData2018Volatile(LocalDateTime start, LocalDateTime end) {
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
        return new TestData(navSeries, cashFlows, start, end);
    }
    
    public static TestData realData2020Recovery(LocalDateTime start, LocalDateTime end) {
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
        return new TestData(navSeries, cashFlows, start, end);
    }
}
