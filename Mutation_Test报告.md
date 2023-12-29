# Mutation_test report
项目简介: 
1. 我们项目是针对于java文件变异测试的网页版应用
2. 实现方式: 采用javaParser工具包对java代码进行分析
    1. 利用javaParser生成语法树
    2. 编写visit类 通过visit访问java代码中可以替换的地方
       - 例如: 用于ABS算子的visitor以及preVisitor，后者用于预先检测记录变异的位置，前者用于进行变异生成
         ```java 
             // ABSVisitor
            public static  class ABSVisitor extends VoidVisitorAdapter<Void> {
                private int number=0;
                @Override
                public void visit(MethodCallExpr methodCall, Void arg){
                    super.visit(methodCall, arg);
        
                    if (methodCall.getName().getIdentifier().equals("abs") &&
                            methodCall.getScope().isPresent() &&
                            methodCall.getScope().get().toString().equals("Math")) {
                        if(number==0){
                            NameExpr negationExpr = new NameExpr("(-"+methodCall+")");
                            methodCall.getParentNode().ifPresent(parent -> parent.replace(methodCall, negationExpr));
                            System.out.println("Method Call: " + methodCall);
                        }
                        number++;
                    }
                }
            }
            // ABSPreVisitor
            public static  class ABSPreVisitor extends VoidVisitorAdapter<Void> {
                private int number=0;
                @Override
                public void visit(MethodCallExpr methodCall, Void arg) {
                    super.visit(methodCall, arg);
                    if (methodCall.getName().getIdentifier().equals("abs") &&
                            methodCall.getScope().isPresent() &&
                            methodCall.getScope().get().toString().equals("Math")) {
                        number++;
                        System.out.println("Method Call: " + methodCall);
        
                    }
                }
                public int getNumber(){
                    return this.number;
                }
            }
         ```
   3. 对用户传入文件的处理，首先我们会检测这个文件是否规范，目前我们只是简单检测，比如类名是否与文件名一致，其次，将文件暂存到本地，后期会实现测试结束后删除
   4. 然后利用evosuite工具针对目标类进行测试类生成，由于机器性能有限，生成过程的参数没有设置的很复杂
   5. 生成之后，利用mvn test命令，对每一个变异体进行测试。
## 前言
1. 由于这个demo使用的是evo和springboot，evo已经在18年后已经停止维护了，而我们使用的是mvn test，evo包在rep上发行的jar包之间居然有冲突，所以我们采用了手动导入jar包，然后再mvn只引用了evo-runtime工具，作用域设置为test阶段，保证在test阶段能正常运行
### 环境
- jdk 1.8
- evo 1.6
- spring 2.0.1
## 1.代码分析和过程分析
### 1.1 后端
主要分为一下五个包:
- controller
- MyTest ***用于保存用户文件***
- response ***定义返回数据结构***
- server 
  - MutationOperator 处理传入文件，实现自动化测试类
  ```java
  String Filename=file.getOriginalFilename();
  String filePath="F:\\three\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\"+ Filename;
  // 增加文件的保存目录，默认用户传入的java文件不包含package
  String content="package com.example.demo.MyTest;";
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
  ```

- util
    - Mutation_enum: 变异算子的枚举类
    - Visitor ***主要的功能实现，报错了变异算子选择和变异生成 项目简介中已经介绍***
## 2. 实现结果
### 2.1 后端运行展示
1. 测试用例的生成过程
![](\使用evo工具生成测试用例的过程.jpg)
2. 测试结果的一个用例展示
![](\测试结果展示.jpg)
### 2.2 前端页面展示
1. 主页
![](\主页.png)
1. 被杀死的变异体
![](\测试被杀死.png)
1. 没有被杀死的变异体
![](\测试未被杀死.png)

## 前端
前端使用Vue框架开发，主要有两个界面和导航组件、select选择组件和文件上传组件

图1
![](\图1.png)
App.vue中使用vue自带的router-link和router-view来进行页面导航

图2
![](\图2.png)
文件上传功能我们选择使用element-plus的自带组件el-upload来完成Java文件的上传和附带参数信息的携带

图3
![](\图3.png)
并在文件上传成功之后触发钩子函数uploadSuccess将后端返回的数据存储在localstorage中以便其他页面使用
```js
 uploadSuccess(response){
      console.log(response);
      localStorage.setItem("bodyLen",response.mutationBody.length);
      localStorage.setItem('body',JSON.stringify(response.mutationBody));
      localStorage.setItem("result",JSON.stringify(response.testResult));
    }
```

之后我们在myShow界面进行变异结果的展示，包括变异体、变异测试结果和变异测试运行的具体耗时等数据

在myShow界面渲染时，会触发生命周期钩子函数mounted，进行localstorage数据的fetch，并通过v-for渲染将数据循环展示在web页面中
图5
![](\图5.png)

