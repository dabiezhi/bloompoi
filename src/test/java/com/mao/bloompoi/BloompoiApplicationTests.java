package com.mao.bloompoi;

import com.mao.bloompoi.exception.ExcelException;
import com.mao.bloompoi.model.CardSecret;
import com.mao.bloompoi.model.User;
import com.mao.bloompoi.reader.ExcelResult;
import com.mao.bloompoi.writer.Exporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BloompoiApplicationTests {

    private Bloom bloom = new Bloom();

    @Test
    public void testReadExcel() throws ExcelException {
        List<CardSecret> cardSecrets = bloom.read(new File("卡密列表.xls"), CardSecret.class).asList();
        System.out.println(cardSecrets);
    }

    @Test
    public void testReadValid() throws ExcelException {
        ExcelResult<CardSecret> excelResult = bloom.read(new File("D:\\卡密列表.xls"), CardSecret.class)
                .startRow(2).asResult();
        System.out.println(excelResult);
    }

    @Test
    public void contextLoads() throws ExcelException {
        List<CardSecret> cardSecrets = this.buildCardSecrets();
        Map<Integer, Object> dataMap = new HashMap<>();
        dataMap.put(0, "");
        bloom.export(Exporter.create(dataMap).byTemplate("D:\\卡密列表.xlsx"))
                .writeAsFileBySpel(new File("D:\\卡密列表1.xlsx"));
    }

    @Test
    public void test() {
        List<User> list = new ArrayList<>();
        User user1 = new User();
        user1.setName("花花");
        user1.setAge(26);
        user1.setSchool("卓健");
        User user2 = new User();
        list.add(user2);
        list.forEach(u -> {
            BeanUtils.copyProperties(user1, u);
        });
        System.out.println(list.get(0).getName());
    }

    @Test
    public void testSpel() {
        // 创建一个ExpressionParser对象，用于解析表达式
        ExpressionParser parser = new SpelExpressionParser();
        // ------------使用SpEL创建数组-----------
        // ------------使用SpEL访问List集合、Map集合的元素-----------
        List<String> list = new ArrayList<String>();
        list.add("java");
        list.add("PHP");
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("math", 78.8);
        map.put("chinese", 98.6);
        map.put("english", 92.2);
        // 创建一个EvaluationContext对象，作为SpEL解析变量的上下文
        EvaluationContext ctx = new StandardEvaluationContext();
        // 设置两个变量
        ctx.setVariable("list", list);
        ctx.setVariable("myMap", map);
        // 修改并访问集合
        parser.parseExpression("#list[0]").setValue(ctx, "JavaEE");
        parser.parseExpression("#myMap['math']").setValue(ctx, 99.9);
        System.out.println("List集合中第一个元素为：" + parser.parseExpression("#list").getValue(ctx));
        System.out
                .println("map中修改后的value为：" + parser.parseExpression("#myMap[math]").getValue(ctx));
    }

    private List<CardSecret> buildCardSecrets() {
        List<CardSecret> cardSecrets = new ArrayList<>();
        cardSecrets.add(new CardSecret("hua", 1111111111, "vlfdzepjmlz2y43z7er4",
                new BigDecimal("20"), true));
        cardSecrets
                .add(new CardSecret("hua", 2, "rasefq2rzotsmx526z6g", new BigDecimal("10"), true));
        cardSecrets
                .add(new CardSecret("hua", 2, "2ru44qut6neykb2380wt", new BigDecimal("50"), false));
        cardSecrets
                .add(new CardSecret("hua", 1, "srcb4c9fdqzuykd6q4zl", new BigDecimal("15"), true));
        return cardSecrets;
    }

    @Test
    public void tt() {
        String str = "nihao{111}";
        System.out.println(str.substring(str.indexOf("{") + 1, str.indexOf("}")));
    }
}
