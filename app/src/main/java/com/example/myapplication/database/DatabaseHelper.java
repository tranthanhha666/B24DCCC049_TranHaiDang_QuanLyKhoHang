package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Product;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "warehouse.db";
    public static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // Bảng products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PRODUCT_ID = "id";
    public static final String COL_PRODUCT_NAME = "name";
    public static final String COL_PRODUCT_PRICE = "price";
    public static final String COL_PRODUCT_QUANTITY = "quantity";
    public static final String COL_PRODUCT_CATEGORY = "category";
    public static final String COL_PRODUCT_ACTIVE = "active";
    public static final String COL_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL"
                + ")";
        db.execSQL(createUsersTable);

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRODUCT_NAME + " TEXT NOT NULL, "
                + COL_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + COL_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + COL_PRODUCT_CATEGORY + " TEXT NOT NULL, "
                + COL_PRODUCT_ACTIVE + " INTEGER NOT NULL, "
                + COL_PRODUCT_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(createProductsTable);

        // Chèn tài khoản mẫu mặc định
        insertUserInternal(db, "admin", "123456");

        // Chèn 3 sản phẩm mẫu ban đầu (active = 1)
        insertProductInternal(db, new Product(
                "Chuột không dây",
                150000,
                20,
                "Điện tử",
                true,
                "Chuột không dây kết nối 2.4GHz, độ nhạy cao, tiết kiệm pin."
        ));
        insertProductInternal(db, new Product(
                "Bàn phím cơ",
                450000,
                3,
                "Điện tử",
                true,
                "Bàn phím cơ thiết kế nhỏ gọn, gõ êm và có đèn nền LED."
        ));
        insertProductInternal(db, new Product(
                "Sổ tay A5",
                25000,
                40,
                "Văn phòng phẩm",
                true,
                "Sổ tay bìa cứng kích thước A5, giấy dày chống thấm mực."
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    private long insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    private long insertProductInternal(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, p.getName());
        values.put(COL_PRODUCT_PRICE, p.getPrice());
        values.put(COL_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COL_PRODUCT_CATEGORY, p.getCategory());
        values.put(COL_PRODUCT_ACTIVE, p.isActive() ? 1 : 0);
        values.put(COL_PRODUCT_DESCRIPTION, p.getDescription() != null ? p.getDescription() : "");
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertUserInternal(db, username, password);
    }

    public boolean checkLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS
                    + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
            cursor = db.rawQuery(query, new String[]{username, password});
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertProductInternal(db, p);
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_PRODUCT_ID + " DESC";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    productList.add(cursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToProduct(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public int updateQuantity(int id, int quantity) {
        if (quantity < 0) {
            return 0;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_QUANTITY, quantity);
        return db.update(
                TABLE_PRODUCTS,
                values,
                COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                TABLE_PRODUCTS,
                COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT COALESCE(SUM(" + COL_PRODUCT_PRICE + " * " + COL_PRODUCT_QUANTITY + "), 0) FROM " + TABLE_PRODUCTS;
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0L;
    }

    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_QUANTITY + " < ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(Product.LOW_STOCK_THRESHOLD)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public int getTotalProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS;
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    private Product cursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
        long price = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_QUANTITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY));
        boolean active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ACTIVE)) == 1;
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESCRIPTION));
        return new Product(id, name, price, quantity, category, active, description);
    }
}
