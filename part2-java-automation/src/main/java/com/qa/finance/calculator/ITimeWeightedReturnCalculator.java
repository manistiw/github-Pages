package com.qa.finance.calculator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.SortedMap;

/**
 * Interface for Time-Weighted Return (TWR) calculation
 * 
 * TWR measures the compound rate of growth in a portfolio, eliminating the distorting effects 
 * of cash flows (contributions and withdrawals). It's particularly useful for evaluating 
 * investment manager performance.
 */
public interface ITimeWeightedReturnCalculator {
    
    /**
     * Calculate Time-Weighted Return for a given portfolio
     * 
     * @param externalFlowTimeSeries Sorted map of external cash flows (DateTime -> Amount)
     *                                Positive values = contributions (inflows)
     *                                Negative values = withdrawals (outflows)
     * @param navTimeSeries Sorted map of Net Asset Values (DateTime -> NAV)
     *                      NAV represents the total portfolio value at each date
     * @param evaluationStart Start date for the TWR calculation period
     * @param evaluationEnd End date for the TWR calculation period
     * @param annualizeReturn If true, return annualized TWR; if false, return total TWR
     * @return TWR as decimal (e.g., 0.15 = 15% return)
     */
    BigDecimal calculateTimeWeightedReturn(
            SortedMap<LocalDateTime, BigDecimal> externalFlowTimeSeries,
            SortedMap<LocalDateTime, BigDecimal> navTimeSeries,
            LocalDateTime evaluationStart,
            LocalDateTime evaluationEnd,
            boolean annualizeReturn
    );
}
