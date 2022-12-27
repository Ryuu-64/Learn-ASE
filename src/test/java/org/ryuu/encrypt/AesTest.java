package org.ryuu.encrypt;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AesTest {
    private static final Stack<File> directoryStack = new Stack<>();
    private static final Stack<File> filesToBeDeletedStack = new Stack<>();

    @Test
    public void encryptAndDecryptTest() {
        AES.setKey("ase");

//        byte[] encryptBytes = AES.encrypt(new byte[]{1, 1, 4, 5, 1, 4});
//        byte[] decryptBytes = AES.decrypt(encryptBytes);
//        System.out.println(Arrays.toString(encryptBytes));
//        System.out.println(Arrays.toString(decryptBytes));
//
//        String encrypt = AES.encrypt("114514");
//        String decrypt = AES.decrypt(encrypt);
//        System.out.println(encrypt);
//        System.out.println(decrypt);

        try {
            byte[] plainBytes = Files.readAllBytes(new File("E:/SvnWorkspace/cooyocode/fingerhockey/tags/AirHockey/Air-Hockey/assetsSrc/vfx/goal.particle").toPath());
            String encryptString = Base64.getUrlEncoder().encodeToString(AES.encrypt(plainBytes));
            byte[] encryptBytes = encryptString.getBytes(StandardCharsets.UTF_8);
            byte[] decrypt = AES.decrypt(Base64.getUrlDecoder().decode(encryptBytes));
            String s = new String(Objects.requireNonNull(decrypt), StandardCharsets.UTF_8);
            System.out.println(encryptString);
            System.out.println(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void directoryEncryptTest() {
        AES.setKey("ase");
        String resourcesPath = "C:/Users/Ryuu/Documents/Learn-ASE/src/main/resources";
        String filePath = "C:/Users/Ryuu/Documents/Learn-ASE/src/main/resources/ase/test";
        String relativePath = filePath.replaceFirst(resourcesPath, "");
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        String[] directories = relativePath.split("/");
        ArrayList<String> encryptDirectories = new ArrayList<>();
        for (String directory : directories) {
            encryptDirectories.add(AES.encrypt(directory));
        }
        System.out.println(Arrays.toString(directories));
        System.out.println(encryptDirectories);
        StringBuilder encryptPath = new StringBuilder(resourcesPath);
        for (String encryptDirectory : encryptDirectories) {
            encryptPath.append("/").append(encryptDirectory);
        }
        System.out.println(encryptPath);
    }

    @Test
    public void fileEncryptTest() {
        AES.setKey("ase");
        String filePath = "C:/Users/Ryuu/Documents/Learn-ASE/src/main/resources/ase/test/ase3.txt";
        int indexOfSlash = filePath.lastIndexOf("/");
        String fileName = filePath.substring(indexOfSlash + 1);
        System.out.println(fileName);
        String encrypt = AES.encrypt(fileName);
        System.out.println(encrypt);
        String decrypt = AES.decrypt(encrypt);
        System.out.println(decrypt);
    }

    @Test
    public void fileRecursiveEncryptTest() {
        File srcDir = new File("E:/SvnWorkspace/cooyocode/fingerhockey/tags/AirHockey/Air-Hockey/assetsSrc");
        File destDir = new File("E:/SvnWorkspace/cooyocode/fingerhockey/tags/AirHockey/Air-Hockey/android/assets");
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AES.setKey("aes");
        encrypt(destDir);
    }

    private static void encrypt(File file) {
        if (!file.exists()) {
            throw new RuntimeException("path not exist: " + file.getPath());
        }

        encryptFileOrPushDirectoryIntoStack(file);
        while (!directoryStack.isEmpty()) {
            File[] children = directoryStack.pop().listFiles();
            if (children == null) {
                continue;
            }
            for (File child : children) {
                encryptFileOrPushDirectoryIntoStack(child);
            }
        }

        while (!filesToBeDeletedStack.isEmpty()) {
            try {
                Files.delete(filesToBeDeletedStack.pop().toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void encryptFileOrPushDirectoryIntoStack(File file) {
        try {
            if (!file.isDirectory()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                Files.write(
                        Path.of(file.getParent() + "/" + AES.encrypt(file.getName())),
                        Base64.getUrlEncoder().encodeToString(AES.encrypt(bytes)).getBytes(StandardCharsets.UTF_8)
                );
                filesToBeDeletedStack.push(file);
            } else {
                directoryStack.push(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}