# Walkthrough - Laptop E-commerce Frontend Implementation

Dự án đã được triển khai hoàn chỉnh các module cốt lõi của ứng dụng thương mại điện tử laptop, tuân thủ kiến trúc **MVVM** và **Repository Pattern**.

## 1. Kiến trúc Hệ thống

Ứng dụng được chia thành các lớp rõ rệt để đảm bảo tính dễ bảo trì và mở rộng:

-   **Presentation Layer**: Sử dụng `Activity`, `Fragment` và `ViewModel`.
-   **Domain Layer**: Các lớp `Model` đại diện cho dữ liệu thực tế trong ứng dụng.
-   **Data Layer**: Bao gồm `Repository` (điểm truy cập dữ liệu duy nhất) và `Network` (Retrofit API calls).

## 2. Các Module đã hoàn thành

### Module 1 & 2: Home Screen, Search & Filter
-   **HomeFragment**: Sử dụng `RecyclerView` với `GridLayoutManager` (2 cột) để hiển thị laptop.
-   **ProductViewModel**: Quản lý trạng thái danh sách sản phẩm, xử lý logic tìm kiếm (`q`) và lọc theo danh mục (`categoryId`) hoặc thương hiệu (`brandId`).
-   **UI Components**: Tích hợp `SearchView` và `ChipGroup` (Material Design) để người dùng thao tác lọc nhanh.

### Module 3: Product Detail
-   **ProductDetailActivity**: Hiển thị chi tiết cấu hình laptop (CPU, RAM, SSD) và mô tả.
-   **Image Loading**: Sử dụng thư viện **Glide** để tải ảnh từ URL một cách mượt mà và tối ưu bộ nhớ.

### Module 4 & 5: Checkout & Success
-   **OrderConfirmationActivity**: Cho phép nhập địa chỉ giao hàng và chọn phương thức thanh toán.
-   **Payment Flow**:
    -   **COD**: Gọi API thanh toán COD trực tiếp.
    -   **VNPay**: Lấy `paymentUrl` từ backend và mở trình duyệt để người dùng thực hiện giao dịch.
-   **OrderSuccessActivity**: Hiển thị mã giao dịch và trạng thái đơn hàng sau khi hoàn tất.

## 3. Thành phần kỹ thuật chính

| Thành phần | Công nghệ sử dụng | Mục đích |
| :--- | :--- | :--- |
| **Networking** | Retrofit 2.11.0 + Gson | Giao tiếp với Spring Boot Backend API. |
| **UI Binding** | ViewBinding | Đảm bảo an toàn kiểu dữ liệu và tránh lỗi null pointer khi truy cập view. |
| **State Mgmt** | LiveData | Quan sát và cập nhật UI tự động khi dữ liệu thay đổi. |
| **UI Kit** | Material Components | Sử dụng CardView, Button, Chip, TextInputLayout theo chuẩn thiết kế hiện đại. |
| **Utilities** | PriceFormatUtils | Định dạng tiền tệ VNĐ chính xác bằng `BigDecimal`. |

## 4. Cấu trúc thư mục mã nguồn

```text
com.ptithcm.frontend
├── activities      # Hoạt động chính: ProductDetail, Checkout, Success
├── fragments       # Các tab: HomeFragment (Search & Filter)
├── adapters        # Bộ nạp dữ liệu: ProductAdapter
├── repository      # EcommerceRepository (Data logic)
├── network         # ApiClient, ApiService, DTOs
├── models          # Domain entities: Product, Brand, Category
├── ui/viewmodels   # Logic nghiệp vụ: ProductViewModel, OrderViewModel
└── utils           # Tiện ích định dạng giá
```

Dự án hiện đã sẵn sàng để kết nối với Backend đang chạy tại `http://10.0.2.2:8080`.
