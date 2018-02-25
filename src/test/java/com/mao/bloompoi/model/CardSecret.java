package com.mao.bloompoi.model;

import com.mao.bloompoi.annotation.Excel;
import com.mao.bloompoi.annotation.ExcelField;
import com.mao.bloompoi.annotation.Special;
import com.mao.bloompoi.converter.UsedTypeConverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 一卡通卡密
 * @author biezhi
 * @date 2018/2/4
 */
@Excel
public class CardSecret extends ImportModel implements Serializable {

    @ExcelField(columnName = "姓名",
            special = @Special(isSpecial = true, specialColNum = 1, specialRowNum = 0))
    private String name;

    @ExcelField(order = 0, columnName = "运营商")
    private Integer cardType;

    @ExcelField(order = 1, columnName = "卡密")
    private String secret;

    @ExcelField(order = 2, columnName = "面额")
    private BigDecimal amount;

    @ExcelField(order = 3, columnName = "过期时间", datePattern = "yyyy年MM月dd日")
    private Date expiredDate;

    @ExcelField(order = 5, columnName = "使用情况", convertType = UsedTypeConverter.class)
    private Boolean used;

    public Map<String, String> map;

    public CardSecret() {
    }

    public CardSecret(String name, Integer cardType, String secret, BigDecimal amount,
            boolean used) {
        this.name = name;
        this.cardType = cardType;
        this.secret = secret;
        this.amount = amount;
        this.expiredDate = new Date();
        this.used = used;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "CardSecret{" + "cardType=" + cardType + ", secret='" + secret + '\'' + ", amount="
                + amount + ", expiredDate=" + expiredDate + ", used=" + used + '}';
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
