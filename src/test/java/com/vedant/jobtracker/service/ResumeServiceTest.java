package com.vedant.jobtracker.service;

import com.vedant.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResumeService resumeService;

    @Test
    void upload_withNonPdfFile_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.txt", "text/plain", "just some text".getBytes()
        );

        assertThrows(IllegalArgumentException.class,
                () -> resumeService.upload(file, "user@example.com"));
    }

    @Test
    void upload_withEmptyFile_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", new byte[0]
        );

        assertThrows(IllegalArgumentException.class,
                () -> resumeService.upload(file, "user@example.com"));
    }
}
