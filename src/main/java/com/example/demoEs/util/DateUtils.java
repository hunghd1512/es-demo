package com.example.demoEs.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Utility class xử lý ngày tháng.
 *
 * <p>Cung cấp các phương thức chuyển đổi giữa {@link Date} và {@link LocalDate} phục vụ
 * Elasticsearch indexing và querying.
 */
public final class DateUtils {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  private DateUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Chuyển {@link Date} sang {@link LocalDate}.
   *
   * @param date đối tượng Date, có thể null
   * @return LocalDate tương ứng, hoặc null nếu input null
   */
  public static LocalDate toLocalDate(Date date) {
    if (date == null) {
      return null;
    }
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  /**
   * Chuyển {@link LocalDate} sang {@link Date}.
   *
   * @param localDate đối tượng LocalDate, có thể null
   * @return Date tương ứng, hoặc null nếu input null
   */
  public static Date toDate(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Parse chuỗi ngày theo format ISO (yyyy-MM-dd).
   *
   * @param dateStr chuỗi ngày
   * @return LocalDate đã parse, hoặc null nếu parse thất bại
   */
  public static LocalDate parse(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) {
      return null;
    }
    try {
      return LocalDate.parse(dateStr, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  /**
   * Format {@link LocalDate} thành chuỗi ISO.
   *
   * @param localDate đối tượng LocalDate
   * @return chuỗi ngày đã format, hoặc null nếu input null
   */
  public static String format(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }
    return localDate.format(DATE_FORMATTER);
  }
}
