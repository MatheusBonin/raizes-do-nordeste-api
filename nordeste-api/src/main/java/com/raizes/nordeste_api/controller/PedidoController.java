package com.raizes.nordeste_api.controller;

import com.raizes.nordeste_api.domain.*;
import com.raizes.nordeste_api.dto.PedidoRequestDTO;
import com.raizes.nordeste_api.repository.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDITORIA_RAIZES");

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @GetMapping
    public List<Pedido> listarPedidos(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status) {
        
        if (canalPedido != null && status != null) {
            return pedidoRepository.findByCanalPedidoAndStatus(canalPedido, status);
        } else if (canalPedido != null) {
            return pedidoRepository.findByCanalPedido(canalPedido);
        } else if (status != null) {
            return pedidoRepository.findByStatus(status);
        }
        return pedidoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Unidade unidade = unidadeRepository.findById(dto.getUnidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(dto.getCanalPedido());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (PedidoRequestDTO.ItemRequestDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + itemDto.getProdutoId() + " não encontrado"));

            if (!produto.getDisponivel()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O produto " + produto.getNome() + " está indisponível no cardápio");
            }

            // Validação e baixa de Estoque por Unidade
            Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidade.getId(), produto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Produto sem registro de estoque nesta unidade"));

            if (estoque.getQuantidade() < itemDto.getQuantidade()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Estoque insuficiente para o produto '" + produto.getNome() + "'. Disponível: " + estoque.getQuantidade());
            }

            estoque.setQuantidade(estoque.getQuantidade() - itemDto.getQuantidade());
            estoqueRepository.save(estoque);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
            totalCalculado = totalCalculado.add(subtotal);

            pedido.getItens().add(itemPedido);
        }

        pedido.setTotal(totalCalculado);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        auditLog.info("[AUDITORIA] Novo pedido criado | ID: {} | Canal: {} | Unidade: {} | ClienteID: {} | Total: {}",
                pedidoSalvo.getId(), pedidoSalvo.getCanalPedido(), unidade.getNome(), cliente.getId(), pedidoSalvo.getTotal());

        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalvo);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        String novoStatusStr = body.get("status");
        if (novoStatusStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campo 'status' é obrigatório");
        }

        try {
            StatusPedido statusAnterior = pedido.getStatus();
            StatusPedido novoStatus = StatusPedido.valueOf(novoStatusStr.toUpperCase());
            pedido.setStatus(novoStatus);
            Pedido atualizado = pedidoRepository.save(pedido);

            auditLog.info("[AUDITORIA] Mudança de status do Pedido ID: {} | De: {} -> Para: {}",
                    id, statusAnterior, novoStatus);

            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        }
    }
}