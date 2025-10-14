package com.ll.rideon.domain.members.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.rideon.domain.members.entity.Members;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByEmail(String email);
    Optional<Members> findById(Long id);
}
