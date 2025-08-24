package com.ll.rideon.domain.users.service;

import com.ll.rideon.domain.users.dto.UserRegisterDto;
import com.ll.rideon.domain.users.entity.AuthProvider;
import com.ll.rideon.domain.users.entity.Users;
import com.ll.rideon.domain.users.repository.UserRepository;
import com.ll.rideon.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private UserRegisterDto validRegisterDto;
    private UserRegisterDto nullProviderRegisterDto;
    private UserRegisterDto emptyProviderRegisterDto;
    private UserRegisterDto invalidProviderRegisterDto;

    @BeforeEach
    void setUp() {
        // 정상적인 회원가입 DTO
        validRegisterDto = new UserRegisterDto();
        validRegisterDto.setEmail("test@example.com");
        validRegisterDto.setPassword("password123");
        validRegisterDto.setName("테스트 사용자");
        validRegisterDto.setGender("남");
        validRegisterDto.setBirthDate("1990-01-01");
        validRegisterDto.setPhone("010-1234-5678");
        validRegisterDto.setProvider("original");

        // provider가 null인 DTO
        nullProviderRegisterDto = new UserRegisterDto();
        nullProviderRegisterDto.setEmail("test2@example.com");
        nullProviderRegisterDto.setPassword("password123");
        nullProviderRegisterDto.setName("테스트 사용자2");
        nullProviderRegisterDto.setGender("여");
        nullProviderRegisterDto.setBirthDate("1995-01-01");
        nullProviderRegisterDto.setPhone("010-9876-5432");
        nullProviderRegisterDto.setProvider(null);

        // provider가 빈 문자열인 DTO
        emptyProviderRegisterDto = new UserRegisterDto();
        emptyProviderRegisterDto.setEmail("test3@example.com");
        emptyProviderRegisterDto.setPassword("password123");
        emptyProviderRegisterDto.setName("테스트 사용자3");
        emptyProviderRegisterDto.setGender("남");
        emptyProviderRegisterDto.setBirthDate("1985-01-01");
        emptyProviderRegisterDto.setPhone("010-1111-2222");
        emptyProviderRegisterDto.setProvider("");

        // 유효하지 않은 provider DTO
        invalidProviderRegisterDto = new UserRegisterDto();
        invalidProviderRegisterDto.setEmail("test4@example.com");
        invalidProviderRegisterDto.setPassword("password123");
        invalidProviderRegisterDto.setName("테스트 사용자4");
        invalidProviderRegisterDto.setGender("여");
        invalidProviderRegisterDto.setBirthDate("2000-01-01");
        invalidProviderRegisterDto.setPhone("010-3333-4444");
        invalidProviderRegisterDto.setProvider("invalid_provider");
    }

    @Test
    @DisplayName("정상적인 회원가입 테스트")
    void register_WithValidData_ShouldSucceed() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(new Users());

        // when
        assertDoesNotThrow(() -> userService.register(validRegisterDto));

        // then
        verify(userRepository).findByEmail(validRegisterDto.getEmail());
        verify(passwordEncoder).encode(validRegisterDto.getPassword());
        verify(userRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("provider가 null인 경우 기본값으로 original 설정")
    void register_WithNullProvider_ShouldUseDefaultProvider() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            assertEquals(AuthProvider.original, savedUser.getProvider());
            return savedUser;
        });

        // when
        assertDoesNotThrow(() -> userService.register(nullProviderRegisterDto));

        // then
        verify(userRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("provider가 빈 문자열인 경우 기본값으로 original 설정")
    void register_WithEmptyProvider_ShouldUseDefaultProvider() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            assertEquals(AuthProvider.original, savedUser.getProvider());
            return savedUser;
        });

        // when
        assertDoesNotThrow(() -> userService.register(emptyProviderRegisterDto));

        // then
        verify(userRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("유효하지 않은 provider인 경우 기본값으로 original 설정")
    void register_WithInvalidProvider_ShouldUseDefaultProvider() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            assertEquals(AuthProvider.original, savedUser.getProvider());
            return savedUser;
        });

        // when
        assertDoesNotThrow(() -> userService.register(invalidProviderRegisterDto));

        // then
        verify(userRepository).save(any(Users.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시도 시 예외 발생")
    void register_WithExistingEmail_ShouldThrowException() {
        // given
        Users existingUser = new Users();
        when(userRepository.findByEmail(validRegisterDto.getEmail())).thenReturn(Optional.of(existingUser));

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> userService.register(validRegisterDto));
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    @DisplayName("다양한 유효한 provider 값들로 회원가입 테스트")
    void register_WithValidProviders_ShouldSucceed() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(new Users());

        // when & then
        for (AuthProvider provider : AuthProvider.values()) {
            UserRegisterDto dto = new UserRegisterDto();
            dto.setEmail("test" + provider.name() + "@example.com");
            dto.setPassword("password123");
            dto.setName("테스트 사용자");
            dto.setGender("남");
            dto.setBirthDate("1990-01-01");
            dto.setPhone("010-1234-5678");
            dto.setProvider(provider.name());

            assertDoesNotThrow(() -> userService.register(dto));
        }
    }
}
