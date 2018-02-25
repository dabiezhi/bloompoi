package com.mao.bloompoi;

import com.mao.bloompoi.exception.ExcelException;
import com.mao.bloompoi.model.CardSecret;
import com.mao.bloompoi.model.User;
import com.mao.bloompoi.reader.ExcelResult;
import com.mao.bloompoi.utils.ExcelUtils;
import com.mao.bloompoi.writer.Exporter;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2018/2/21.
 */
public class Examples {

    @Test
    public void testReadList() throws ExcelException {
        List<CardSecret> excelResult = Bloom.me()
                .read(new File("D:\\卡密列表.xls"), CardSecret.class).startRow(1).asList();
        System.out.println(excelResult);
    }

    @Test
    public void testReadResult() throws ExcelException {
        ExcelResult<CardSecret> excelResult = Bloom.me()
                .read(new File("D:\\卡密列表2.xls"), CardSecret.class).startRow(2).asResult();
        System.out.println(excelResult);
    }

    @Test
    public void testExport() throws ExcelException {
        List<CardSecret> cardSecrets = this.buildCardSecrets();
        Bloom.me().export(cardSecrets).writeAsFile(new File("D:\\卡密列表2.xls"));
    }

    @Test
    public void testExportBySpel() throws ExcelException {
        Map<Integer, Object> map = new HashMap<>();

        CardSecret secret = new CardSecret();
        secret.setCardType(1);
        secret.setSecret("niadafd");
        Map<String, String> map1 = new HashMap<>();
        map1.put("姓名", "花花");
        map1.put("年龄", "28");
        map1.put("学校", "余姚");
        secret.setMap(map1);

        CardSecret secret1 = new CardSecret();
        Map<String, String> map2 = new HashMap<>();
        map2.put("姓名", "猫");
        map2.put("年龄", "28");
        map2.put("学校", "丹东");
        secret1.setCardType(2);
        secret1.setSecret("hahahaha");
        secret1.setMap(map2);
        List<CardSecret> list = Lists.newArrayList(secret, secret1);
        List<String> titleList = Lists.newArrayList("姓名", "年龄", "学校");

        User user = new User();
        user.setList(list);
        user.setTitleList(titleList);
        map.put(0, user);

        Bloom.me().export(Exporter.create(map).byTemplate("D:\\卡密列表.xls"))
                .writeAsFileBySpel(new File("D:\\卡密列表1.xls"));
    }

    private List<CardSecret> buildCardSecrets() {
        List<CardSecret> cardSecrets = new ArrayList<>();
        cardSecrets
                .add(new CardSecret("hua", 1, "vlfdzepjmlz2y43z7er4", new BigDecimal("20"), true));
        cardSecrets
                .add(new CardSecret("hua", 2, "rasefq2rzotsmx526z6g", new BigDecimal("10"), true));
        cardSecrets
                .add(new CardSecret("hua", 2, "2ru44qut6neykb2380wt", new BigDecimal("50"), false));
        cardSecrets
                .add(new CardSecret("hua", 1, "srcb4c9fdqzuykd6q4zl", new BigDecimal("15"), true));
        return cardSecrets;
    }
}
