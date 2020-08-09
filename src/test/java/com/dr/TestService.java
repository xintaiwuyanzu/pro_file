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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class TestService {
    Logger logger = LoggerFactory.getLogger(TestService.class);
    @Autowired
    CommonFileService commonFileService;

    @Test
    public void testAdd() throws IOException {
        commonFileService.addFile(new FileSystemFileResource("d:/backup6.log", "aaa"), "aaa");
        commonFileService.addFile(new FileSystemFileResource("d:/backup6.log", "bbb"), "aaa");
        commonFileService.addFile(new FileSystemFileResource("d:/backup6.log", "ccc"), "aaa");
        commonFileService.addFile(new FileSystemFileResource("d:/backup6.log", "ddd"), "aaa");
        List<FileInfo> fileInfos = commonFileService.list("aaa");
        Assert.assertEquals(4, fileInfos.size());
        commonFileService.removeFile(fileInfos.get(2).getId());
        fileInfos = commonFileService.list("aaa");
        Assert.assertEquals(3, fileInfos.size());
    }

}
