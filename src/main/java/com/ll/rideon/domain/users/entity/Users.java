package com.ll.rideon.domain.users.entity;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * users 엔티티
 */
@Table(name = "users")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users {
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
     * 성별
     */
    private String gender;

    /**
     * 출생년도
     */
    @Column(name = "birth_date")
    private String birthDate;

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
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

}
