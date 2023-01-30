package com.example.test_upload_file.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String downloadUri;
    private long size;

    // getters and setters are not shown for brevity

}
