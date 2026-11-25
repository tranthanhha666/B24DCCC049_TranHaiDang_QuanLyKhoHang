package com.example.myapplication;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Product;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private Context context;
    private DatabaseHelper dbHelper;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        dbHelper = new DatabaseHelper(context);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void seedDataAndLoginWorkCorrectly() {
        assertTrue(dbHelper.checkLogin("admin", "123456"));
        assertFalse(dbHelper.checkLogin("admin", "wrong"));
        assertFalse(dbHelper.checkLogin("unknown", "123456"));

        ArrayList<Product> products = dbHelper.getAllProducts();
        assertEquals(3, products.size());
        assertEquals(5350000L, dbHelper.getTotalValue());
        assertEquals(1, dbHelper.countLowStock());
    }

    @Test
    public void insertUpdateDeleteProductWorkCorrectly() {
        long id = dbHelper.insertProduct(
                new Product("Tai nghe", 200000, 2, "Điện tử", true, "Mô tả test")
        );
        assertTrue(id > 0);

        Product loaded = dbHelper.getProductById((int) id);
        assertNotNull(loaded);
        assertEquals("Tai nghe", loaded.getName());
        assertEquals(2, loaded.getQuantity());
        assertEquals(2, dbHelper.countLowStock());

        // Cập nhật tăng số lượng
        int updated = dbHelper.updateQuantity((int) id, 6);
        assertEquals(1, updated);
        assertEquals(6, dbHelper.getProductById((int) id).getQuantity());
        assertEquals(1, dbHelper.countLowStock());

        // Không cho cập nhật số lượng âm
        assertEquals(0, dbHelper.updateQuantity((int) id, -1));
        assertEquals(6, dbHelper.getProductById((int) id).getQuantity());

        // Xóa sản phẩm
        int deleted = dbHelper.deleteProduct((int) id);
        assertEquals(1, deleted);
        assertNull(dbHelper.getProductById((int) id));
    }
}
