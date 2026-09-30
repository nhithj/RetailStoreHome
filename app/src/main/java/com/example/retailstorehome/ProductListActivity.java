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
import java.util.ArrayList;
import java.util.List;

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
        LinearLayout list = findViewById(R.id.productList);
        List<Integer> shown = new ArrayList<>();
        for (int id : ProductRepository.getAllIds(this)) {
            if (!sellerView && !keyword.isEmpty() && !ProductRepository.getName(this,id).toLowerCase().contains(keyword.toLowerCase())) continue;
            if (!sellerView && !category.isEmpty() && !ProductRepository.getCategory(this,id).equals(category)) continue;
            shown.add(id);
        }
        renderGrid(list, shown);
        findViewById(R.id.tvNoResult).setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnCart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }
    private String value(String key) { String v = getIntent().getStringExtra(key); return v == null ? "" : v; }
    private void renderGrid(LinearLayout list, List<Integer> ids) {
        for (int position = 0; position < ids.size(); position += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.addView(createCard(ids.get(position)), new LinearLayout.LayoutParams(0, dp(250), 1));
            if (position + 1 < ids.size()) {
                LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0, dp(250), 1);
                second.leftMargin = dp(8);
                row.addView(createCard(ids.get(position + 1)), second);
            } else row.addView(new View(this), new LinearLayout.LayoutParams(0, dp(250), 1));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(250));
            params.topMargin = dp(10);
            list.addView(row, params);
        }
    }
    private LinearLayout createCard(int id) {
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.VERTICAL); row.setPadding(dp(8),dp(8),dp(8),dp(8)); row.setBackgroundResource(R.drawable.bg_product_card);
        ImageView image = new ImageView(this); image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setBackgroundColor(getColor(R.color.white));
        ProductRepository.showImage(image, this, id);
        row.addView(image, new LinearLayout.LayoutParams(-1,dp(145)));
        TextView text = new TextView(this); String info = ProductRepository.getName(this,id) + "\n" + format(ProductRepository.getPrice(this,id)) + "\n" + ProductRepository.getAvailabilityText(this,id);
        if (sellerView) info += "\nMã: " + ProductRepository.getCode(this,id) + " • Còn: " + ProductRepository.getQuantity(this,id) + "\nThêm: " + ProductRepository.getDate(this,id);
        text.setText(info); text.setTextSize(sellerView ? 12 : 14); text.setPadding(0,dp(8),0,0); text.setTextColor(ProductRepository.isAvailable(this,id) ? getColor(R.color.text_primary) : 0xFFC62828); text.setMaxLines(sellerView ? 6 : 4); row.addView(text, new LinearLayout.LayoutParams(-1,0,1));
        row.setOnClickListener(v -> {
            if (!ProductRepository.exists(this, id)) {
                Toast.makeText(this, "Sản phẩm này không còn tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ProductDetailActivity.class);
            i.putExtra("product",id); i.putExtra("seller_view",sellerView); startActivity(i);
        });
        return row;
    }
    private String format(long n) { return String.format("%,d VNĐ",n).replace(',', '.'); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
