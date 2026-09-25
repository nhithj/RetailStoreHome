package com.example.retailstorehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnProductList).setOnClickListener(v -> openProducts(null, null));
        findViewById(R.id.btnCartHome).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.btnOrderHistory).setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
        findViewById(R.id.btnExitCustomer).setOnClickListener(v -> finish());
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String keyword = ((EditText) findViewById(R.id.edtSearch)).getText().toString().trim();
            if (keyword.isEmpty()) {
                ((EditText) findViewById(R.id.edtSearch)).setError("Nhập tên sản phẩm cần tìm");
            } else {
                openProducts(keyword, null);
            }
        });
        findViewById(R.id.categoryFamily).setOnClickListener(v -> openProducts(null, "Y tế gia đình"));
        findViewById(R.id.categoryPro).setOnClickListener(v -> openProducts(null, "Y tế chuyên dụng"));
        findViewById(R.id.categoryCare).setOnClickListener(v -> openProducts(null, "Chăm sóc sức khỏe"));

        findViewById(R.id.btnLogin).setOnClickListener(v -> loginOrLogoutCustomer());
        findViewById(R.id.btnSellerAddProduct).setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));
        findViewById(R.id.btnSellerProducts).setOnClickListener(v -> startActivity(new Intent(this, ProductManagerActivity.class)));
        findViewById(R.id.btnSellerDashboard).setOnClickListener(v -> startActivity(new Intent(this, SellerDashboardActivity.class)));
        findViewById(R.id.btnSellerOrders).setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
        findViewById(R.id.btnSellerLogout).setOnClickListener(v -> {
            prefs().edit().putBoolean("seller_logged_in", false).apply();
            Toast.makeText(this, "Đã đăng xuất người bán", Toast.LENGTH_SHORT).show();
            updateMode();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMode();
    }

    private void loginOrLogoutCustomer() {
        if (prefs().getBoolean("customer_logged_in", false)) {
            prefs().edit().putBoolean("customer_logged_in", false).apply();
            Toast.makeText(this, "Đã đăng xuất khách hàng", Toast.LENGTH_SHORT).show();
            updateMode();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void updateMode() {
        SharedPreferences data = prefs();
        boolean seller = data.getBoolean("seller_logged_in", false);
        boolean customer = data.getBoolean("customer_logged_in", false);
        findViewById(R.id.customerArea).setVisibility(seller ? View.GONE : View.VISIBLE);
        findViewById(R.id.sellerArea).setVisibility(seller ? View.VISIBLE : View.GONE);
        ((Button) findViewById(R.id.btnLogin)).setText(customer ? "Đăng xuất" : "Đăng nhập");
        int count = 0;
        long value = 0;
        for (int id : ProductRepository.getAllIds(this)) {
            count += ProductRepository.getQuantity(this, id);
            value += ProductRepository.getPrice(this, id) * ProductRepository.getQuantity(this, id);
        }
        ((TextView) findViewById(R.id.tvProductCount)).setText(count + " sản phẩm trong kho");
        ((TextView) findViewById(R.id.tvTotalValue)).setText(String.format("%,d VNĐ", value).replace(',', '.'));
    }

    private void openProducts(String keyword, String category) {
        Intent intent = new Intent(this, ProductListActivity.class);
        if (keyword != null) intent.putExtra("keyword", keyword);
        if (category != null) intent.putExtra("category", category);
        startActivity(intent);
    }

    private SharedPreferences prefs() {
        return getSharedPreferences("store_data", MODE_PRIVATE);
    }
}
