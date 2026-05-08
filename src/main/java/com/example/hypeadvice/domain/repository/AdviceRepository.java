package com.example.hypeadvice.domain.repository;

import com.example.hypeadvice.domain.entity.Advice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceRepository extends JpaRepository<Advice, Long> {

}
