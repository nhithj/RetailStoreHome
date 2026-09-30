# RetailStoreHome

Ứng dụng Android Java + XML minh họa giao diện trang chủ quản lý sản phẩm cho cửa hàng thiết bị y tế.

## Nội dung giao diện

- Logo y tế và tên cửa hàng Thiết bị Y tế An Tâm
- Số lượng sản phẩm mẫu
- Tổng giá trị hàng hóa mẫu
- Ảnh minh họa: máy đo huyết áp, xe lăn và nhiệt kế
- Các nút: Danh sách sản phẩm, Thêm sản phẩm, Đăng nhập, Thoát
- Nhấn **Danh sách sản phẩm** để mở màn hình các gói sản phẩm có hình minh họa và giá

## Thay ảnh theo ý muốn

- Thay banner: chép ảnh mới vào app/src/main/res/drawable/ với tên banner_medical_store.png.
- Muốn thay ảnh từng sản phẩm, thêm ảnh vào cùng thư mục và đổi android:src trong layout hoặc mảng images trong ProductDetailActivity.java.
- `ScrollView` giúp giao diện không bị tràn trên màn hình nhỏ
- Chuỗi, màu sắc và hình ảnh được tách thành resource riêng

## Mở project

1. Mở Android Studio.
2. Chọn **Open** và chọn thư mục `RetailStoreHome`.
3. Chờ Gradle Sync hoàn tất.
4. Chọn máy ảo hoặc điện thoại Android rồi nhấn **Run**.

## Đưa lên GitHub

1. Trên GitHub, tạo repository mới tên `RetailStoreHome`; không chọn tạo README.
2. Trong Android Studio, chọn **VCS > Enable Version Control Integration > Git**.
3. Chọn **Git > Commit**, đánh dấu toàn bộ file và ghi `Tao giao dien trang chu`.
4. Chọn **Git > GitHub > Share Project on GitHub**, đăng nhập và chọn repository.
5. Sao chép đường dẫn repository để nộp bài.

## Lỗi thường gặp

1. **Gradle Sync Failed:** kiểm tra Internet và chọn Gradle JDK 17 tại **Settings > Build, Execution, Deployment > Build Tools > Gradle**.
2. **Không thấy giao diện hoặc app đóng:** kiểm tra `MainActivity` có khai báo trong `AndroidManifest.xml` và chạy đúng cấu hình `app`.
