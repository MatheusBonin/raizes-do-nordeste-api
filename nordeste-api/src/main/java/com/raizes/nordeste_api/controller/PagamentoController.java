package com.raizes.nordeste_api.controller;

import com.raizes.nordeste_api.domain.Pagamento;
import com.raizes.nordeste_api.domain.Pedido;
import com.raizes.nordeste_api.domain.StatusPagamento;
import com.raizes.nordeste_api.domain.StatusPedido;
import com.raizes.nordeste_api.domain.Usuario;
import com.raizes.nordeste_api.dto.PagamentoRequestDTO;
import com.raizes.nordeste_api.repository.PagamentoRepository;
import com.raizes.nordeste_api.repository.PedidoRepository;
import com.raizes.nordeste_api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    @PostMapping("/mock")
    public ResponseEntity<Pagamento> processarPagamentoMock(@Valid @RequestBody PagamentoRequestDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido não está aguardando pagamento. Status atual: " + pedido.getStatus());
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getTotal());
        pagamento.setFormaPagamento(pedido.getFormaPagamento() != null ? pedido.getFormaPagamento() : "MOCK");
        pagamento.setCodigoTransacaoMock("MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (Boolean.TRUE.equals(dto.getSimularAprovacao())) {
            pagamento.setStatus(StatusPagamento.APROVADO);
            pedido.setStatus(StatusPedido.EM_PREPARO);

            // Regra de Fidelização vinculada ao consentimento da LGPD
            Usuario cliente = pedido.getCliente();
            if (Boolean.TRUE.equals(cliente.getAceiteLgpd())) {
                int pontosGanhos = pedido.getTotal().intValue();
                int saldoAtual = cliente.getPontosFidelidade() != null ? cliente.getPontosFidelidade() : 0;
                cliente.setPontosFidelidade(saldoAtual + pontosGanhos);
                usuarioRepository.save(cliente);
            }
        } else {
            pagamento.setStatus(StatusPagamento.RECUSADO);
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedidoRepository.save(pedido);
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoSalvo);
    }
}