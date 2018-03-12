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
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BloompoiApplicationTests {

    @Test
    public void contextLoads() throws ExcelException {
        int i = (int) (Math.random() * 100);
        System.out.println(i);
    }

}
