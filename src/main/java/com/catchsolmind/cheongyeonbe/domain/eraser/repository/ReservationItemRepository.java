package com.catchsolmind.cheongyeonbe.domain.eraser.repository;

import com.catchsolmind.cheongyeonbe.domain.eraser.entity.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    // 그룹 ID와 날짜(VisitDate)로 예약 아이템 조회
    // ReservationItem -> Reservation -> User -> GroupMember -> Group 순으로 연결하여 조회
    @Query("SELECT ri FROM ReservationItem ri " +
            "JOIN FETCH ri.reservation r " +
            "JOIN FETCH r.user u " +
            "WHERE u.userId IN (" +
            "SELECT gm.user.userId FROM GroupMember gm WHERE gm.group.groupId = :groupId AND gm.status <> 'LEFT') " +
            "AND ri.visitDate = :date " +
            "ORDER BY ri.visitTime ASC")
    List<ReservationItem> findByGroupIdAndDate(
            @Param("groupId") Long groupId,
            @Param("date") LocalDate date
    );
}
