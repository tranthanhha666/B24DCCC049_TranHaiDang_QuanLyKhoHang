package com.example.myapplication;

import com.example.myapplication.model.Product;
import com.example.myapplication.util.FormatUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductAndFormatTest {

    @Test
    public void productInventoryValueAndLowStockWorkCorrectly() {
        Product p1 = new Product(1, "Chuột không dây", 150000, 20, "Điện tử", true, "");
        assertEquals(3000000L, p1.getInventoryValue());
        assertFalse(p1.isLowStock());

        Product p2 = new Product(2, "Bàn phím cơ", 450000, 3, "Điện tử", true, "");
        assertEquals(1350000L, p2.getInventoryValue());
        assertTrue(p2.isLowStock());

        Product pBoundary = new Product(3, "Test", 10000, 5, "Khác", true, "");
        assertFalse(pBoundary.isLowStock());

        Product pZero = new Product(4, "Test Zero", 10000, 0, "Khác", false, "");
        assertTrue(pZero.isLowStock());
        assertEquals(0L, pZero.getInventoryValue());
    }

    @Test
    public void currencyFormattingUsesDotThousandSeparators() {
        assertEquals("0 đ", FormatUtils.formatCurrency(0));
        assertEquals("25.000 đ", FormatUtils.formatCurrency(25000));
        assertEquals("150.000 đ", FormatUtils.formatCurrency(150000));
        assertEquals("5.350.000 đ", FormatUtils.formatCurrency(5350000));
    }
}
