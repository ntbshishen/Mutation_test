package com.example.demo.server;

import com.example.demo.response.ResultResponse;
import com.example.demo.util.Visitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MutationOperator {
    public static final Visitor visitor=new Visitor();

    public ResultResponse ResolveJavaFile(MultipartFile file,String type)  {
       // ResultResponse resultResponse=new ResultResponse();
        String Filename=file.getOriginalFilename();
        String filePath="F:\\three\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\"+ Filename;
        // 增加文件的保存目录，默认用户传入的java文件不包含package
        String content="package com.example.demo.MyTest;";
        byte[] PackageContent=content.getBytes();
        // 保存文件目录
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
            //evosuite.
            // 获取运行环境
            Runtime runtime = Runtime.getRuntime();
            Process process = null;
            int number= absPreVisitor.getNumber();
            List<String> MutationBody=new ArrayList<>();
            List<String> MutationTestResult=new ArrayList<>();
            for(int i=0;i<number;i++){
                compilationUnit.accept(visitor.getVisitor(type,i),null);
                String mutatedCode=result.getResult().get().toString();
                System.out.println(mutatedCode);
                MutationBody.add(mutatedCode);
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
                    if(line.startsWith("Tests")&&line.endsWith(JavaName+"_ESTest")){
                        System.out.println("This is a result of Mutation test");
                        System.out.println(line);
                        MutationTestResult.add(line);
                    }
                }
            }
            // 防止用户传入空文件
            assert process != null;
            process.waitFor();
            process.destroy();
            System.out.println("MutationBody"+MutationBody);
            System.out.println("MutationTestResult"+MutationTestResult);
            return new ResultResponse(MutationBody,MutationTestResult);
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
    }
}
