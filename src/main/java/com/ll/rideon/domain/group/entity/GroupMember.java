package com.ll.rideon.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import com.ll.rideon.domain.members.entity.Members;

@Entity
@Table(name = "group_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    @EmbeddedId
    private GroupMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId") // PK 안의 memberId 매핑
    @JoinColumn(name = "member_id")
    private Members member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId") // PK 안의 groupId 매핑
    @JoinColumn(name = "group_id")
    private RidingGroup ridingGroup;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}