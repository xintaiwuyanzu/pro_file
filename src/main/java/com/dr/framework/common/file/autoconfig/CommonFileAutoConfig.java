package com.dr.framework.common.file.autoconfig;

import com.dr.framework.common.file.FileInfoHandler;
import com.dr.framework.common.file.FileSaveHandler;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.common.file.service.impl.DefaultCommonFileService;
import com.dr.framework.common.file.service.impl.DefaultFileInfoHandler;
import com.dr.framework.common.file.service.impl.FileHandlerComposite;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

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
    FileInfoHandler fileInfoHandler() {
        return new DefaultFileInfoHandler();
    }

    @Primary
    @Bean
    FileHandlerComposite fileSaveHandler(List<FileSaveHandler> fileSaveHandlerList) {
        return new FileHandlerComposite(fileSaveHandlerList);
    }


}
