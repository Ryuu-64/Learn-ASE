package org.ryuu.encrypt;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class AesTest {
    private static final Stack<File> directoryStack = new Stack<>();

    @Test
    public void encryptAndDecryptTest() {
        AES.setKey("ase");

        byte[] encryptBytes = AES.encrypt(new byte[]{1, 1, 4, 5, 1, 4});
        byte[] decryptBytes = AES.decrypt(encryptBytes);
        System.out.println(Arrays.toString(encryptBytes));
        System.out.println(Arrays.toString(decryptBytes));

        String encrypt = AES.encrypt("114514");
        String decrypt = AES.decrypt(encrypt);
        System.out.println(encrypt);
        System.out.println(decrypt);

//        for (int i = 0; i < 1; i++) {
//            encrypt("C:/Users/Administrator/Documents/Learn-ASE/gradlew");
//        }
    }

    public static void encrypt(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("path not exist: " + path);
        }

        encryptFileOrPushDirectoryIntoStack(file);
        while (!directoryStack.isEmpty()) {
            file = directoryStack.pop();
            File[] children = file.listFiles();
            if (children == null) {
                continue;
            }
            for (File child : children) {
                encryptFileOrPushDirectoryIntoStack(child);
            }
        }
    }

    private static void encryptFileOrPushDirectoryIntoStack(File file) {
        try {
            if (!file.isDirectory()) {
                Files.write(Path.of("C:/Users/Administrator/Documents/Learn-ASE/" + AES.encrypt(file.getName())), Objects.requireNonNull(AES.encrypt(Files.readAllBytes(file.toPath()))));
            } else {
                directoryStack.push(file);
            }
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
        String filePath = "C:/Users/Ryuu/Documents/Learn-ASE/src/main/resources/ase/test/ase.txt";
        int indexOfSlash = filePath.lastIndexOf("/");
        String fileName = filePath.substring(indexOfSlash + 1);
        System.out.println(fileName);
        String encrypt = AES.encrypt(fileName);
        System.out.println(encrypt);
        String decrypt = AES.decrypt(encrypt);
        System.out.println(decrypt);
    }
}