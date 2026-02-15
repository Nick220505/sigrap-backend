package com.sigrap.user.application.service;

import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @InjectMocks
    private GetUserService getUserService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        reset(userRepository);
        testUser = new User(
            new UserId(1L),
            new Username("john_doe"),
            new UserEmail("john@example.com"),
            "hashed_password",
            new HashSet<>(),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldGetUserById() {
        // Given
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        
        // When
        User result = getUserService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        
        verify(userRepository).findById(any(UserId.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getUserService.getById(999L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        assertTrue(exception.getMessage().contains("999"));
        
        verify(userRepository).findById(any(UserId.class));
    }
    
    @Test
    void shouldGetUserByUsername() {
        // Given
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.of(testUser));
        
        // When
        User result = getUserService.getByUsername("john_doe");
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        
        verify(userRepository).findByUsername(any(Username.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFoundByUsername() {
        // Given
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getUserService.getByUsername("nonexistent")
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        assertTrue(exception.getMessage().contains("nonexistent"));
        
        verify(userRepository).findByUsername(any(Username.class));
    }
    
    @Test
    void shouldGetUserByEmail() {
        // Given
        when(userRepository.findByEmail(any(UserEmail.class))).thenReturn(Optional.of(testUser));
        
        // When
        User result = getUserService.getByEmail("john@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        
        verify(userRepository).findByEmail(any(UserEmail.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Given
        when(userRepository.findByEmail(any(UserEmail.class))).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getUserService.getByEmail("nonexistent@example.com")
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        assertTrue(exception.getMessage().contains("nonexistent@example.com"));
        
        verify(userRepository).findByEmail(any(UserEmail.class));
    }
    
    @Test
    void shouldGetAllUsers() {
        // Given
        User user2 = new User(
            new UserId(2L),
            new Username("jane_doe"),
            new UserEmail("jane@example.com"),
            "hashed_password",
            new HashSet<>(),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));
        
        // When
        List<User> result = getUserService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(userRepository).findAll();
    }
    
    @Test
    void shouldGetAllEnabledUsers() {
        // Given
        when(userRepository.findAllEnabled()).thenReturn(Arrays.asList(testUser));
        
        // When
        List<User> result = getUserService.getAllEnabled();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isEnabled());
        
        verify(userRepository).findAllEnabled();
    }
}
