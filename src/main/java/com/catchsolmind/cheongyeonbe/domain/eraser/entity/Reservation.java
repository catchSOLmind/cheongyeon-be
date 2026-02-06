package com.catchsolmind.cheongyeonbe.domain.eraser.entity;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자/빌더를 통해서만 객체를 생성하도록 강제
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice; // 할인 전 총 금액

    @Column(name = "used_point", nullable = false)
    private Integer usedPoint;  // 사용 포인트

    @Column(name = "final_price", nullable = false)
    private Integer finalPrice; // 최종 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.RESOLVED_BY_ERASER; // 예약 확정

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ReservationItem> items = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addReservationItem(ReservationItem item) {
        items.add(item);
        item.setReservation(this);
    }
}
