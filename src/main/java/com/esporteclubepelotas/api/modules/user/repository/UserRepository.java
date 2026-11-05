package com.esporteclubepelotas.api.modules.user.repository;

import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.cpf = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    Optional<User> findByResetPasswordToken(String resetPasswordToken);

    Optional<User> findByEmailAndVerificationCode(String email, String verificationCode);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Page<User> findAllByIsActiveTrue(Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(:includeInactive = true OR u.isActive = true) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:roleName IS NULL OR u.role.name = :roleName) AND " +
            "(:search IS NULL OR " +
            "LOWER(u.firstName) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(u.lastName) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(u.email) LIKE CONCAT('%', :search, '%') OR " +
            "u.cpf LIKE CONCAT('%', :search, '%'))")
    Page<User> findUsersWithFilters(
            @Param("search") String search,
            @Param("roleName") String roleName,
            @Param("status") UserStatus status,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );
}
