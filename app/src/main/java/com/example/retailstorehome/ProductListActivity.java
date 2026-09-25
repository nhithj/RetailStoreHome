package com.example.retailstorehome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProductListActivity extends AppCompatActivity {
    private boolean sellerView;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_product_list);
        sellerView = getIntent().getBooleanExtra("seller_view", false);
        String keyword = value("keyword"), category = value("category");
        if (sellerView) {
            ((TextView)findViewById(R.id.tvProductListTitle)).setText("Tồn kho sản phẩm");
            ((TextView)findViewById(R.id.tvProductListSubtitle)).setText("Danh sách hàng hóa đang kinh doanh");
            findViewById(R.id.btnCart).setVisibility(View.GONE);
        } else if (!keyword.isEmpty()) {
            ((TextView)findViewById(R.id.tvProductListTitle)).setText("Kết quả tìm kiếm");
            ((TextView)findViewById(R.id.tvProductListSubtitle)).setText("Từ khóa: " + keyword);
        } else if (!category.isEmpty()) ((TextView)findViewById(R.id.tvProductListTitle)).setText(category);
        LinearLayout list = findViewById(R.id.productList); int count = 0;
        for (int id : ProductRepository.getAllIds(this)) {
            if (!sellerView && !keyword.isEmpty() && !ProductRepository.getName(this,id).toLowerCase().contains(keyword.toLowerCase())) continue;
            if (!sellerView && !category.isEmpty() && !ProductRepository.getCategory(this,id).equals(category)) continue;
            addCard(list, id); count++;
        }
        findViewById(R.id.tvNoResult).setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnCart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }
    private String value(String key) { String v = getIntent().getStringExtra(key); return v == null ? "" : v; }
    private void addCard(LinearLayout list, int id) {
        LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(20,18,20,18); row.setMinimumHeight(132); row.setBackgroundResource(R.drawable.bg_product_card);
        ImageView image = new ImageView(this); image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ProductRepository.showImage(image, this, id);
        row.addView(image, new LinearLayout.LayoutParams(96,96));
        TextView text = new TextView(this); String info = ProductRepository.getName(this,id) + "\n" + format(ProductRepository.getPrice(this,id));
        if (sellerView) info += "\nCòn: " + ProductRepository.getQuantity(this,id) + " • " + ProductRepository.getStatus(this,id);
        text.setText(info); text.setTextSize(18); text.setPadding(20,0,0,0); text.setTextColor(getColor(R.color.text_primary)); row.addView(text, new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1,-2); params.topMargin = 12; list.addView(row,params);
        row.setOnClickListener(v -> {
            if (!ProductRepository.exists(this, id)) {
                Toast.makeText(this, "Sản phẩm này không còn tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ProductDetailActivity.class);
            i.putExtra("product",id); i.putExtra("seller_view",sellerView); startActivity(i);
        });
    }
    private String format(long n) { return String.format("%,d VNĐ",n).replace(',', '.'); }
}
