package com.example.demo.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepo extends JpaRepository<Audit,Integer>{

    @Query("SELECT a FROM Audit a WHERE a.isEmailSent = false AND a.accessedAt <= :cutoff")
    List<Audit> findPendingLogs(@Param("cutoff") LocalDateTime tenMinutesAgo);
    
}
