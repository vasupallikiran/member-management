package com.surest.member.repository;

import com.surest.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE " +
            "(:firstName IS NULL OR LOWER(m.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))")
    Page<Member> findByFirstNameAndLastName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable
    );
}
