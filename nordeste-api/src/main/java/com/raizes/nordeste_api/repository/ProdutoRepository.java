package com.raizes.nordeste_api.repository;

import com.raizes.nordeste_api.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Método auxiliar para listar apenas produtos disponíveis no cardápio
    List<Produto> findByDisponivelTrue();
}