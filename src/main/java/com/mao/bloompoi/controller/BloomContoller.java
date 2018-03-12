package com.mao.bloompoi.controller;

import com.mao.bloompoi.Bloom;
import com.mao.bloompoi.model.CardSecret;
import com.mao.bloompoi.writer.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tsy
 * @Description
 * @date 14:05 2018/3/2
 */
@Controller
@RequestMapping(value = "/bloom")
public class BloomContoller {

    protected static final Logger LOG = LoggerFactory.getLogger(BloomContoller.class);

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public void list(HttpServletResponse response) {
        try {
            List<CardSecret> cardSecrets = this.buildCardSecrets();
            Bloom.me().export(cardSecrets)
                    .writeAsResponse(ResponseWrapper.create(response, "bloom.xls"));
        } catch (Exception e) {
            LOG.error("", e);
        }
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
