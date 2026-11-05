package com.raizes.nordeste_api.repository;

import com.raizes.nordeste_api.domain.CanalPedido;
import com.raizes.nordeste_api.domain.Pedido;
import com.raizes.nordeste_api.domain.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByCanalPedidoAndStatus(CanalPedido canalPedido, StatusPedido status);
}