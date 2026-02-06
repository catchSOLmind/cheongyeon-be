package com.catchsolmind.cheongyeonbe.domain.eraser.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservation_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReservationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_item_id")
    private Long reservationItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // 나중에 상품이 삭제되더라도 기록은 남아야 하므로 ID와 이름을 별도 저장
    @Column(name = "suggestion_task_id")
    private Long suggestionTaskId;

    @Column(name = "task_title", nullable = false)
    private String taskTitle; // 화장실 청소

    @Column(name = "option_count", nullable = false)
    private String optionCount; // 1개

    @Column(name = "price_at_reservation", nullable = false)
    private Integer price; // 예약 당시 가격 (스냅샷)

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "visit_time", nullable = false, length = 10)
    private String visitTime;

    // 연관 관계 편의 메서드
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
