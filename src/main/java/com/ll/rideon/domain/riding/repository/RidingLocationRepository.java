package com.ll.rideon.domain.riding.repository;

import com.ll.rideon.domain.riding.entity.RidingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RidingLocationRepository extends JpaRepository<RidingLocation, Long> {

    @Query("SELECT rl FROM RidingLocation rl WHERE rl.rideSessionId = :sessionId ORDER BY rl.recordedAt ASC")
    List<RidingLocation> findByRideSessionIdOrderByRecordedAtAsc(@Param("sessionId") Long sessionId);

    @Query("SELECT rl FROM RidingLocation rl WHERE rl.rideSessionId = :sessionId AND rl.isOfflineSync = true ORDER BY rl.recordedAt ASC")
    List<RidingLocation> findOfflineSyncLocationsBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT rl FROM RidingLocation rl WHERE rl.rideSessionId = :sessionId AND rl.recordedAt BETWEEN :startTime AND :endTime ORDER BY rl.recordedAt ASC")
    List<RidingLocation> findBySessionIdAndTimeRange(@Param("sessionId") Long sessionId, 
                                                    @Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime);

    @Modifying
    @Query("UPDATE RidingLocation rl SET rl.isOfflineSync = false WHERE rl.rideSessionId = :sessionId AND rl.isOfflineSync = true")
    void markAllAsSynced(@Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(rl) FROM RidingLocation rl WHERE rl.rideSessionId = :sessionId")
    Long countByRideSessionId(@Param("sessionId") Long sessionId);
} 