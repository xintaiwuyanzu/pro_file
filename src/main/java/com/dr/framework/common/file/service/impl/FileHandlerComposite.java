package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.BaseFile;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.FileHandler;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * 合并的文件处理器
 * 统一管理和分发
 */
public class FileHandlerComposite implements FileHandler {
    protected Collection<FileHandler> fileHandlers;

    public FileHandlerComposite(Collection<FileHandler> fileHandlers) {
        Assert.isTrue(fileHandlers != null && !fileHandlers.isEmpty(), "文件处理器不能为空！");
        List<FileHandler> handlers = new ArrayList<>(fileHandlers);
        handlers.sort(Comparator.comparingInt(FileHandler::getOrder));
        this.fileHandlers = handlers;
    }

    @Override
    public boolean canHandle(BaseFile fileInfo) {
        return true;
    }

    @Override
    public void writeFile(FileResource file, BaseFile fileInfo) throws IOException {
        for (FileHandler handler : fileHandlers) {
            if (handler.canHandle(fileInfo)) {
                handler.writeFile(file, fileInfo);
                break;
            }
        }
    }

    @Override
    public InputStream readFile(BaseFile fileInfo) throws IOException {
        for (FileHandler handler : fileHandlers) {
            if (handler.canHandle(fileInfo)) {
                return handler.readFile(fileInfo);
            }
        }
        return null;
    }

    @Override
    public void deleteFile(BaseFile fileInfo) {
        for (FileHandler handler : fileHandlers) {
            if (handler.canHandle(fileInfo)) {
                handler.deleteFile(fileInfo);
                break;
            }
        }
    }

}
