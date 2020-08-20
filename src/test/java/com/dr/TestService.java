package com.dr;

import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.resource.FileSystemFileResource;
import com.dr.framework.common.file.service.CommonFileService;
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
import java.util.List;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class TestService {
    Logger logger = LoggerFactory.getLogger(TestService.class);
    @Autowired
    CommonFileService commonFileService;
    ApplicationHome home = new ApplicationHome();

    @Test
    public void testAdd() throws IOException {
        File file = new File(home.getDir(), "pom.xml");
        String refId = "refId";

        commonFileService.addFile(new FileSystemFileResource(file, "aaa"), refId);
        commonFileService.addFile(new FileSystemFileResource(file, "bbb"), refId);
        commonFileService.addFile(new FileSystemFileResource(file, "ccc"), refId);
        commonFileService.addFile(new FileSystemFileResource(file, "ddd"), refId);
        List<FileInfo> fileInfos = commonFileService.list(refId);
        Assert.assertEquals(4, fileInfos.size());
        commonFileService.removeFile(fileInfos.get(2).getId());

        fileInfos = commonFileService.list(refId);
        Assert.assertEquals(3, fileInfos.size());

        commonFileService.removeFileByRef(refId);
    }

}
