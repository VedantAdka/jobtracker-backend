package com.vedant.jobtracker.service;


import com.vedant.jobtracker.dto.ResumeResponse;
import com.vedant.jobtracker.entity.User;
import com.vedant.jobtracker.exception.ResourceNotFoundException;
import com.vedant.jobtracker.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ResumeService {

    private static final int PREVIEW_LENGTH = 300;
    // Below this, we assume it's a scanned/image-only PDF with no real text layer.
    private static final int MIN_TEXT_LENGTH = 50;

    private final UserRepository userRepository;

    public ResumeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResumeResponse upload(MultipartFile file, String email) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a file to upload.");
        }

        String filename = file.getOriginalFilename();
        boolean looksLikePdf = "application/pdf".equals(file.getContentType())
                || (filename != null && filename.toLowerCase().endsWith(".pdf"));

        if (!looksLikePdf) {
            throw new IllegalArgumentException("Only PDF files are supported.");
        }

        String text;
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            text = new PDFTextStripper().getText(document).trim();
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't read that PDF - it may be corrupted.");
        }

        if (text.length() < MIN_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "Couldn't find readable text in that PDF. It might be a scanned image rather than a text-based file."
            );
        }

        User user = getUser(email);
        user.setResumeText(text);
        user.setResumeFileName(filename);
        user.setResumeUploadedAt(LocalDateTime.now());
        userRepository.save(user);

        return toResponse(user);
    }

    public ResumeResponse getCurrent(String email) {
        return toResponse(getUser(email));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ResumeResponse toResponse(User user) {
        String text = user.getResumeText();
        if (text == null) {
            return new ResumeResponse(false, null, null, null, null);
        }
        String preview = text.length() > PREVIEW_LENGTH ? text.substring(0, PREVIEW_LENGTH) + "..." : text;
        return new ResumeResponse(true, user.getResumeFileName(), user.getResumeUploadedAt(), preview, text.length());
    }
}
