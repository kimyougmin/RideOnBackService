package com.ll.rideon.domain.member.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.rideon.domain.member.dto.request.PasswordUpdateRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Member 엔티티
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    /**
     * 유저 고유 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이메일
     */
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * 비밀번호
     */
    @Column(nullable = false)
    private String password;

    /**
     * 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 전화번호
     */
    private String phone;

    /**
     * 프로필 사진
     */
    @Column(name = "profile_image")
    private String profileImage;

    /**
     * 생성 일자
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 수정 일자
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 유저 소셜 ID
     */
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * 사용자 고유 번호
     */
    @Column(name = "unique_key", nullable = false, updatable = false, unique = true)
    private String uniqueKey;


    /**
     * 유저의 전화번호를 설정하는 메서드
     * @param phone 전화번호
     */
    public void activatePhone(String phone) {
        this.phone = phone;
    }

    public static User updatePassword(User user, PasswordUpdateRequest request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .phone(user.getPhone())
                .userId(user.getUserId())
                .uniqueKey(user.getUniqueKey())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
