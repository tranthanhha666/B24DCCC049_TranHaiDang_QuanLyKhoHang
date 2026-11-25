package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.FormatUtils;
import com.google.android.material.appbar.MaterialToolbar;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";

    private int productId = -1;
    private Product currentProduct;
    private DatabaseHelper databaseHelper;

    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private TextView tvDetailStatus;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailInventoryValue;
    private TextView tvDetailWarning;
    private TextView tvDetailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailInventoryValue = findViewById(R.id.tvDetailInventoryValue);
        tvDetailWarning = findViewById(R.id.tvDetailWarning);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        Button btnIncrease = findViewById(R.id.btnIncrease);
        Button btnDecrease = findViewById(R.id.btnDecrease);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy mã sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductDetail();

        btnIncrease.setOnClickListener(v -> {
            if (currentProduct == null) {
                return;
            }
            int newQuantity = currentProduct.getQuantity() + 1;
            databaseHelper.updateQuantity(productId, newQuantity);
            loadProductDetail();
        });

        btnDecrease.setOnClickListener(v -> {
            if (currentProduct == null) {
                return;
            }
            if (currentProduct.getQuantity() <= 0) {
                Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQuantity = currentProduct.getQuantity() - 1;
            databaseHelper.updateQuantity(productId, newQuantity);
            loadProductDetail();
        });

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> confirmDeleteProduct());
    }

    private void loadProductDetail() {
        currentProduct = databaseHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không còn tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText("Loại: " + currentProduct.getCategory());

        if (currentProduct.isActive()) {
            tvDetailStatus.setText("Đang kinh doanh");
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_category);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else {
            tvDetailStatus.setText("Ngừng kinh doanh");
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_inactive);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }

        tvDetailPrice.setText(FormatUtils.formatCurrency(currentProduct.getPrice()));
        tvDetailQuantity.setText("Số lượng tồn: " + currentProduct.getQuantity());
        tvDetailInventoryValue.setText("Giá trị tồn kho: "
                + FormatUtils.formatCurrency(currentProduct.getInventoryValue()));

        if (currentProduct.isLowStock()) {
            tvDetailWarning.setVisibility(View.VISIBLE);
            tvDetailWarning.setText(currentProduct.getQuantity() == 0
                    ? "Cảnh báo: Hết hàng (số lượng = 0)"
                    : "Cảnh báo: Sản phẩm sắp hết hàng (dưới 5)");
            tvDetailQuantity.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        } else {
            tvDetailWarning.setVisibility(View.GONE);
            tvDetailQuantity.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }

        String desc = currentProduct.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            tvDetailDescription.setText("Không có mô tả");
        } else {
            tvDetailDescription.setText(desc);
        }
    }

    private void confirmDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + currentProduct.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int rows = databaseHelper.deleteProduct(productId);
                    if (rows > 0) {
                        Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
