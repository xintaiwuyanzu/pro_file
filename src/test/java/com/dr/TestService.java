package com.dr;

import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.resource.FileSystemFileResource;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.core.security.SecurityHolder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class TestService {
    Logger logger = LoggerFactory.getLogger(TestService.class);
    @Autowired
    CommonFileService commonFileService;
    ApplicationHome home = new ApplicationHome();

    @Test
    public void testAdd() {
        File file = new File(home.getDir(), "pom.xml");
        String refId = "refId";
        SecurityHolder securityHolder = SecurityHolder.get();
        Arrays.asList("aaa", "bbb", "ccc", "ddd")
                //.parallelStream()
                .forEach(c ->
                        {
                            SecurityHolder.set(securityHolder);
                            try {
                                commonFileService.addFile(new FileSystemFileResource(file, c,1), refId);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );
        List<FileInfo> fileInfos = commonFileService.list(refId);
        Assert.assertEquals(4, fileInfos.size());
        commonFileService.removeFile(fileInfos.get(2).getId());

        fileInfos = commonFileService.list(refId);
        Assert.assertEquals(3, fileInfos.size());

        commonFileService.removeFileByRef(refId);
    }

}
