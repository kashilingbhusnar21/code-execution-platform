package com.codeplatform.code_executor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.codeplatform.code_executor.model.CodeRequest;
import com.codeplatform.code_executor.service.S3Service;

@RestController
@RequestMapping("/api/code")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/upload-url")
    public String getUploadUrl(@RequestParam String fileName) {
        return s3Service.generateUploadUrl(fileName);
    }

    // test API
    @GetMapping("/test")
    public String test() {
        return "S3 Controller Working";
    }

    @PostMapping("/submit")
    public String submitCode(@RequestBody CodeRequest codeRequest) {
        String language = codeRequest.getLanguage();
        String fileName = codeRequest.getFileName();

        if (fileName == null || fileName.isEmpty()) {
            fileName = "Main.java";
            if (language != null && language.equalsIgnoreCase("python")) {
                fileName = "main.py";
            }
        }

        // Generate S3 upload URL
        String uploadUrl = s3Service.generateUploadUrl(fileName);

        return "Upload your file using this URL: " + uploadUrl +
                " | Language: " + language;
    }

}


