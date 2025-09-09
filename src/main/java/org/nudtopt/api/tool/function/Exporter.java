package org.nudtopt.api.tool.function;

import org.nudtopt.api.model.Solution;

import java.io.*;

public class Exporter {


    // 将通用化的solution存入文件
    public static void writeSolution(String path, Solution solution) throws IOException {
        System.out.println("\n正在写入文件 ......");
        File file = new File(path);
        FileOutputStream output = new FileOutputStream(file);
        ObjectOutputStream writer = new ObjectOutputStream(output);
        writer.writeObject(solution);
        writer.flush();
        writer.close();
        System.out.println("写入成功: " + path + "\n");
    }


    // 读取文件中的solution并重置score
    public static Solution readSolution(String path) throws Exception {
        System.out.println("\n正在读取历史文件: " + path);
        InputStream input = new FileInputStream(path);
        return readSolution(input);
    }
    public static Solution readSolution(InputStream input) throws Exception {
        ObjectInputStream reader = new ObjectInputStream(input);
        Solution solution = (Solution) reader.readObject();
        input.close();
        reader.close();
        System.out.println("文件读取成功！历史评分: " + solution.getScore() + "\n");
        return solution;
    }


    // 导出txt, xml, drl, csv格式文件
    public static void writeFile(StringBuilder displayString, String filePath, String suffix) throws IOException {
        File file = new File(filePath + suffix);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter fw;
        switch (suffix) {
            case ".txt":
                fw = new FileWriter(file.getAbsoluteFile());
                break;
            case ".xml":
                fw = new FileWriter(file.getAbsoluteFile());
                break;
            case ".drl":
                fw = new FileWriter(file.getAbsoluteFile());
                break;
            case ".csv":
                fw = new OutputStreamWriter(new FileOutputStream(file), "GBK");
                break;
            default:
                System.out.println("error：录入文件后缀名错误！");
                return;
        }
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("" + displayString);
        bw.close();
        System.out.println("成功将结果写入文件：" + filePath + suffix);
        /* function ends */
    }


}
