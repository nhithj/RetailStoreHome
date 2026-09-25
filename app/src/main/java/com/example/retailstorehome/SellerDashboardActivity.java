package com.example.retailstorehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SellerDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);
        SharedPreferences data = getSharedPreferences("store_data", MODE_PRIVATE);
        int products = 0;
        for (int id : ProductRepository.getAllIds(this)) products += ProductRepository.getQuantity(this, id);
        int orderCount = data.getInt("order_count", 0);
        long revenue = data.getLong("order_revenue", 0);
        ((TextView) findViewById(R.id.tvDashboardProducts)).setText(products + " sản phẩm đang quản lý");
        ((TextView) findViewById(R.id.tvDashboardOrders)).setText(orderCount + " đơn hàng đã tạo");
        ((TextView) findViewById(R.id.tvDashboardRevenue)).setText(String.format("%,d VNĐ", revenue).replace(',', '.'));
        findViewById(R.id.btnDashboardAdd).setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));
        findViewById(R.id.btnDashboardOrders).setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
        findViewById(R.id.btnDashboardInventory).setOnClickListener(v -> {
            startActivity(new Intent(this, ProductManagerActivity.class));
        });
    }
}
