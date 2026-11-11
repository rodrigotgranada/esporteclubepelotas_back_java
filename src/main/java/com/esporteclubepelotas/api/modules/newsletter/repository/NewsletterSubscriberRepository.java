package com.esporteclubepelotas.api.modules.newsletter.repository;

import com.esporteclubepelotas.api.modules.newsletter.entity.NewsletterSubscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, UUID> {

    Optional<NewsletterSubscriber> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<NewsletterSubscriber> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

