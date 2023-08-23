package com.imooc.controller;

import com.imooc.config.MinIOConfig;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.MinIOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// 文件上传
@RestController
@Slf4j
public class FileController {

    @Autowired
    private MinIOConfig minIOConfig;

    @PostMapping("/upload")
    public GraceJSONResult upload(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        MinIOUtils.uploadFile(
                minIOConfig.getBucketName(),filename,file.getInputStream());
        String imgUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + filename;

        return GraceJSONResult.ok(imgUrl);
    }
}
