package com.example.retailstorehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {
    private int product;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_product_detail);
        product = getIntent().getIntExtra("product", 0);
        if (!ProductRepository.exists(this, product)) {
            Toast.makeText(this, "Sản phẩm không còn tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        boolean sellerView = getIntent().getBooleanExtra("seller_view", false);
        ImageView image = findViewById(R.id.imgDetail);
        ProductRepository.showImage(image, this, product);
        ((TextView)findViewById(R.id.tvDetailName)).setText(ProductRepository.getName(this,product));
        ((TextView)findViewById(R.id.tvDetailPrice)).setText(format(ProductRepository.getPrice(this,product)));
        ((TextView)findViewById(R.id.tvDetailText)).setText(ProductRepository.getDetail(this,product) + "\nLoại: " + ProductRepository.getCategory(this,product) + "\nTrạng thái: " + ProductRepository.getStatus(this,product));
        findViewById(R.id.btnAddCart).setVisibility(sellerView ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnBuyNow).setVisibility(sellerView ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnAddCart).setOnClickListener(v -> addCart(false));
        findViewById(R.id.btnBuyNow).setOnClickListener(v -> addCart(true));
    }
    private void addCart(boolean openCart) {
        if (ProductRepository.getQuantity(this,product) <= 0) { Toast.makeText(this,"Sản phẩm đã hết hàng",Toast.LENGTH_SHORT).show(); return; }
        SharedPreferences p = getSharedPreferences("store_data", MODE_PRIVATE);
        p.edit().putString("cart_items", p.getString("cart_items", "") + product + ",").apply();
        Toast.makeText(this,"Đã thêm vào giỏ hàng",Toast.LENGTH_SHORT).show();
        if (openCart) startActivity(new Intent(this, CartActivity.class));
    }
    private String format(long n) { return String.format("%,d VNĐ",n).replace(',', '.'); }
}
