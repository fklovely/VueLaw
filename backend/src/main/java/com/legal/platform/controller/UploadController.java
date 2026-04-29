package com.legal.platform.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.legal.platform.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.upload-path:uploads}")
    private String configuredUploadPath;

    private String uploadPath;

    @PostConstruct
    public void init() {
        File uploadDir = new File(configuredUploadPath);
        if (!uploadDir.isAbsolute()) {
            String userDir = System.getProperty("user.dir");
            uploadPath = userDir + File.separator + configuredUploadPath;
        } else {
            uploadPath = configuredUploadPath;
        }
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        System.out.println("Upload path: " + uploadPath);
    }

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String newFilename = IdUtil.simpleUUID() + "." + extension;
            String relativePath = datePath + "/" + newFilename;
            
            File destDir = new File(uploadPath + File.separator + datePath.replace("/", File.separator));
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            
            File dest = new File(destDir, newFilename);
            file.transferTo(dest.getAbsoluteFile());
            
            Map<String, String> data = new HashMap<>();
            data.put("url", "/api/uploads/" + relativePath);
            data.put("name", originalFilename);
            return Result.success("上传成功", data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!extension.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return Result.error("请上传图片文件");
        }
        return upload(file);
    }

    @PostMapping("/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!extension.matches("mp4|avi|mov|wmv|flv|mkv|webm")) {
            return Result.error("请上传视频文件");
        }
        return upload(file);
    }
}
