package com.example.retailstorehome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class OrderHistoryActivity extends AppCompatActivity {
    private boolean seller;
    @Override public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_order_history);seller=getSharedPreferences("store_data",MODE_PRIVATE).getBoolean("seller_logged_in",false);((TextView)findViewById(R.id.tvOrderTitle)).setText(seller?"QUẢN LÝ ĐƠN HÀNG":"Đơn hàng của tôi");render();}
    private void render(){LinearLayout list=findViewById(R.id.orderList);list.removeAllViews();String raw=prefs().getString("orders","");if(raw.isEmpty()){((TextView)findViewById(R.id.tvNoOrders)).setText(seller?"Chưa có đơn hàng mới.":"Bạn chưa có đơn hàng nào.");return;}((TextView)findViewById(R.id.tvNoOrders)).setText("");String[] orders=raw.split("\\|\\|");for(int i=0;i<orders.length;i++)if(!orders[i].isEmpty())addOrder(list,orders[i],i);}
    private void addOrder(LinearLayout list,String order,int index){LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(20,18,20,18);row.setBackgroundResource(R.drawable.bg_product_card);TextView t=new TextView(this);t.setText(order);t.setTextColor(getColor(R.color.text_primary));t.setTextSize(17);row.addView(t);if(seller&&order.contains("Chờ xác nhận")){Button send=new Button(this);send.setText("Xác nhận gửi hàng");row.addView(send);send.setOnClickListener(v->confirm(index));}LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=12;list.addView(row,p);}
    private void confirm(int index){String[] orders=prefs().getString("orders","").split("\\|\\|");if(index<orders.length)orders[index]=orders[index].replace("Chờ xác nhận","Đã xác nhận gửi hàng");StringBuilder out=new StringBuilder();for(String x:orders)if(!x.isEmpty())out.append(x).append("||");prefs().edit().putString("orders",out.toString()).apply();Toast.makeText(this,"Đã xác nhận gửi hàng cho khách",Toast.LENGTH_SHORT).show();render();}
    private SharedPreferences prefs(){return getSharedPreferences("store_data",MODE_PRIVATE);}
}
