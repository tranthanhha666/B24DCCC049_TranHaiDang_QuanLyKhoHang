package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Product;
import com.google.android.material.appbar.MaterialToolbar;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private Spinner spnCategory;
    private SwitchCompat switchActive;
    private EditText edtDescription;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAdd);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        spnCategory = findViewById(R.id.spnCategory);
        switchActive = findViewById(R.id.switchActive);
        edtDescription = findViewById(R.id.edtDescription);
        Button btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.product_categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên sản phẩm");
            edtName.requestFocus();
            Toast.makeText(this, "Tên sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceStr.isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá bán");
            edtPrice.requestFocus();
            Toast.makeText(this, "Giá bán không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceStr);
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            edtPrice.requestFocus();
            Toast.makeText(this, "Giá bán không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price <= 0) {
            edtPrice.setError("Giá bán phải lớn hơn 0");
            edtPrice.requestFocus();
            Toast.makeText(this, "Giá bán phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantityStr.isEmpty()) {
            edtQuantity.setError("Vui lòng nhập số lượng");
            edtQuantity.requestFocus();
            Toast.makeText(this, "Số lượng không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            edtQuantity.requestFocus();
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity < 0) {
            edtQuantity.setError("Số lượng không được âm");
            edtQuantity.requestFocus();
            Toast.makeText(this, "Số lượng không được âm", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spnCategory.getSelectedItem().toString();
        boolean active = switchActive.isChecked();

        Product newProduct = new Product(name, price, quantity, category, active, description);
        long insertedId = databaseHelper.insertProduct(newProduct);

        if (insertedId > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}
