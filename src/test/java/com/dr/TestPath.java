package com.dr;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestPath {
    public static void main(String[] args) {
        Path p = Paths.get("c:/");
        Path p2 = Paths.get("c:/aaa/bbb");
        System.out.println(p.relativize(p2).toString().replace(File.separator, "/"));
    }
}
