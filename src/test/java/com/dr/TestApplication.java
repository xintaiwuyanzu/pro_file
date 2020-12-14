package com.dr;

import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.resource.FileSystemFileResource;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.EnableAutoMapper;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoMapper
public class TestApplication implements InitializingBean {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Autowired
    CommonFileService commonFileService;

    @Override
    public void afterPropertiesSet() throws Exception {
        SecurityHolder.set(new SecurityHolder() {
            @Override
            public ClientInfo getClientInfo() {
                return null;
            }

            @Override
            public Person currentPerson() {
                return new Person();
            }

            @Override
            public Organise currentOrganise() {
                return null;
            }

            @Override
            public String personToken() {
                return null;
            }
        });
        FileInfo info = commonFileService.addFile(new FileSystemFileResource("C:\\Users\\choux\\Desktop\\档案分类与著录标引.pdf"), "aaa");
        System.out.println(info.getId());

    }
}
