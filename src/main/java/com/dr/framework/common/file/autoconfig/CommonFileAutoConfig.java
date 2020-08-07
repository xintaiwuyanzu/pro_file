package com.dr.framework.common.file.autoconfig;

import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.common.file.service.FileMineHandler;
import com.dr.framework.common.file.service.impl.DefaultCommonFileService;
import com.dr.framework.common.file.service.impl.DefaultTikaFileMineHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用附件相关
 * 自动配置
 *
 * @author dr
 */
@Configuration
@EnableConfigurationProperties(CommonFileConfig.class)
class CommonFileAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    CommonFileService commonFileService() {
        return new DefaultCommonFileService();
    }

    @Bean
    @ConditionalOnMissingBean
    FileMineHandler fileMineHandler() {
        return new DefaultTikaFileMineHandler();
    }

}
