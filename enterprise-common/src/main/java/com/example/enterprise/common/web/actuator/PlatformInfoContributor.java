package com.example.enterprise.common.web.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 向 /actuator/info 追加平台标识信息。
 * <p>
 * 仅在引入 spring-boot-starter-actuator 的服务中生效。
 */
@Component
@ConditionalOnClass(InfoContributor.class)
public class PlatformInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("platform", "enterprise-platform")
                .withDetail("version", "1.0.0-SNAPSHOT");
    }
}

