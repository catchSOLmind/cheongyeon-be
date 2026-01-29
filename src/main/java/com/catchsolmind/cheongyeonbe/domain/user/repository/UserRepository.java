package com.catchsolmind.cheongyeonbe.domain.user.repository;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
