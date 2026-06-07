package com.aistore.controller;

import com.aistore.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 文件上传接口集成测试（写入测试期临时目录）。 */
@SpringBootTest
@AutoConfigureMockMvc
class FileUploadControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void upload_image_returns201_withUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "ticket.png", "image/png", new byte[]{1, 2, 3, 4});
        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").isString())
                .andExpect(jsonPath("$.url", startsWith("/api/files/")));
    }

    @Test
    void upload_nonImage_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "note.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("FILE_TYPE_NOT_ALLOWED"));
    }

    @Test
    void upload_empty_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);
        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("FILE_EMPTY"));
    }
}
