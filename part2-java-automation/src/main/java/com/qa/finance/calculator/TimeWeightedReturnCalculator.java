package com.qa.finance.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Time-Weighted Return calculator
public class TimeWeightedReturnCalculator implements ITimeWeightedReturnCalculator {
    
    private static final int PRECISION = 10;
    private static final int DAYS_PER_YEAR = 365;
    
    @Override
    public BigDecimal calculateTimeWeightedReturn(
            SortedMap<LocalDateTime, BigDecimal> externalFlowTimeSeries,
            SortedMap<LocalDateTime, BigDecimal> navTimeSeries,
            LocalDateTime evaluationStart,
            LocalDateTime evaluationEnd,
            boolean annualizeReturn) {
        
        validateInputs(externalFlowTimeSeries, navTimeSeries, evaluationStart, evaluationEnd);
        
        BigDecimal startNAV = getNAVAtDate(navTimeSeries, evaluationStart);
        if (startNAV.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Starting NAV cannot be zero");
        }
        
        List<LocalDateTime> cashFlowDates = getCashFlowDatesInPeriod(
                externalFlowTimeSeries, evaluationStart, evaluationEnd);
        
        BigDecimal twrFactor = BigDecimal.ONE;
        LocalDateTime periodStart = evaluationStart;
        
        for (LocalDateTime cashFlowDate : cashFlowDates) {
            BigDecimal subPeriodReturn = calculateSubPeriodReturn(
                    navTimeSeries, externalFlowTimeSeries, periodStart, cashFlowDate);
            
            twrFactor = twrFactor.multiply(BigDecimal.ONE.add(subPeriodReturn));
            periodStart = cashFlowDate;
        }
        
        BigDecimal finalSubPeriodReturn = calculateSubPeriodReturn(
                navTimeSeries, externalFlowTimeSeries, periodStart, evaluationEnd);
        
        twrFactor = twrFactor.multiply(BigDecimal.ONE.add(finalSubPeriodReturn));
        
        BigDecimal twr = twrFactor.subtract(BigDecimal.ONE);
        
        if (annualizeReturn) {
            long totalDays = ChronoUnit.DAYS.between(evaluationStart, evaluationEnd);
            return totalDays == 0 ? BigDecimal.ZERO : annualizeTWR(twr, totalDays).setScale(PRECISION, RoundingMode.HALF_UP);
        }
        
        return twr.setScale(PRECISION, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateSubPeriodReturn(
            SortedMap<LocalDateTime, BigDecimal> navTimeSeries,
            SortedMap<LocalDateTime, BigDecimal> cashFlows,
            LocalDateTime periodStart,
            LocalDateTime periodEnd) {
        
        BigDecimal startNAV = getNAVAtDate(navTimeSeries, periodStart);
        BigDecimal endNAV = getNAVAtDate(navTimeSeries, periodEnd);
        BigDecimal netCashFlow = getNetCashFlowInPeriod(cashFlows, periodStart, periodEnd);
        
        if (startNAV.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal navChange = endNAV.subtract(startNAV).subtract(netCashFlow);
        return navChange.divide(startNAV, PRECISION, RoundingMode.HALF_UP);
    }
    
    private BigDecimal getNAVAtDate(SortedMap<LocalDateTime, BigDecimal> navTimeSeries, LocalDateTime date) {
        if (navTimeSeries.containsKey(date)) {
            return navTimeSeries.get(date);
        }
        
        SortedMap<LocalDateTime, BigDecimal> headMap = navTimeSeries.headMap(date);
        if (headMap.isEmpty()) {
            throw new IllegalArgumentException("No NAV data available for date: " + date);
        }
        
        return headMap.get(headMap.lastKey());
    }
    
    private List<LocalDateTime> getCashFlowDatesInPeriod(
            SortedMap<LocalDateTime, BigDecimal> cashFlows,
            LocalDateTime start,
            LocalDateTime end) {
        
        return cashFlows.keySet().stream()
                .filter(date -> date.isAfter(start) && date.isBefore(end))
                .toList();
    }
    
    private BigDecimal getNetCashFlowInPeriod(
            SortedMap<LocalDateTime, BigDecimal> cashFlows,
            LocalDateTime start,
            LocalDateTime end) {
        
        return cashFlows.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(start) && e.getKey().isBefore(end))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal annualizeTWR(BigDecimal twr, long totalDays) {
        double annualizationFactor = (double) DAYS_PER_YEAR / totalDays;
        double annualizedReturn = Math.pow(1 + twr.doubleValue(), annualizationFactor) - 1;
        
        return BigDecimal.valueOf(annualizedReturn);
    }
    
    private void validateInputs(
            SortedMap<LocalDateTime, BigDecimal> cashFlows,
            SortedMap<LocalDateTime, BigDecimal> navTimeSeries,
            LocalDateTime start,
            LocalDateTime end) {
        
        if (cashFlows == null || navTimeSeries == null) {
            throw new IllegalArgumentException("Time series cannot be null");
        }
        
        if (start == null || end == null) {
            throw new IllegalArgumentException("Evaluation dates cannot be null");
        }
        
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException("Evaluation end must be after start");
        }
        
        if (navTimeSeries.isEmpty()) {
            throw new IllegalArgumentException("NAV time series cannot be empty");
        }
    }
}
