package com.example.demo.util;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import  com.github.javaparser.ast.stmt.Statement;
import java.io.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
public class ReadClass {
    private final String filePath="";

    public static void main(String[] main){
        try {
            File file = new File("F:\\大三课程\\mobileInternet\\backend\\demo\\src\\main\\java\\com\\example\\demo\\MyTest\\myTest_one.java");
            FileInputStream fis=new FileInputStream(file);
            JavaParser javaParser=new JavaParser();
            ParseResult<CompilationUnit> result=javaParser.parse(fis);
            CompilationUnit compilationUnit=result.getResult().get();
           //compilationUnit.accept(new MethodCallVisitor(),null);
            //compilationUnit.accept(new AORVisitor(),null);
            //compilationUnit.accept(new LCRVisitor(),null);
            compilationUnit.accept(new RORVisitor(),null);
            //compilationUnit.accept(new AORVisitor(),null);
            System.out.println(result.getResult().get().toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

    // ABSVisitor
    private static  class ABSVisitor extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(MethodCallExpr methodCall,Void arg){
            super.visit(methodCall, arg);

            if (methodCall.getName().getIdentifier().equals("abs") &&
                    methodCall.getScope().isPresent() &&
                    methodCall.getScope().get().toString().equals("Math")) {
                NameExpr negationExpr = new NameExpr("(-"+methodCall+")");
                methodCall.getParentNode().ifPresent(parent -> parent.replace(methodCall, negationExpr));
                System.out.println("Method Call: " + methodCall);
            }
        }
    }

    //AORVisitor
    private static class AORVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(BinaryExpr binaryExpr, Void arg) {
            super.visit(binaryExpr, arg);

            // 检查是否为算术运算符表达式
            if (isArithmeticOperator(binaryExpr.getOperator())) {
                // 进行算术运算符替换
                BinaryExpr.Operator newOperator = getReplacementOperator(binaryExpr.getOperator());
                binaryExpr.setOperator(newOperator);
            }
        }

        // 检查是否为算术运算符
        private boolean isArithmeticOperator(BinaryExpr.Operator operator) {
            return operator.equals(BinaryExpr.Operator.PLUS) ||
                    operator.equals(BinaryExpr.Operator.MINUS) ||
                    operator.equals(BinaryExpr.Operator.MULTIPLY) ||
                    operator.equals(BinaryExpr.Operator.DIVIDE) ||
                    operator.equals(BinaryExpr.Operator.REMAINDER);
        }

        // 获取替换后的算术运算符
        private BinaryExpr.Operator getReplacementOperator(BinaryExpr.Operator operator) {
            // 比如将 PLUS 替换为 MINUS，MULTIPLY 替换为 DIVIDE 等等
            if (operator.equals(BinaryExpr.Operator.PLUS)) {
                return BinaryExpr.Operator.MINUS;
            } else if (operator.equals(BinaryExpr.Operator.MINUS)) {
                return BinaryExpr.Operator.PLUS;
            } else if (operator.equals(BinaryExpr.Operator.MULTIPLY)) {
                return BinaryExpr.Operator.DIVIDE;
            } else if (operator.equals(BinaryExpr.Operator.DIVIDE)) {
                return BinaryExpr.Operator.MULTIPLY;
            } else if (operator.equals(BinaryExpr.Operator.REMAINDER)) {
                return BinaryExpr.Operator.MULTIPLY;
            }

            return operator;
        }
    }
    //LCRVisitor
    private static class LCRVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(BinaryExpr binaryExpr, Void arg) {
            super.visit(binaryExpr, arg);

            // 检查是否为逻辑连接词表达式
            if (isLogicalConnector(binaryExpr.getOperator())) {
                // 进行逻辑连接词替换
                BinaryExpr.Operator newOperator = getReplacementOperator(binaryExpr.getOperator());
                binaryExpr.setOperator(newOperator);
            }
        }

        // 检查是否为逻辑连接词
        private boolean isLogicalConnector(BinaryExpr.Operator operator) {
            return operator.equals(BinaryExpr.Operator.AND) ||
                    operator.equals(BinaryExpr.Operator.OR);
        }

        // 获取替换后的逻辑连接词
        private BinaryExpr.Operator getReplacementOperator(BinaryExpr.Operator operator) {
            // 比如将 AND 替换为 OR，OR 替换为 AND 等等
            if (operator.equals(BinaryExpr.Operator.AND)) {
                return BinaryExpr.Operator.OR;
            } else if (operator.equals(BinaryExpr.Operator.OR)) {
                return BinaryExpr.Operator.AND;
            }

            return operator;
        }
    }
    // RORVisitor
    private static class RORVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(BinaryExpr binaryExpr, Void arg) {
            super.visit(binaryExpr, arg);

            // 检查是否为关系运算符表达式
            if (isRelationalOperator(binaryExpr.getOperator())) {
                // 进行关系运算符替换
                BinaryExpr.Operator newOperator = getReplacementOperator(binaryExpr.getOperator());
                binaryExpr.setOperator(newOperator);
            }
        }

        // 检查是否为关系运算符
        private boolean isRelationalOperator(BinaryExpr.Operator operator) {
            return operator.equals(BinaryExpr.Operator.EQUALS) ||
                    operator.equals(BinaryExpr.Operator.NOT_EQUALS) ||
                    operator.equals(BinaryExpr.Operator.LESS) ||
                    operator.equals(BinaryExpr.Operator.LESS_EQUALS) ||
                    operator.equals(BinaryExpr.Operator.GREATER) ||
                    operator.equals(BinaryExpr.Operator.GREATER_EQUALS);
        }

        // 获取替换后的关系运算符
        private BinaryExpr.Operator getReplacementOperator(BinaryExpr.Operator operator) {
            // 比如将 EQUALS 替换为 NOT_EQUALS，LESS 替换为 GREATER 等等
            if (operator.equals(BinaryExpr.Operator.EQUALS)) {
                return BinaryExpr.Operator.NOT_EQUALS;
            } else if (operator.equals(BinaryExpr.Operator.NOT_EQUALS)) {
                return BinaryExpr.Operator.EQUALS;
            } else if (operator.equals(BinaryExpr.Operator.LESS)) {
                return BinaryExpr.Operator.GREATER_EQUALS;
            } else if (operator.equals(BinaryExpr.Operator.LESS_EQUALS)) {
                return BinaryExpr.Operator.GREATER;
            } else if (operator.equals(BinaryExpr.Operator.GREATER)) {
                return BinaryExpr.Operator.LESS_EQUALS;
            } else if (operator.equals(BinaryExpr.Operator.GREATER_EQUALS)) {
                return BinaryExpr.Operator.LESS;
            }

            return operator;
        }
    }

    // UOIVisitor
    private static class UOIVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(UnaryExpr unaryExpr, Void arg) {
            super.visit(unaryExpr, arg);

            // 检查是否为一元运算符表达式
            if (isUnaryOperator(unaryExpr.getOperator())) {
                // 进行一元运算符替换
                UnaryExpr.Operator newOperator = getReplacementOperator(unaryExpr.getOperator());
                unaryExpr.setOperator(newOperator);
            }
        }

        // 检查是否为一元运算符
        private boolean isUnaryOperator(UnaryExpr.Operator operator) {
            // 这里只列举了一些常见的一元运算符作为示例
            return operator.equals(UnaryExpr.Operator.POSTFIX_INCREMENT) ||
                    operator.equals(UnaryExpr.Operator.PREFIX_INCREMENT) ||
                    operator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        }

        // 获取替换后的一元运算符
        private UnaryExpr.Operator getReplacementOperator(UnaryExpr.Operator operator) {
            // 比如将自增运算符（++）替换为自减运算符（--），
            // 将逻辑非运算符（!）替换为按位取反运算符（~）等等
            if (operator.equals(UnaryExpr.Operator.POSTFIX_INCREMENT) ||
                    operator.equals(UnaryExpr.Operator.PREFIX_INCREMENT)) {
                return UnaryExpr.Operator.POSTFIX_DECREMENT;
            } else if (operator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)) {
                return UnaryExpr.Operator.BITWISE_COMPLEMENT;
            }

            return operator;
        }
    }


}
