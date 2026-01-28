package com.catchsolmind.cheongyeonbe.domain.user.repository;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
