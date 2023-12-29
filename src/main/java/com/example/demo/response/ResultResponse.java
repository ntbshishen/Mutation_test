package com.example.demo.response;

import lombok.Data;

import java.util.List;

@Data
public class ResultResponse {
    // 返回变异体
    private List<String> MutationBody;
    // 变异测试结果
    private List<String> MutationTestResult;
    public ResultResponse(List<String> Body, List<String> Result){
        this.MutationBody=Body;
        this.MutationTestResult=Result;

    }

}
