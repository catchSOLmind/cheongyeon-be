package com.catchsolmind.cheongyeonbe.domain.eraser.repository;

import com.catchsolmind.cheongyeonbe.domain.eraser.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
