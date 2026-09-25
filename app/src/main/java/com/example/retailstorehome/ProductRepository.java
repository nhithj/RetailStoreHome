package com.example.retailstorehome;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final int BASE = 6;
    private static final String[] DEFAULT_NAMES = {"Máy đo huyết áp", "Xe lăn y tế", "Nhiệt kế điện tử", "Máy xông khí dung", "Máy đo đường huyết", "Khẩu trang y tế hộp 50 cái"};
    private static final long[] DEFAULT_PRICES = {450000, 1850000, 180000, 720000, 650000, 65000};
    private static final String[] DEFAULT_DETAILS = {"Gói máy đo huyết áp điện tử", "Gói xe lăn hỗ trợ di chuyển", "Gói nhiệt kế điện tử", "Máy xông hỗ trợ chăm sóc đường hô hấp", "Thiết bị theo dõi đường huyết", "Khẩu trang y tế hộp 50 cái"};
    private static final String[] DEFAULT_CATEGORIES = {"Y tế gia đình", "Y tế chuyên dụng", "Y tế gia đình", "Chăm sóc sức khỏe", "Y tế gia đình", "Chăm sóc sức khỏe"};
    private static final int[] IMAGES = {R.drawable.product_blood_pressure, R.drawable.product_wheelchair, R.drawable.product_thermometer, R.drawable.product_nebulizer_v2, R.drawable.product_glucose_meter_v2, R.drawable.product_masks_v2};

    private static SharedPreferences p(Context context) {
        return context.getSharedPreferences("store_data", Context.MODE_PRIVATE);
    }
    private static String key(int id, String field) { return "product_" + id + "_" + field; }
    public static boolean exists(Context context, int id) {
        return id >= 0 && id < BASE + p(context).getInt("custom_product_count", 0)
                && !p(context).getBoolean(key(id, "deleted"), false);
    }
    public static List<Integer> getAllIds(Context context) {
        List<Integer> ids = new ArrayList<>();
        int count = BASE + p(context).getInt("custom_product_count", 0);
        for (int i = 0; i < count; i++) if (exists(context, i)) ids.add(i);
        return ids;
    }
    public static String getName(Context c, int id) {
        String fallback = id < BASE ? DEFAULT_NAMES[id] : "Sản phẩm mới";
        return p(c).getString(key(id, "name"), fallback);
    }
    public static String getCode(Context c, int id) { return p(c).getString(key(id, "code"), "YT" + String.format("%03d", id + 1)); }
    public static long getPrice(Context c, int id) {
        long fallback = id < BASE ? DEFAULT_PRICES[id] : 0;
        return p(c).getLong(key(id, "price"), fallback);
    }
    public static int getQuantity(Context c, int id) { return p(c).getInt(key(id, "quantity"), id < BASE ? 10 : 0); }
    public static String getDetail(Context c, int id) {
        String fallback = id < BASE ? DEFAULT_DETAILS[id] : "Sản phẩm mới được thêm vào cửa hàng.";
        return p(c).getString(key(id, "detail"), fallback);
    }
    public static String getCategory(Context c, int id) {
        String fallback = id < BASE ? DEFAULT_CATEGORIES[id] : "Thiết bị đo sức khỏe";
        return p(c).getString(key(id, "category"), fallback);
    }
    public static String getStatus(Context c, int id) { return p(c).getString(key(id, "status"), "Đang bán"); }
    public static String getImageUri(Context c, int id) { return p(c).getString(key(id, "image"), ""); }
    /** Mọi sản phẩm, kể cả sản phẩm admin mới thêm, đều có ảnh dự phòng. */
    public static int getImageRes(int id) {
        return id >= 0 && id < BASE ? IMAGES[id] : R.drawable.ic_store;
    }

    /** Không để ảnh người dùng chọn làm văng ứng dụng khi URI đã hết quyền truy cập. */
    public static void showImage(android.widget.ImageView view, Context context, int id) {
        String uri = getImageUri(context, id);
        try {
            if (uri != null && !uri.trim().isEmpty()) {
                view.setImageURI(android.net.Uri.parse(uri));
                if (view.getDrawable() != null) return;
            }
        } catch (Exception ignored) { }
        view.setImageResource(getImageRes(id));
    }
    public static int create(Context c, String code, String name, long price, int quantity, String category, String date, String status, String image) {
        SharedPreferences pref = p(c);
        int id = BASE + pref.getInt("custom_product_count", 0);
        save(c, id, code, name, price, quantity, category, date, status, image);
        pref.edit().putInt("custom_product_count", pref.getInt("custom_product_count", 0) + 1).apply();
        return id;
    }
    public static void save(Context c, int id, String code, String name, long price, int quantity, String category, String date, String status, String image) {
        p(c).edit().putString(key(id, "code"), code).putString(key(id, "name"), name)
                .putLong(key(id, "price"), price).putInt(key(id, "quantity"), quantity)
                .putString(key(id, "category"), category).putString(key(id, "date"), date)
                .putString(key(id, "status"), status).putString(key(id, "image"), image)
                .putBoolean(key(id, "deleted"), false).apply();
    }
    public static void delete(Context c, int id) { p(c).edit().putBoolean(key(id, "deleted"), true).apply(); }
    public static void decreaseStock(Context c, int id) {
        int now = getQuantity(c, id);
        if (now > 0) p(c).edit().putInt(key(id, "quantity"), now - 1).apply();
    }
}
