package com.example.demo.server;

import com.example.demo.util.Visitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;

@Service
public class MutationOperator {
    public static final Visitor visitor=new Visitor();

    public void ResolveJavaFile(MultipartFile file,String type)  {
        String content="package com.example.demo.MyTest;";
        byte[] PackageContent=content.getBytes();
        String Filename=file.getOriginalFilename();
        String filePath="F:\\大三课程\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\"+ Filename;
        assert Filename != null;
        String JavaName= Arrays.stream(Filename.split("\\.")).findFirst().get();
        File Java_file=new File(filePath);
        try {
            if (Java_file.createNewFile()) {
               // log.info("文件创建成功！");
                try(FileOutputStream fos = new FileOutputStream(filePath)){
                    fos.write(PackageContent);
                    fos.write(file.getBytes());
                }
            } else {
                //log.info("文件创建失败！");
            }
            File AfterFile=new File(filePath);
            FileInputStream fis=new FileInputStream(AfterFile);
            JavaParser javaParser=new JavaParser();
            ParseResult<CompilationUnit> result=javaParser.parse(fis);
            CompilationUnit compilationUnit=result.getResult().get();
            // 获取变异次数
            Visitor.ABSPreVisitor absPreVisitor=new Visitor.ABSPreVisitor();
            absPreVisitor.visit(compilationUnit,null);
            Properties.RANDOM_SEED = 12345L; // 设置随机种子
            Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.BRANCH}; // 设置覆盖准则
            String classes="com.example.demo.MyTest."+JavaName;
            // 调用Evosuite生成测试用例,生成两个
            EvoSuite evosuite = new EvoSuite();
            String[] command = { "-generateSuite", "-class", classes, "-projectCP", "target/classes", "-Dtest_dir", "src/test/evosuite" };
            evosuite.parseCommandLine(command);
            // 获取运行环境
            Runtime runtime = Runtime.getRuntime();
            Process process = null;
            int number= absPreVisitor.getNumber();
            //log.info(String.valueOf(number));
            for(int i=0;i<number;i++){
                compilationUnit.accept(visitor.getVisitor(type,i),null);
                String mutatedCode=result.getResult().get().toString();
                System.out.println(mutatedCode);
                saveToFile(mutatedCode,filePath);
                // 通过命令行执行test操作
                process= runtime.exec("cmd /c   cd F:\\three\\mobileInternet\\backend\\demo &&mvn test");
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                // 读取命令的输出
                String line;
                while ((line = reader.readLine()) != null) {
                    // 处理输出结果，通过样例通过情况，来得出变异测试结果
                   // System.out.println(line);
                    if(line.startsWith("Tests")){
                        System.out.println("This is a result of Mutation test");
                        System.out.println(line);
                    }
                }
            }
            // 防止用户传入空文件
            assert process != null;
            process.waitFor();
            process.destroy();
        } catch (IOException  | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static void saveToFile(String content, String filepath) throws IOException {
        FileWriter fw = new FileWriter(filepath);
        fw.write(content);
        fw.close();
    }
    public static void main(String[] args) {
        String content="package com.example.demo.MyTest;";
        byte[] PackageContent=content.getBytes();
        String filePath="F:\\three\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\me.java";
        try {
            File AfterFile=new File(filePath);
            FileInputStream fis=new FileInputStream(AfterFile);
            JavaParser javaParser=new JavaParser();
            ParseResult<CompilationUnit> result=javaParser.parse(fis);
            CompilationUnit compilationUnit=result.getResult().get();
            // 获取变异次数
            Visitor.ABSPreVisitor absPreVisitor=new Visitor.ABSPreVisitor();
            absPreVisitor.visit(compilationUnit,null);
            Properties.RANDOM_SEED = 12345L; // 设置随机种子
            Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.BRANCH}; // 设置覆盖准则
            String classes="com.example.demo.MyTest.me";
            // 调用Evosuite生成测试用例
            EvoSuite evosuite = new EvoSuite();
            // System.setProperty("tools_jar_location","C:\\Users\\windows\\.jdks\\corretto-1.8.0_382\\lib\\tools.jar");
            String[] command = { "-generateSuite", "-class", classes, "-projectCP", "target/classes", "-Dtest_dir", "src/test/evosuite" };

           // int exitCode = process.waitFor();
            //process.destroy();
           // if (exitCode == 0) {
               // System.out.println("命令行进程执行完成");
           // } else {
            //    System.out.println("命令行进程执行失败，退出码：" + exitCode);
          //  }

            //String className = "src/test/evosuite/com/example/demo/MyTest/me_ESTest.java";
            //Class<?> testClass = Class.forName(className);
            int number= absPreVisitor.getNumber();
            //log.info(String.valueOf(number));
            Runtime runtime = Runtime.getRuntime();
            Process process = null;

            // 读取命令的输出
            for(int i=0;i<number;i++){
                compilationUnit.accept(visitor.getVisitor("ABS",i),null);
                String mutatedCode=result.getResult().get().toString();
                saveToFile(mutatedCode,filePath);
               // TimeUnit.SECONDS.sleep(1);
                process= runtime.exec("cmd /c   cd F:\\three\\mobileInternet\\backend\\demo &&mvn test");
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // 读取命令的输出
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.startsWith("Tests")){
                        System.out.println("This is a result of Mutation test");
                        System.out.println(line);
                    }
                }
                process.waitFor();
                process.destroy();

            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
