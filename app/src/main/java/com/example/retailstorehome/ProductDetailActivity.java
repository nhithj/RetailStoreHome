package com.example.retailstorehome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {
    private int product;
    private boolean sellerView;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_product_detail);
        product = getIntent().getIntExtra("product", 0);
        if (!ProductRepository.exists(this, product)) {
            Toast.makeText(this, "Sản phẩm không còn tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        sellerView = getIntent().getBooleanExtra("seller_view", false);
        ImageView image = findViewById(R.id.imgDetail);
        ProductRepository.showImage(image, this, product);
        ((TextView)findViewById(R.id.tvDetailName)).setText(ProductRepository.getName(this,product));
        ((TextView)findViewById(R.id.tvDetailPrice)).setText(format(ProductRepository.getPrice(this,product)));
        String detail = ProductRepository.getDetail(this,product) + "\nLoại: " + ProductRepository.getCategory(this,product) + "\nTình trạng: " + ProductRepository.getAvailabilityText(this,product);
        if (sellerView) detail += "\nMã sản phẩm: " + ProductRepository.getCode(this,product) + "\nThời gian thêm: " + ProductRepository.getDate(this,product) + "\nSố lượng tồn: " + ProductRepository.getQuantity(this,product);
        ((TextView)findViewById(R.id.tvDetailText)).setText(detail);
        findViewById(R.id.btnAddCart).setVisibility(sellerView ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnBuyNow).setVisibility(sellerView ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnAddCart).setOnClickListener(v -> addCart(false));
        findViewById(R.id.btnBuyNow).setOnClickListener(v -> addCart(true));
        renderRelatedProducts();
    }
    private void addCart(boolean openCart) {
        if (!ProductRepository.isAvailable(this,product)) { Toast.makeText(this,"Sản phẩm hiện không thể đặt mua",Toast.LENGTH_SHORT).show(); return; }
        SharedPreferences p = getSharedPreferences("store_data", MODE_PRIVATE);
        p.edit().putString("cart_items", p.getString("cart_items", "") + product + ",").apply();
        Toast.makeText(this,"Đã thêm vào giỏ hàng",Toast.LENGTH_SHORT).show();
        if (openCart) startActivity(new Intent(this, CartActivity.class));
    }
    private String format(long n) { return String.format("%,d VNĐ",n).replace(',', '.'); }

    private void renderRelatedProducts() {
        java.util.List<Integer> ordered = new java.util.ArrayList<>();
        String currentCategory = ProductRepository.getCategory(this, product);
        for (int id : ProductRepository.getAllIds(this))
            if (id != product && ProductRepository.getCategory(this,id).equals(currentCategory)) ordered.add(id);
        for (int id : ProductRepository.getAllIds(this))
            if (id != product && !ordered.contains(id)) ordered.add(id);
        LinearLayout grid = findViewById(R.id.relatedProductGrid);
        int limit = Math.min(4, ordered.size());
        for (int position = 0; position < limit; position += 2) {
            LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(createRelatedCard(ordered.get(position)), new LinearLayout.LayoutParams(0, dp(205), 1));
            if (position + 1 < limit) {
                LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0, dp(205), 1); second.leftMargin = dp(8);
                row.addView(createRelatedCard(ordered.get(position + 1)), second);
            } else row.addView(new View(this), new LinearLayout.LayoutParams(0, dp(205), 1));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(205)); params.topMargin = dp(8); grid.addView(row, params);
        }
        findViewById(R.id.tvRelatedTitle).setVisibility(limit == 0 ? View.GONE : View.VISIBLE);
    }

    private LinearLayout createRelatedCard(int id) {
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(7),dp(7),dp(7),dp(7)); card.setBackgroundResource(R.drawable.bg_product_card);
        ImageView image = new ImageView(this); image.setScaleType(ImageView.ScaleType.FIT_CENTER); image.setBackgroundColor(getColor(R.color.white)); ProductRepository.showImage(image,this,id); card.addView(image,new LinearLayout.LayoutParams(-1,dp(125)));
        TextView info = new TextView(this); info.setText(ProductRepository.getName(this,id)+"\n"+format(ProductRepository.getPrice(this,id))); info.setTextSize(13); info.setTextColor(getColor(R.color.text_primary)); info.setPadding(0,dp(6),0,0); info.setMaxLines(3); card.addView(info,new LinearLayout.LayoutParams(-1,0,1));
        card.setOnClickListener(v->{Intent intent=new Intent(this,ProductDetailActivity.class);intent.putExtra("product",id);intent.putExtra("seller_view",sellerView);startActivity(intent);});
        return card;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
