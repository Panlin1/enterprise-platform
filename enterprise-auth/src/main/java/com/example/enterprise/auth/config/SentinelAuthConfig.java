package com.example.enterprise.auth.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Auth service local flow rules (resource names match @SentinelResource).
 */
@Configuration
public class SentinelAuthConfig {

    public static final String RES_LOGIN = "auth:login";
    public static final String RES_CAPTCHA = "auth:captcha";

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        FlowRule login = new FlowRule();
        login.setResource(RES_LOGIN);
        login.setGrade(RuleConstant.FLOW_GRADE_QPS);
        login.setCount(20);
        rules.add(login);

        FlowRule captcha = new FlowRule();
        captcha.setResource(RES_CAPTCHA);
        captcha.setGrade(RuleConstant.FLOW_GRADE_QPS);
        captcha.setCount(30);
        rules.add(captcha);

        FlowRuleManager.loadRules(rules);
    }
}
