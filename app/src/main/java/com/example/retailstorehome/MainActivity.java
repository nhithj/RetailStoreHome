package com.example.retailstorehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private long lastBackPressed = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnProductList).setOnClickListener(v -> openProducts(null, null));
        findViewById(R.id.btnCartHome).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.btnOrderHistory).setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
        findViewById(R.id.btnCustomerAddress).setOnClickListener(v ->
                startActivity(new Intent(this, CustomerInfoActivity.class)));
        findViewById(R.id.btnSupport).setOnClickListener(v ->
                Toast.makeText(this, "Hỗ trợ: 0985 9999 29", Toast.LENGTH_LONG).show());
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
        setupBottomBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMode();
        renderHomeProducts();
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
        findViewById(R.id.bottomActionBar).setVisibility(seller ? View.GONE : View.VISIBLE);
        ((Button) findViewById(R.id.btnLogin)).setText(customer ? "Đăng xuất" : "Đăng nhập");
        findViewById(R.id.btnCustomerAddress).setVisibility(!seller && customer ? View.VISIBLE : View.GONE);
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

    private void setupBottomBar() {
        ScrollView scroll = findViewById(R.id.homeScroll);
        View bar = findViewById(R.id.bottomActionBar);
        scroll.setOnScrollChangeListener((v, x, y, oldX, oldY) -> {
            if (y > oldY + 8) {
                bar.animate().translationY(bar.getHeight() + 12).alpha(0f).setDuration(180).start();
            } else if (y < oldY - 4) {
                bar.animate().translationY(0).alpha(1f).setDuration(180).start();
            }
        });
    }

    /** Hiển thị sản phẩm theo lưới 2 cột, kèm ảnh; bấm vào từng ô để xem chi tiết. */
    private void renderHomeProducts() {
        LinearLayout grid = findViewById(R.id.homeProductGrid);
        grid.removeAllViews();
        java.util.List<Integer> ids = ProductRepository.getAllIds(this);
        for (int position = 0; position < ids.size(); position += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(createHomeProductCard(ids.get(position)), new LinearLayout.LayoutParams(0, -2, 1));
            if (position + 1 < ids.size()) {
                LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0, -2, 1);
                second.leftMargin = dp(8);
                row.addView(createHomeProductCard(ids.get(position + 1)), second);
            } else {
                row.addView(new View(this), new LinearLayout.LayoutParams(0, -2, 1));
            }
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
            p.topMargin = dp(8);
            grid.addView(row, p);
        }
    }

    private LinearLayout createHomeProductCard(int id) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(8), dp(8), dp(8), dp(8));
            card.setMinimumHeight(dp(246));
            card.setBackgroundResource(R.drawable.bg_product_card);
            ImageView image = new ImageView(this);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setAdjustViewBounds(false);
            image.setBackgroundColor(getColor(R.color.white));
            ProductRepository.showImage(image, this, id);
            card.addView(image, new LinearLayout.LayoutParams(-1, dp(150)));
            TextView info = new TextView(this);
            info.setText(ProductRepository.getName(this, id) + "\n" + String.format("%,d VNĐ", ProductRepository.getPrice(this, id)).replace(',', '.') + "\n" + ProductRepository.getAvailabilityText(this,id));
            info.setTextSize(14);
            info.setTextColor(getColor(R.color.text_primary));
            info.setMaxLines(4);
            info.setPadding(0, dp(8), 0, 0);
            card.addView(info, new LinearLayout.LayoutParams(-1, dp(80)));
            card.setOnClickListener(v -> { Intent i = new Intent(this, ProductDetailActivity.class); i.putExtra("product", id); startActivity(i); });
            return card;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private SharedPreferences prefs() {
        return getSharedPreferences("store_data", MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastBackPressed < 2000) {
            super.onBackPressed();
        } else {
            lastBackPressed = now;
            Toast.makeText(this, "Chạm lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show();
        }
    }
}
