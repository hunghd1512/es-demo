# Elasticsearch Overview

## 1. Elasticsearch Object Mapping

### Mapping Elasticsearch vs Database
- Với các index phức tạp, có cấu trúc lồng nhau (nested):
    - Thường lưu JSON trong database.
    - Nhược điểm: tốn thời gian để parse JSON khi lấy dữ liệu.
- Với cấu trúc đơn giản, có thể map 1-1 các trường giữa DB và ES.
- Thông thường lưu dữ liệu ở database trước, lấy ID làm tham chiếu cho ID bên Elasticsearch.

### Mapping field trong schema
- **Field type**:
    - `text`: được analyze (phân tích, tách từ).
    - `keyword`: không analyze (lưu nguyên giá trị).
- Có quá nhiều field được analyze sẽ ảnh hưởng đến hiệu suất.
- Nếu cần search full-text trên nhiều field, thường gộp tất cả các field đó nối thành 1 chuỗi duy nhất.
- Các field khác có thể lưu dư thừa để thuận tiện cho việc detect, hiển thị nhanh chóng.

---

## 2. Elasticsearch Operations - Queries

- **String Query**: Viết truy vấn dạng chuỗi JSON, giảm số lượng code, nhưng khó debug khi lỗi.
- **Native Query**: Xây dựng truy vấn linh động với các dạng như:
    - `match`, `match_phrase`, `bool`, `term`, `wildcard`, v.v.
- **Aggregation**: Tổng hợp dữ liệu, ví dụ như `sum`, `avg`, `terms`, `count`,...

---

## 3. Response Search

- **SearchHit**: Đại diện cho một bản ghi trả về, chứa các thông tin như:
    - Điểm số (score)
    - Highlight (phần nội dung được tô sáng)
    - Giá trị sort
    - Inner hits (nếu có nested query)
- **SearchPage**: Chứa các đối tượng `SearchHit<T>`, dùng cho phân trang.

---

## 4. Suggestion

- Hỗ trợ đề xuất kết quả tìm kiếm gần đúng hoặc tự động hoàn thành.

---

## 5. Analyze Setting

- Thường cấu hình file JSON để thiết lập analyzer cho index.
- Mặc định analyzer phân tách theo khoảng trắng.
- Thường thêm bộ lọc bỏ dấu, không phân biệt hoa thường.
- Hỗ trợ sub-text search (tìm kiếm theo cụm con).
- Cần cân nhắc thiết kế analyzer phù hợp với số lượng bản ghi và mục đích tìm kiếm vì nó ảnh hưởng trực tiếp tới hiệu suất (tốc độ, bộ nhớ).

---

## 6. Re-index khi

- Cấu trúc database hoặc schema thay đổi.
- Thay đổi analyzer (cách phân tích từ khóa).
- Cập nhật dữ liệu mới nhất từ database sang Elasticsearch.

---

## 7. Logic Search

- Bản ghi có score cao (khớp nhiều điều kiện) sẽ được ưu tiên hiển thị.
- Cần chuẩn hóa và chỉnh sửa dữ liệu để tối ưu kết quả tìm kiếm.

---

**Chúc bạn triển khai Elasticsearch hiệu quả và thành công!**
