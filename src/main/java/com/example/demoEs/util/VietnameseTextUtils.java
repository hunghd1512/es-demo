package com.example.demoEs.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class xử lý text tiếng Việt.
 *
 * <p>Cung cấp các phương thức để loại bỏ dấu tiếng Việt (accent removal) phục vụ tìm kiếm
 * không dấu (accent-insensitive search).
 *
 * <p><strong>Implementation:</strong> Sử dụng Unicode NFD (Canonical Decomposition) để tách dấu
 * thanh khỏi ký tự gốc, sau đó loại bỏ các combining marks.
 *
 * <p><strong>Ví dụ:</strong>
 *
 * <pre>{@code
 * VietnameseTextUtils.removeAccents("Thành phố Hà Nội")  → "Thanh pho Ha Noi"
 * VietnameseTextUtils.removeAccents("Hồ Chí Minh")       → "Ho Chi Minh"
 * VietnameseTextUtils.removeAccents("Đà Nẵng")           → "Da Nang"
 * }</pre>
 */
public final class VietnameseTextUtils {

  private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{M}");

  private VietnameseTextUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Loại bỏ dấu tiếng Việt từ text.
   *
   * <p>Sử dụng Unicode NFD normalization để tách dấu thanh khỏi ký tự gốc, sau đó loại bỏ các
   * combining marks (diacritics).
   *
   * <p><strong>Ví dụ:</strong>
   *
   * <pre>
   * removeAccents("Thành phố Hà Nội")  → "Thanh pho Ha Noi"
   * removeAccents("Hồ Chí Minh")       → "Ho Chi Minh"
   * removeAccents("Đà Nẵng")           → "Da Nang"
   * removeAccents("VIỆT NAM")          → "VIET NAM"
   * </pre>
   *
   * <p><strong>Technical Details:</strong>
   *
   * <ol>
   *   <li>Replace đ/Đ explicitly (không được xử lý bởi NFD normalization)
   *   <li>Apply NFD normalization: "ế" → "e" + combining acute accent
   *   <li>Remove combining marks (\p{M}): dấu sắc, huyền, hỏi, ngã, nặng
   * </ol>
   *
   * @param text text cần loại bỏ dấu, có thể null hoặc empty
   * @return text đã loại bỏ dấu, hoặc input text nếu null/empty
   */
  public static String removeAccents(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }

    String result = text.replace('đ', 'd').replace('Đ', 'D');
    result = Normalizer.normalize(result, Normalizer.Form.NFD);
    result = DIACRITICS_PATTERN.matcher(result).replaceAll("");

    return result;
  }
}
