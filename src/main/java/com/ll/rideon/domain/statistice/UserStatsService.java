package com.ll.rideon.domain.statistice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserStatsService {

    @PersistenceContext
    private EntityManager em;

    public long getDailyActiveUserCount() {
        return (long) em.createQuery("""
            SELECT COUNT(DISTINCT u.id)
            FROM UserLog u
            WHERE u.timestamp >= CURRENT_DATE
        """).getSingleResult();
    }

    public long getMonthlyActiveUserCount() {
        return (long) em.createQuery("""
            SELECT COUNT(DISTINCT u.id)
            FROM UserLog u
            WHERE u.timestamp >= :start
        """)
        .setParameter("start", LocalDate.now().minusMonths(1))
        .getSingleResult();
    }
}
