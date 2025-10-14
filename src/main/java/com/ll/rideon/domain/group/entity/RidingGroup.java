package com.ll.rideon.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.ll.rideon.domain.group.entity.GroupMember;

@Entity
@Table(name = "riding_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RidingGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(nullable = false, length = 6)
    private String region;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "limit_members", nullable = false)
    private Integer limitMembers;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private java.time.LocalDate deadline;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 양방향 매핑 (선택)
    @OneToMany(mappedBy = "ridingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMessage> messages;
}