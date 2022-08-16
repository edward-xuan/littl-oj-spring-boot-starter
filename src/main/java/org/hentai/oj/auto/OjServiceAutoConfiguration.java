package org.hentai.oj.auto;

import org.hentai.oj.bean.OjAdminProperties;
import org.hentai.oj.bean.OjProperties;
import org.hentai.oj.service.OjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OjProperties.class)
public class OjServiceAutoConfiguration {

    @Autowired
    OjProperties ojProperties;

    @ConditionalOnMissingBean(OjService.class)
    @Bean
    public OjService ojService() {
        OjAdminProperties ojAdminProperties = OjAdminProperties.getOjAdminProperties(ojProperties);
        return new OjService(ojAdminProperties);
    }

}
