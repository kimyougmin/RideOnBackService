package com.ll.rideon.domain.riding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "obstacle_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ObstacleReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "latitude", nullable = false, columnDefinition = "DOUBLE PRECISION")
    private Double latitude;

    @Column(name = "longitude", nullable = false, columnDefinition = "DOUBLE PRECISION")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, columnDefinition = "report_type_enum")
    private ReportType reportType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "report_status_enum")
    private ReportStatus status;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ObstacleReport(Long userId, Double latitude, Double longitude, 
                         ReportType reportType, String description, String image) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportType = reportType;
        this.description = description;
        this.image = image;
        this.status = ReportStatus.UNCONFIRMED;
    }

    public void updateStatus(ReportStatus status) {
        this.status = status;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public enum ReportType {
        OBSTACLE("장애물"),
        ROAD_DAMAGE("도로 손상"),
        CONSTRUCTION("공사"),
        SLIPPERY("미끄러운 도로"),
        ETC("기타");

        private final String description;

        ReportType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ReportStatus {
        UNCONFIRMED("미확인"),
        CONFIRMED("확인됨"),
        RESOLVED("해결됨");

        private final String description;

        ReportStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
