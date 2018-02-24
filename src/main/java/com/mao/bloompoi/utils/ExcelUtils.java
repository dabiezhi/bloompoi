package com.mao.bloompoi.utils;

import com.mao.bloompoi.Constant;
import com.mao.bloompoi.annotation.ExcelField;
import com.mao.bloompoi.annotation.Excel;
import com.mao.bloompoi.annotation.Special;
import com.mao.bloompoi.conterver.Converter;
import com.mao.bloompoi.conterver.EmptyConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mao.bloompoi.Constant.EMPTY_STRING;

/**
 * Created by mao on 2018/2/14.
 */
public class ExcelUtils {

    private static final Map<String, List<Field>> FIELD_CACHE = new HashMap<>(8);

    public static String getSheetName(Object item) {
        Excel excel = item.getClass().getAnnotation(Excel.class);
        if (StringUtils.isNotBlank(excel.sheetName())) {
            return Constant.SHEET_ZERO;
        }
        return excel.sheetName();
    }

    public static String getColumnValue(Object item, Field field) {

        try {
            field.setAccessible(true);
            Object value = field.get(item);
            return asString(field, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    public static Map<String, Integer> getWriteFieldNames(Class<?> type) {
        List<Field> fields = getAndSaveFields(type);
        Map<String, Integer> map = new HashMap<>();
        fields.forEach(field -> {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            map.put(excelField.columnName(), excelField.order());
        });
        return map;
    }

    public static List<Field> getAndSaveFields(Class<?> type) {
        List<Field> fields = FIELD_CACHE.getOrDefault(type.getName(), getFiledList(type));
        return fields.stream().filter(u -> null != u.getAnnotation(ExcelField.class))
                .filter(s -> StringUtils.isNotBlank(s.getAnnotation(ExcelField.class).columnName()))
                .collect(Collectors.toList());
    }

    private static List<Field> getFiledList(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        if (null == clazz) {
            return list;
        }
        if (null != clazz.getSuperclass()) {
            list.addAll(getFiledList(clazz.getSuperclass()));
        }
        // 获取所有字段（包括private）
        Field[] fields = clazz.getDeclaredFields();
        if (null != fields) {
            list.addAll(Arrays.asList(fields));
        }

        return list;
    }

    public static String validFieldByAnnotation(Object item, int row, int col, String value,
            boolean flag) {
        Field field = ExcelUtils.getFieldByCols(item.getClass(), row, col, flag);
        if (null == field) {
            return null;
        }
        ExcelField excelField = field.getAnnotation(ExcelField.class);
        if (value == null || StringUtils.isNotBlank(value) && excelField.nullable()) {
            return excelField.columnName() + "不能为空";
        }
        if (value.length() > excelField.maxLength() && excelField.maxLength() != 0) {
            return excelField.columnName() + "长度不能超过" + excelField.maxLength();
        }
        if (value.length() < excelField.minLength() && excelField.minLength() != 0) {
            return excelField.columnName() + "长度不能小于" + excelField.minLength();
        }
        switch (excelField.regexType()) {
        case NONE:
            break;
        case SPECIALCHAR:
            if (RegexUtils.hasSpecialChar(value)) {
                return excelField.columnName() + "不能含有特殊字符";
            }
            break;
        case CHINESE:
            if (RegexUtils.isChinese2(value)) {
                return excelField.columnName() + "不能含有中文字符";
            }
            break;
        case EMAIL:
            if (!RegexUtils.isEmail(value)) {
                return excelField.columnName() + "地址格式不正确";
            }
            break;
        case IP:
            if (!RegexUtils.isIp(value)) {
                return excelField.columnName() + "IP地址格式不正确";
            }
            break;
        case NUMBER:
            if (!RegexUtils.isNumber(value)) {
                return excelField.columnName() + "不是数字";
            }
            break;
        case PHONENUMBER:
            if (!RegexUtils.isPhoneNumber(value)) {
                return excelField.columnName() + "不是正规手机号";
            }
            break;
        case DATE1:
            if (!RegexUtils.isValidDate(value, "yyyy.MM.dd")) {
                return excelField.columnName() + "不是正规日期格式";
            }
            break;
        case DATE2:
            if (!RegexUtils.isValidDate(value, "yyyy.MM.dd HH:mm")) {
                return excelField.columnName() + "不是正规日期格式";
            }
            break;
        case IDENTITYCARD:
            if (!RegexUtils.isIdentifyCard(value)) {
                return excelField.columnName() + "不是正规身份证格式";
            }
            break;
        case DOUBLE:
            if (!RegexUtils.isDouble(value)) {
                return excelField.columnName() + "不是浮点格式";
            }
            break;
        default:
            break;
        }

        if (StringUtils.isNotBlank(excelField.regexExpression())
                && value.matches(excelField.regexExpression())) {
            return excelField.columnName() + "格式不正确";
        }
        return null;
    }

    public static void writeToField(Object item, int row, int col, String value, boolean flag) {
        Field field = ExcelUtils.getFieldByCols(item.getClass(), row, col, flag);
        if (null != field) {
            try {
                field.setAccessible(true);
                Object newValue = asObject(field, value);
                field.set(item, newValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Field getFieldByCols(Class<?> type, int row, int col, boolean flag) {
        List<Field> fields = getAndSaveFields(type);
        for (Field field : fields) {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if (!flag && excelField.order() == col) {
                return field;
            }
            if (flag && excelField.special().specialColNum() == col
                    && excelField.special().specialRowNum() == row) {
                return field;
            }
        }
        return null;
    }

    public static Map<String, Integer> getSpecialFieldMap(Class<?> type) {
        List<Field> fields = getAndSaveFields(type);
        Map<String, Integer> map = new HashMap<>();
        fields.forEach(field -> {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            Special special = excelField.special();
            if (special.isSpecial()) {
                map.put(special.specialRowNum() + Constant.UNDERLINE + special.specialColNum(),
                        special.specialRowNum());
            }
        });
        return map;
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> String asString(Field field, T value) {
        if (null == value) {
            return EMPTY_STRING;
        }
        ExcelField excelField = field.getAnnotation(ExcelField.class);
        if (null != excelField && !excelField.convertType().equals(EmptyConverter.class)) {
            Converter converter = newInstance(excelField.convertType());
            if (null != converter) {
                return converter.write(value);
            }
        }

        if (value instanceof Date) {
            if (null != excelField && !EMPTY_STRING.equals(excelField.datePattern())) {
                return DateUtils.toString((Date) value, excelField.datePattern());
            }
        }
        return value.toString();
    }

    private static Object asObject(Field field, String value) {
        if (null == value || "".equals(value)) {
            return null;
        }
        ExcelField excelField = field.getAnnotation(ExcelField.class);
        if (null != excelField && !excelField.convertType().equals(EmptyConverter.class)) {
            Converter converter = newInstance(excelField.convertType());
            if (null != converter) {
                return converter.read(value);
            }
        }
        if (field.getType().equals(String.class)) {
            return value;
        }
        if (field.getType().equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }
        if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
            return Long.valueOf(value);
        }
        if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
            return Integer.valueOf(value);
        }
        if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
            return Double.valueOf(value);
        }
        if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
            return Float.valueOf(value);
        }
        if (field.getType().equals(Short.class) || field.getType().equals(short.class)) {
            return Short.valueOf(value);
        }
        if (field.getType().equals(Byte.class) || field.getType().equals(byte.class)) {
            return Byte.valueOf(value);
        }
        if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            return Boolean.valueOf(value);
        }

        if (field.getType().equals(Date.class)) {
            if (null != excelField && !EMPTY_STRING.equals(excelField.datePattern())) {
                return DateUtils.toDate(value, excelField.datePattern());
            }
        }
        return value;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            cell.setCellType(CellType.STRING);
        }
        switch (cell.getCellTypeEnum()) {
        case NUMERIC:
            cellValue = String.valueOf(cell.getNumericCellValue());
            break;
        case STRING:
            cellValue = String.valueOf(cell.getStringCellValue());
            break;
        case BOOLEAN:
            cellValue = String.valueOf(cell.getBooleanCellValue());
            break;
        case FORMULA:
            cellValue = String.valueOf(cell.getCellFormula());
            break;
        case BLANK:
            cellValue = "";
            break;
        case ERROR:
            cellValue = "illegal character";
            break;
        default:
            cellValue = "Unknown type";
            break;
        }
        return cellValue;
    }

}
