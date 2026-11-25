package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.FormatUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ProductAdapter productAdapter;
    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private TextView tvEmptyList;
    private RecyclerView rvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        rvProducts = findViewById(R.id.rvProducts);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        productAdapter = new ProductAdapter(new ArrayList<>(), product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsAndStatistics();
    }

    private void loadProductsAndStatistics() {
        ArrayList<Product> products = databaseHelper.getAllProducts();
        productAdapter.updateData(products);

        if (products.isEmpty()) {
            rvProducts.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            rvProducts.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(View.GONE);
        }

        int totalProducts = products.size();
        long totalValue = databaseHelper.getTotalValue();
        int lowStockCount = databaseHelper.countLowStock();

        tvTotalProducts.setText(String.valueOf(totalProducts));
        tvTotalValue.setText(FormatUtils.formatCurrency(totalValue));
        tvLowStockCount.setText(String.valueOf(lowStockCount));
    }
}
