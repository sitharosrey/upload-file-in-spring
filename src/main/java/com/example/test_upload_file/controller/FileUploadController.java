package com.example.test_upload_file.controller;

import com.example.test_upload_file.file.FileDownloadUtil;
import com.example.test_upload_file.file.FileUploadUtil;
import com.example.test_upload_file.response.FileUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {
    private final FileUploadUtil fileUploadUtil;
    public FileUploadController(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    @PostMapping(value = "/uploadFile", consumes = {"multipart/form-data"})
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();

        String fileCode = fileUploadUtil.saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/downloadFile/" + fileCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
