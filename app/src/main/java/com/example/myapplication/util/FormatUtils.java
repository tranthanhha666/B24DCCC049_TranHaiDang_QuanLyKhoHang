package com.example.myapplication.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class FormatUtils {

    private FormatUtils() {
    }

    public static String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount) + " đ";
    }
}
