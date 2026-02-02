package com.catchsolmind.cheongyeonbe.domain.houseworktest.entity;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


// User의 현재 테스트 결과, 재테스트시 overwrite

@Entity
@Table(name = "housework_test")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseworkTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "housework_test_result_id")
    private Long resultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", nullable = false, length = 30)
    private TestResultType resultType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
