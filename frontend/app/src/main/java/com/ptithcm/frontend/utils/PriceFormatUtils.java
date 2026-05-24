package com.ptithcm.frontend.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class PriceFormatUtils {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0");

    private PriceFormatUtils() {
    }

    public static String formatCurrency(BigDecimal value) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        return FORMAT.format(safeValue) + " VND";
    }
}