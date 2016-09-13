package com.github.javacommons.encryption;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
class POJO {

    public double dbl = 0;
}

class POJO1 extends POJO {

    public int i = 1234;
}

public class TestMain {

    public static void hwTest() throws SocketException, IOException {
        Enumeration<NetworkInterface> nic = NetworkInterface.getNetworkInterfaces();
        for (; nic.hasMoreElements();) {
            NetworkInterface n = nic.nextElement();
            System.out.println(n.getName() + " : " + java.util.Arrays.toString(n.getHardwareAddress()));
        }
        // wmic command for diskdrive id: wmic DISKDRIVE GET SerialNumber
        // wmic command for cpu id : wmic cpu get ProcessorId
        // wmic csproduct get uuid
        //Process process = Runtime.getRuntime().exec(new String[]{"wmic", "bios", "get", "serialnumber"});
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "csproduct", "get", "uuid"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        String property = sc.next();
        String serial = sc.next();
        System.out.println(property + ": " + serial);
    }

    public static void zipTest1() throws IOException, ZipException {

        ZipModel model = new ZipModel();
        model.setFileNameCharset("UTF-8");

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        parameters.setPassword("test2");
        parameters.setSourceExternalStream(true);
        parameters.setFileNameInZip("aaa/bbb.txt");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            final String fileName = "xyz/abcd漢字.txt";
            final byte[] input = "testテスト".getBytes();
            File file = new File(fileName) {
                @Override
                public boolean exists() {
                    return true;
                }

                @Override
                public boolean isDirectory() {
                    return false;
                }

                @Override
                public String getAbsolutePath() {
                    return fileName;
                }

                @Override
                public boolean isHidden() {
                    return false;
                }

                @Override
                public long lastModified() {
                    return System.currentTimeMillis();
                }

                @Override
                public long length() {
                    return input.length;
                }
            };

            zipOutputStream.putNextEntry(file, parameters);
            zipOutputStream.write(input);
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
        }

        byte[] output = byteArrayOutputStream.toByteArray();

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/home/javacommons/output.zip"));
        bos.write(output);
        bos.close();
    }

    public static void zipTest2() throws IOException, ZipException {
        ZipFile zf = new ZipFile("/home/javacommons/output.zip");
        zf.removeFile("aaa/bbb.txt");
    }

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, ZipException {
        //BouncyCastleProvider bcp = new BouncyCastleProvider();
        /*
        GnuCrypto bcp = new GnuCrypto();
        Set<Provider.Service> services = bcp.getServices();
        for (Provider.Service service : services) {
            System.out.println(service.getAlgorithm());
        }*/
        for (int i = 0; i < 5; i++) {
            String jap = CryptoUtils.randomJapaneseString(16);
            System.out.println(jap);
        }
        //GenerateMain.main(new String[0]);
        if (false) {
            return;
        }
        byte[] key = "abc".getBytes();
        byte[] sha256 = CryptoUtils.sha256(key);
        //System.out.println(sha256.length);
        sha256 = CryptoUtils.randomAsciiBytes(16);
        //CryptoEngine en = new CryptoEngine("AES", sha256);
        //CryptoEngine en = new CryptoEngine("AESFast", sha256, 10);
        //CryptoEngine en = new CryptoEngine("Blowfish", sha256, 1000);
        //byte[] data = "1234567890123456".getBytes();
        CommonKeyAlgorithm en = new CommonKeyAlgorithm();
        //en.addCryptoEngine("Blowfish", sha256, 10);
        en.addOperation("AES", sha256, 1);
        byte[] data = "abc".getBytes();
        data = ("|" + CryptoUtils.randomAsciiString(1024)).getBytes();
        byte[] enc = en.encryptToBytes(data);
        //String enc2 = en.encryptToBase64(data);
        //System.out.println("enc2=" + enc2);
        //System.out.println(enc.length);
        byte[] dec = en.decryptFromBytes(enc);
        System.out.println(new String(dec));

        //Double x = 1.23456789;
        POJO x = new POJO1();
        x.dbl = 1.23456789;
        String x64 = en.objectToBase64(x);
        System.out.println("x64=" + x64);
        POJO y = en.objectFromBase64(x64, POJO.class);
        System.out.println(y.getClass().getName());
        System.out.println(y);
        System.out.println("(x == y)" + (x.dbl == y.dbl));
        ////System.out.println(RandomStringUtils.randomAscii(16));
        zipTest1();
        zipTest2();
        hwTest();
    }

}

class Zip {

    public static byte[] encryptZip(final String fileName, final byte[] input, String password) throws IOException, ZipException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        parameters.setPassword(password);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            File file = new File(fileName) {
                @Override
                public boolean exists() {
                    return true;
                }

                @Override
                public boolean isDirectory() {
                    return false;
                }

                @Override
                public String getAbsolutePath() {
                    return fileName;
                }

                @Override
                public boolean isHidden() {
                    return false;
                }

                @Override
                public long lastModified() {
                    return System.currentTimeMillis();
                }

                @Override
                public long length() {
                    return input.length;
                }
            };

            zipOutputStream.putNextEntry(file, parameters);
            zipOutputStream.write(input);
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
        }

        return byteArrayOutputStream.toByteArray();

    }
}
