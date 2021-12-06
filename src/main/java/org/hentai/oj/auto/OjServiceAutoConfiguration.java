package org.hentai.oj.auto;

import org.hentai.oj.bean.OjProperties;
import org.hentai.oj.service.OjService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OjProperties.class)
public class OjServiceAutoConfiguration {

    @ConditionalOnMissingBean(OjService.class)
    @Bean
    public OjService ojService() {
        return new OjService();
    }

}
