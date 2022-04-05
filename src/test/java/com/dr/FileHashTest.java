package com.dr;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHashTest {
    public static void main(String[] args) throws IOException {
        ApplicationHome home = new ApplicationHome();
        File file = new File(home.getDir(), "pom.xml");
        String guavaHash = Files.asByteSource(file).hash(Hashing.sha512()).toString();
        String commonHash = DigestUtils.sha512Hex(new FileInputStream(file));

        System.out.println(guavaHash);
        System.out.println(commonHash);
        System.out.println(commonHash.equals(guavaHash));
    }
}
