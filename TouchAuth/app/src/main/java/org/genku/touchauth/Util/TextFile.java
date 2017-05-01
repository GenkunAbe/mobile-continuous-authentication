package org.genku.touchauth.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TextFile {

    public static void writeFile(String fileName, String content, boolean isAdd) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName, isAdd);
            byte [] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName) {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte [] temp = new byte[length];
            fin.read(temp);
            res = new String(temp, "UTF-8");
            fin.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void makeRootDirectory(String filePath) {
        File file;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] listFile(String rootPath) {
        File[] files = new File(rootPath + "/").listFiles();
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            result[i] = rootPath + "/" + files[i].getName();
        }
        return result;
    }

    public static void writeFileFromNums(String filename, double[][] nums, boolean isAdd) {
        int size = nums.length;
        if (size < 1) return;
        if (isAdd) {
            writeFileFromNums(filename, nums[0], true, false, -1);
        } else {
            writeFileFromNums(filename, nums[0], false, false, -1);
        }
        for (int i = 1; i < size; ++i) {
            writeFileFromNums(filename, nums[i], true, false, -1);
        }
    }

    public static void writeFileFromNums(
            String filename, double[] nums,
            boolean isAdd, boolean isSvmMode, int label) {

        StringBuilder sb = new StringBuilder("");
        if (isSvmMode) {
            sb.append(label > 0 ? "+" + label : label).append("\t");
            for (int i = 0; i < nums.length; ++i) {
                sb.append(i + 1).append(":").append(nums[i]).append("\t");
            }
        }
        else {
            for (double num : nums) {
                sb.append(num).append("\t");
            }
        }
        sb.append("\n");
        writeFile(filename, sb.toString(), isAdd);
    }

    public static double[][] readFileToMatrix(String filename) {

        String rawString = readFile(filename);
        String[] lines = rawString.split("\n");
        double[][] vectors = new double[lines.length][lines[0].split("\t").length];
        for (int i = 0; i < lines.length; ++i) {
            String[] items = lines[i].split("\t");
            for (int j = 0; j < items.length; ++j) {
                vectors[i][j] = Double.parseDouble(items[j]);
            }
        }
        return vectors;
    }


}