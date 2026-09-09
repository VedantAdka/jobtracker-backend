package com.vedant.jobtracker.service;

import com.vedant.jobtracker.dto.JobApplicationRequest;
import com.vedant.jobtracker.dto.JobApplicationResponse;
import com.vedant.jobtracker.entity.ApplicationStatus;
import com.vedant.jobtracker.entity.JobApplication;
import com.vedant.jobtracker.entity.User;
import com.vedant.jobtracker.exception.ResourceNotFoundException;
import com.vedant.jobtracker.repository.JobApplicationRepository;
import com.vedant.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Mockito replaces the real repositories with fakes we control - this
// tests JobApplicationService's own logic (ownership checks, defaults,
// validation) without touching a real database at all.
@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private User owner;
    private User otherUser;
    private JobApplication application;

    @BeforeEach
    void setUp() {
        owner = new User("Owner", "owner@example.com", "hashed");
        owner.setId(1L);

        otherUser = new User("Someone Else", "other@example.com", "hashed");
        otherUser.setId(2L);

        application = new JobApplication("Acme Corp", "Backend Engineer", LocalDate.now(), owner);
        application.setId(10L);
        application.setStatus(ApplicationStatus.APPLIED);
    }

    @Test
    void create_defaultsStatusToApplied() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        JobApplicationRequest request = new JobApplicationRequest();
        request.setCompany("Acme Corp");
        request.setJobTitle("Backend Engineer");
        // status intentionally left unset

        JobApplicationResponse response = jobApplicationService.create(request, "owner@example.com");

        assertEquals("Acme Corp", response.getCompany());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
    }

    @Test
    void getOne_whenOwnedByCaller_returnsIt() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(jobApplicationRepository.findById(10L)).thenReturn(Optional.of(application));

        JobApplicationResponse response = jobApplicationService.getOne(10L, "owner@example.com");

        assertEquals(10L, response.getId());
    }

    @Test
    void getOne_whenOwnedBySomeoneElse_throwsNotFound() {
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(jobApplicationRepository.findById(10L)).thenReturn(Optional.of(application));

        // Same exception as "doesn't exist" - see the next test. This is
        // deliberate: someone probing another user's IDs shouldn't be able
        // to tell the two cases apart.
        assertThrows(ResourceNotFoundException.class,
                () -> jobApplicationService.getOne(10L, "other@example.com"));
    }

    @Test
    void getOne_whenApplicationDoesNotExist_throwsNotFound() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(jobApplicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> jobApplicationService.getOne(999L, "owner@example.com"));
    }

    @Test
    void delete_removesTheApplication() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(jobApplicationRepository.findById(10L)).thenReturn(Optional.of(application));

        jobApplicationService.delete(10L, "owner@example.com");

        verify(jobApplicationRepository).delete(application);
    }

    @Test
    void analyze_withoutResume_throwsAndNeverCallsTheAi() {
        owner.setResumeText(null); // no resume uploaded
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

        assertThrows(IllegalArgumentException.class,
                () -> jobApplicationService.analyze(10L, "Some job description", "owner@example.com"));

        // Confirms the precondition check happens BEFORE any AI call is
        // made, not after a failed/wasted one.
        verifyNoInteractions(aiAnalysisService);
    }

    @Test
    void analyze_withBlankJobDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> jobApplicationService.analyze(10L, "   ", "owner@example.com"));
    }
}
