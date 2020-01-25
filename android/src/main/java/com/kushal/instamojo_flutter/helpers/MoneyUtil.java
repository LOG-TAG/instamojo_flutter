package com.kushal.instamojo_flutter.helpers;

import java.math.BigDecimal;

public class MoneyUtil {

    /**
     * Converts a number to a expected precision by rounding the number.
     * @param value Amount
     * @param precision Number of precision digits post decimal point.
     * @return Rounded value of amount.
     */
    public static double getRoundedValue(double value, int precision) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(precision, BigDecimal.ROUND_HALF_DOWN);
        return bigDecimal.doubleValue();
    }

    /**
     * Calculates the monthly EMI installment of an EMI Plan.
     * @param amount Principal Amount.
     * @param rate Rate of interest.
     * @param tenure Number of months.
     * @return Monthly installment amount.
     */
    public static double getMonthlyEMI(double amount, BigDecimal rate, int tenure) {
        double perRate = rate.doubleValue() / 1200;
        double emiAmount = amount * perRate / (1 - Math.pow((1 / (1 + perRate)), tenure));
        return getRoundedValue(emiAmount, 2);
    }
}
