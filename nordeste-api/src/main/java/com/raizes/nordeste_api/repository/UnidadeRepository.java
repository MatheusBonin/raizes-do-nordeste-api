package com.raizes.nordeste_api.repository;

import com.raizes.nordeste_api.domain.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {
}