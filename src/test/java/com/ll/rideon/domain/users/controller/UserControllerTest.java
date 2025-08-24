package com.ll.rideon.domain.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.rideon.domain.users.dto.UserRegisterDto;
import com.ll.rideon.domain.users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.ll.rideon.global.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 테스트")
    void register_WithValidData_ShouldReturnSuccess() throws Exception {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setName("테스트 사용자");
        dto.setGender("남");
        dto.setBirthDate("1990-01-01");
        dto.setPhone("010-1234-5678");
        dto.setProvider("original");

        doNothing().when(userService).register(any(UserRegisterDto.class));

        // when & then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("회원가입 성공"));
    }

    @Test
    @DisplayName("provider가 null인 회원가입 요청 테스트")
    void register_WithNullProvider_ShouldReturnSuccess() throws Exception {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test2@example.com");
        dto.setPassword("password123");
        dto.setName("테스트 사용자2");
        dto.setGender("여");
        dto.setBirthDate("1995-01-01");
        dto.setPhone("010-9876-5432");
        dto.setProvider(null);

        doNothing().when(userService).register(any(UserRegisterDto.class));

        // when & then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("회원가입 성공"));
    }

    @Test
    @DisplayName("provider가 빈 문자열인 회원가입 요청 테스트")
    void register_WithEmptyProvider_ShouldReturnSuccess() throws Exception {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test3@example.com");
        dto.setPassword("password123");
        dto.setName("테스트 사용자3");
        dto.setGender("남");
        dto.setBirthDate("1985-01-01");
        dto.setPhone("010-1111-2222");
        dto.setProvider("");

        doNothing().when(userService).register(any(UserRegisterDto.class));

        // when & then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("회원가입 성공"));
    }

    @Test
    @DisplayName("유효하지 않은 provider로 회원가입 요청 테스트")
    void register_WithInvalidProvider_ShouldReturnSuccess() throws Exception {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test4@example.com");
        dto.setPassword("password123");
        dto.setName("테스트 사용자4");
        dto.setGender("여");
        dto.setBirthDate("2000-01-01");
        dto.setPhone("010-3333-4444");
        dto.setProvider("invalid_provider");

        doNothing().when(userService).register(any(UserRegisterDto.class));

        // when & then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("회원가입 성공"));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시도 시 예외 처리 테스트")
    void register_WithExistingEmail_ShouldReturnError() throws Exception {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("existing@example.com");
        dto.setPassword("password123");
        dto.setName("테스트 사용자");
        dto.setGender("남");
        dto.setBirthDate("1990-01-01");
        dto.setPhone("010-1234-5678");
        dto.setProvider("original");

        doThrow(new IllegalStateException("이미 존재하는 이메일입니다."))
                .when(userService).register(any(UserRegisterDto.class));

        // when & then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("provider 필드가 없는 회원가입 요청 테스트")
    void register_WithoutProviderField_ShouldReturnSuccess() throws Exception {
        // given
        String jsonWithoutProvider = """
                {
                    "email": "test5@example.com",
                    "password": "password123",
                    "name": "테스트 사용자5",
                    "gender": "남",
                    "birthDate": "1990-01-01",
                    "phone": "010-5555-6666"
                }
                """;

        doNothing().when(userService).register(any(UserRegisterDto.class));

        // when & then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithoutProvider))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("회원가입 성공"));
    }
}
