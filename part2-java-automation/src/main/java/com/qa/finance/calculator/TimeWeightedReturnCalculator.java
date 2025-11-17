package com.qa.finance.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implementation of Time-Weighted Return (TWR) Calculator.
 * <p>
 * TWR measures investment performance by eliminating the impact of cash flows,
 * making it ideal for comparing portfolio managers.
 * <p>
 * Formula: TWR = [(1 + R1) × (1 + R2) × ... × (1 + Rn)] - 1
 * <p>
 * Where each R is the return for a sub-period between cash flows:
 * R = (Ending NAV - Beginning NAV - Net Cash Flow) / Beginning NAV
 * <p>
 * For annualized returns: Annualized TWR = [(1 + TWR)^(365/days)] - 1
 */
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
        
        // Get NAV at start
        BigDecimal startNAV = getNAVAtDate(navTimeSeries, evaluationStart);
        
        if (startNAV.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Starting NAV cannot be zero");
        }
        
        // Get cash flows within evaluation period
        List<LocalDateTime> cashFlowDates = getCashFlowDatesInPeriod(
                externalFlowTimeSeries, evaluationStart, evaluationEnd);
        
        // Calculate TWR using sub-period method
        BigDecimal twrFactor = BigDecimal.ONE;
        LocalDateTime periodStart = evaluationStart;
        
        for (LocalDateTime cashFlowDate : cashFlowDates) {
            // Calculate return for sub-period before this cash flow
            BigDecimal subPeriodReturn = calculateSubPeriodReturn(
                    navTimeSeries, externalFlowTimeSeries, periodStart, cashFlowDate);
            
            twrFactor = twrFactor.multiply(BigDecimal.ONE.add(subPeriodReturn));
            periodStart = cashFlowDate;
        }
        
        // Calculate return for final sub-period
        BigDecimal finalSubPeriodReturn = calculateSubPeriodReturn(
                navTimeSeries, externalFlowTimeSeries, periodStart, evaluationEnd);
        
        twrFactor = twrFactor.multiply(BigDecimal.ONE.add(finalSubPeriodReturn));
        
        // TWR = (factor - 1)
        BigDecimal twr = twrFactor.subtract(BigDecimal.ONE);
        
        // Annualize if requested
        if (annualizeReturn) {
            long totalDays = ChronoUnit.DAYS.between(evaluationStart, evaluationEnd);
            if (totalDays == 0) {
                return BigDecimal.ZERO;
            }
            twr = annualizeTWR(twr, totalDays);
        }
        
        return twr.setScale(PRECISION, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate return for a sub-period between cash flows.
     * Formula: R = (Ending NAV - Beginning NAV - Net Cash Flow) / Beginning NAV
     *
     * @param navTimeSeries the NAV time series
     * @param cashFlows the cash flow time series
     * @param periodStart start of the sub-period
     * @param periodEnd end of the sub-period
     * @return the sub-period return
     */
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
        
        // R = (End - Start - CashFlow) / Start
        BigDecimal navChange = endNAV.subtract(startNAV).subtract(netCashFlow);
        return navChange.divide(startNAV, PRECISION, RoundingMode.HALF_UP);
    }
    
    /**
     * Get NAV at a specific date. Returns exact NAV if available,
     * otherwise returns the most recent NAV before the date.
     *
     * @param navTimeSeries the NAV time series
     * @param date the date to retrieve NAV for
     * @return NAV value at or before the date
     * @throws IllegalArgumentException if no NAV data exists before the date
     */
    private BigDecimal getNAVAtDate(SortedMap<LocalDateTime, BigDecimal> navTimeSeries, LocalDateTime date) {
        // Check if exact date exists first
        if (navTimeSeries.containsKey(date)) {
            return navTimeSeries.get(date);
        }
        
        // Get closest NAV before the date
        SortedMap<LocalDateTime, BigDecimal> headMap = navTimeSeries.headMap(date);
        if (headMap.isEmpty()) {
            throw new IllegalArgumentException("No NAV data available for date: " + date);
        }
        
        return headMap.get(headMap.lastKey());
    }
    
    /**
     * Get cash flow dates within the evaluation period (exclusive of boundaries).
     *
     * @param cashFlows the cash flow time series
     * @param start start of evaluation period
     * @param end end of evaluation period
     * @return list of cash flow dates between start and end
     */
    private List<LocalDateTime> getCashFlowDatesInPeriod(
            SortedMap<LocalDateTime, BigDecimal> cashFlows,
            LocalDateTime start,
            LocalDateTime end) {
        
        return cashFlows.keySet().stream()
                .filter(date -> date.isAfter(start) && date.isBefore(end))
                .toList();
    }
    
    /**
     * Calculate net cash flow within a period [start, end).
     * Positive values = contributions, negative values = withdrawals.
     *
     * @param cashFlows the cash flow time series
     * @param start start of period (inclusive)
     * @param end end of period (exclusive)
     * @return sum of all cash flows in the period
     */
    private BigDecimal getNetCashFlowInPeriod(
            SortedMap<LocalDateTime, BigDecimal> cashFlows,
            LocalDateTime start,
            LocalDateTime end) {
        
        return cashFlows.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(start) && e.getKey().isBefore(end))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Convert total return to annualized return.
     * Formula: [(1 + TWR)^(365/days)] - 1
     *
     * @param twr the total return
     * @param totalDays number of days in the period
     * @return annualized return
     */
    private BigDecimal annualizeTWR(BigDecimal twr, long totalDays) {
        double annualizationFactor = (double) DAYS_PER_YEAR / totalDays;
        double annualizedReturn = Math.pow(1 + twr.doubleValue(), annualizationFactor) - 1;
        
        return BigDecimal.valueOf(annualizedReturn);
    }
    
    /**
     * Validate method inputs for correctness.
     *
     * @throws IllegalArgumentException if any validation fails
     */
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
