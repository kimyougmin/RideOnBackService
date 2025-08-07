package com.ll.rideon.domain.riding.repository;

import com.ll.rideon.domain.riding.entity.NetworkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NetworkStatusRepository extends JpaRepository<NetworkStatus, Long> {

    @Query("SELECT ns FROM NetworkStatus ns WHERE ns.rideSessionId = :sessionId ORDER BY ns.recordedAt DESC")
    List<NetworkStatus> findByRideSessionIdOrderByRecordedAtDesc(@Param("sessionId") Long sessionId);

    @Query("SELECT ns FROM NetworkStatus ns WHERE ns.rideSessionId = :sessionId AND ns.isConnected = false ORDER BY ns.recordedAt DESC")
    List<NetworkStatus> findDisconnectionEventsBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT ns FROM NetworkStatus ns WHERE ns.rideSessionId = :sessionId AND ns.recordedAt BETWEEN :startTime AND :endTime ORDER BY ns.recordedAt ASC")
    List<NetworkStatus> findBySessionIdAndTimeRange(@Param("sessionId") Long sessionId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    @Query("SELECT AVG(ns.signalStrength) FROM NetworkStatus ns WHERE ns.rideSessionId = :sessionId")
    Double getAverageSignalStrengthBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(ns) FROM NetworkStatus ns WHERE ns.rideSessionId = :sessionId AND ns.isConnected = false")
    Long countDisconnectionEventsBySessionId(@Param("sessionId") Long sessionId);
} 