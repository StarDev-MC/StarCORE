package net.starcore.starecon.util;

/**
 * Utility for formatting currency values.
 * Supports: $1,250, $15.3K, $2.4M, $1.2B formats
 */
public class CurrencyFormatter {
    private static final String CURRENCY_SYMBOL = "$";
    private static final long BILLION = 1_000_000_000L;
    private static final long MILLION = 1_000_000L;
    private static final long THOUSAND = 1_000L;

    /**
     * Format a double balance to human-readable currency string.
     */
    public static String format(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            return CURRENCY_SYMBOL + "0";
        }
        
        long absAmount = Math.abs((long) amount);
        double decimalAmount = amount - (long) amount;
        
        if (absAmount >= BILLION) {
            double billions = amount / BILLION;
            return CURRENCY_SYMBOL + String.format("%.1f", billions).replaceAll("\\.0$", "") + "B";
        }
        if (absAmount >= MILLION) {
            double millions = amount / MILLION;
            return CURRENCY_SYMBOL + String.format("%.1f", millions).replaceAll("\\.0$", "") + "M";
        }
        if (absAmount >= THOUSAND) {
            double thousands = amount / THOUSAND;
            return CURRENCY_SYMBOL + String.format("%.1f", thousands).replaceAll("\\.0$", "") + "K";
        }
        
        if (decimalAmount > 0) {
            return CURRENCY_SYMBOL + String.format("%.2f", amount).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return CURRENCY_SYMBOL + String.format("%,d", (long) amount);
    }
}
