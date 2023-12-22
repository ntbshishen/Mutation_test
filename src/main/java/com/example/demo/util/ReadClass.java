package com.example.demo.util;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;

public class ReadClass {
    private final String filePath="";
    public static final Visitor visitor=new Visitor();
    public static void main(String[] main){
        try {
            File file = new File("F:\\大三课程\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\myTest_one.java");
            FileInputStream fis=new FileInputStream(file);
            JavaParser javaParser=new JavaParser();
            ParseResult<CompilationUnit> result=javaParser.parse(fis);
            CompilationUnit compilationUnit=result.getResult().get();
            compilationUnit.accept(visitor.getVisitor("ABS"),null);
            String mutatedCode=result.getResult().get().toString();
            saveToFile(mutatedCode);
            System.out.println(result.getResult().get().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }
    private static void saveToFile(String content) throws IOException {
            FileWriter fw = new FileWriter("F:\\大三课程\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\myTest_one.java");
            fw.write(content);
            fw.close();
//        File file=new File(filePath);
//        if (file.exists()) {
//            System.out.println("File already exists.");
//        } else {
//            // 创建新文件
//            boolean created = file.createNewFile();
//            if (created) {
//                System.out.println("File created successfully.");
//                FileOutputStream fos = new FileOutputStream(file);
//                fos.write(content.getBytes());
//                fos.close();
//            } else {
//                System.out.println("Failed to create file.");
//            }
//        }

    }




}
