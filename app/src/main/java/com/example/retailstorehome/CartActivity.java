package com.example.retailstorehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private long total = 0;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cart);
        renderCart();
        findViewById(R.id.btnClearCart).setOnClickListener(v -> {
            getSharedPreferences("store_data", MODE_PRIVATE).edit().remove("cart_items").apply();
            recreate();
        });
        findViewById(R.id.btnCheckout).setOnClickListener(v -> checkout());
    }

    private void renderCart() {
        LinearLayout list = findViewById(R.id.cartList);
        String items = getSharedPreferences("store_data", MODE_PRIVATE).getString("cart_items", "");
        if (items.isEmpty()) {
            ((TextView) findViewById(R.id.tvCartEmpty)).setText("Giỏ hàng chưa có sản phẩm");
            ((TextView) findViewById(R.id.tvCartTotal)).setText("Tổng: 0 VNĐ");
            return;
        }
        for (String s : items.split(",")) if (!s.isEmpty()) {
            int i;
            try { i = Integer.parseInt(s); } catch (NumberFormatException e) { continue; }
            if (!ProductRepository.exists(this, i)) continue;
            total += ProductRepository.getPrice(this, i);
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(20, 20, 20, 20);
            row.setBackgroundResource(R.drawable.bg_product_card);
            ImageView image = new ImageView(this);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ProductRepository.showImage(image, this, i);
            row.addView(image, new LinearLayout.LayoutParams(86, 86));
            TextView v = new TextView(this);
            v.setText(ProductRepository.getName(this, i) + "\n" + format(ProductRepository.getPrice(this, i)));
            v.setTextSize(18);
            v.setTextColor(getColor(R.color.text_primary));
            v.setPadding(18, 0, 8, 0);
            row.addView(v, new LinearLayout.LayoutParams(0, -2, 1));
            Button remove = new Button(this);
            remove.setText("Xóa");
            row.addView(remove);
            remove.setOnClickListener(x -> removeOne(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.topMargin = 10;
            list.addView(row, params);
        }
        ((TextView) findViewById(R.id.tvCartTotal)).setText("Tổng thanh toán: " + format(total));
    }

    private void removeOne(int product) {
        SharedPreferences p = getSharedPreferences("store_data", MODE_PRIVATE);
        String now = p.getString("cart_items", "");
        p.edit().putString("cart_items", now.replaceFirst(product + ",", "")).apply();
        recreate();
    }

    private void checkout() {
        SharedPreferences p = getSharedPreferences("store_data", MODE_PRIVATE);
        String currentItems = p.getString("cart_items", "");
        if (currentItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống, hãy chọn sản phẩm trước", Toast.LENGTH_LONG).show();
            return;
        }
        if (!p.getBoolean("customer_logged_in", false)) {
            Toast.makeText(this, "Hãy đăng nhập tài khoản khách hàng để đặt hàng", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        String phone = p.getString("customer_phone", "");
        String address = p.getString("customer_address", "");
        if (phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Hãy đăng ký số điện thoại và địa chỉ nhận hàng", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CustomerInfoActivity.class));
            return;
        }
        String time = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String order = "Đơn #" + (p.getInt("order_count", 0) + 1) + " – " + format(total)
                + "\n" + time + " • Chờ xác nhận";
        String history = p.getString("orders", "");
        for (String item : currentItems.split(",")) if (!item.isEmpty()) ProductRepository.decreaseStock(this, Integer.parseInt(item));
        p.edit().putString("orders", history + order + "||")
                .putInt("order_count", p.getInt("order_count", 0) + 1)
                .putLong("order_revenue", p.getLong("order_revenue", 0) + total)
                .remove("cart_items").apply();
        Toast.makeText(this, "Đặt hàng thành công. Cửa hàng sẽ xác nhận đơn sớm.", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, OrderHistoryActivity.class));
        finish();
    }

    private String format(long value) {
        return String.format("%,d VNĐ", value).replace(',', '.');
    }
}
