package com.ll.rideon.domain.riding.repository;

import com.ll.rideon.domain.riding.entity.RidingSession;
import com.ll.rideon.domain.riding.entity.RidingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RidingSessionRepository extends JpaRepository<RidingSession, Long> {

    @Query("SELECT rs FROM RidingSession rs WHERE rs.userId = :userId ORDER BY rs.startedAt DESC")
    Page<RidingSession> findByUserIdOrderByStartedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT rs FROM RidingSession rs WHERE rs.userId = :userId AND rs.status = :status ORDER BY rs.startedAt DESC")
    List<RidingSession> findByUserIdAndStatusOrderByStartedAtDesc(@Param("userId") Long userId, @Param("status") RidingStatus status);

    @Query("SELECT rs FROM RidingSession rs WHERE rs.userId = :userId AND rs.status = 'ACTIVE'")
    Optional<RidingSession> findActiveSessionByUserId(@Param("userId") Long userId);

    @Query("SELECT rs FROM RidingSession rs WHERE rs.userId = :userId AND rs.startedAt BETWEEN :startDate AND :endDate ORDER BY rs.startedAt DESC")
    List<RidingSession> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(rs) FROM RidingSession rs WHERE rs.userId = :userId AND rs.status = 'COMPLETED'")
    Long countCompletedSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(rs.totalDistanceKm) FROM RidingSession rs WHERE rs.userId = :userId AND rs.status = 'COMPLETED'")
    Float getTotalDistanceByUserId(@Param("userId") Long userId);
} 