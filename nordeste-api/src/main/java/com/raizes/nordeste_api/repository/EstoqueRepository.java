package com.raizes.nordeste_api.repository;

import com.raizes.nordeste_api.domain.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    List<Estoque> findByUnidadeId(Long unidadeId);
    Optional<Estoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);
}