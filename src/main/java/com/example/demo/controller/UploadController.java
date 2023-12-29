package com.example.demo.controller;

import com.example.demo.response.ResultResponse;
import com.example.demo.server.MutationOperator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Upload")
@CrossOrigin
public class UploadController {
    private final MutationOperator mutationOperator= new MutationOperator();

    @PostMapping("/UpJava")
    public ResultResponse UploadJava(@RequestParam MultipartFile file, @RequestParam String type){
        //log.info("Mutation type: "+type);
        //log.info("JavaFile name: "+file.getOriginalFilename());
        return mutationOperator.ResolveJavaFile(file,type);
    }

}
