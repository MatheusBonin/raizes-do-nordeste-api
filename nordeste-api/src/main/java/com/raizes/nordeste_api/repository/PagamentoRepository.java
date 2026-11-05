package com.raizes.nordeste_api.repository;

import com.raizes.nordeste_api.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByPedidoId(Long pedidoId);
}