package com.github.javacommons.encryption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public final class UnicodeRangeGenerator {

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
        new File("/home/javacommons").mkdirs();
        File outputFile = new File("/home/javacommons/UnicodeRange.java");
        FileOutputStream fos = new FileOutputStream(outputFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        PrintWriter pw = new PrintWriter(osw);

        List<Integer> cpList = new ArrayList<Integer>();
        for (int i = 32; i < 0x10FFFF; i++) {
            String s = codepointToString(i);
            if (isSJIS(s)) {
                cpList.add(i);
            }
        }
        //System.out.println(cpList.size());

        pw.println("package com.github.javacommons.encryption;");
        pw.println();
        //pw.println("import java.util.Arrays;");
        //pw.println("import java.util.Collections;");
        //pw.println("import java.util.List;");
        //pw.println();
        pw.println("public class UnicodeRange {");
        //pw.println("    public static final List<String> JAPANESE_RANGE = Collections.unmodifiableList(Arrays.asList(");
        pw.println("    protected static final String[] JAPANESE_RANGE = {");

        for (int i = 0; i < cpList.size(); i++) {
            int cp = cpList.get(i);
            System.out.println(cp);
            String s = codepointToString(cp);
            System.out.println(s);
            pw.print("        ");
            if (i > 0) {
                pw.print(",");
            }
            if (s.equals("\"") || s.equals("\\")) {
                pw.printf("\"\\%s\"", s);
            } else {
                pw.printf("\"%s\"", s);
            }
            pw.println();
        }
        pw.println("    };");
        pw.println("}");
        //SecureRandom sr = new SecureRandom();
        //for (int i = 0; i < 50; i++) {
        //    System.out.println(sr.nextInt(10));
        //}

        pw.close();
    }

    static boolean isSJIS(String s) {
        String charset = "Shift_JIS";
        if (s.length() == 0) {
            return false;
        }
        try {
            byte[] bytes = s.getBytes(charset);
            String s2 = new String(bytes, charset);
            return (s.equals(s2));
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
    }

    static String codepointToString(int codepoint) {
        char[] ch = Character.toChars(codepoint);
        String result = "";
        for (int i = 0; i < ch.length; i++) {
            result += ch[i];
        }
        return result;
    }

}
