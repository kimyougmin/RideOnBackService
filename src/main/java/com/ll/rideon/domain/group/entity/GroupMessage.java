package com.ll.rideon.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.ll.rideon.domain.members.entity.Members;

@Entity
@Table(name = "group_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // sender_id → members(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Members sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();

    // group_id → riding_group(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private RidingGroup group;
}