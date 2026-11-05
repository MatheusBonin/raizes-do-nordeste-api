package com.raizes.nordeste_api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGAMENTOS")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "forma_pagamento", nullable = false, length = 30)
    private String formaPagamento;

    @Column(name = "codigo_transacao_mock", length = 100)
    private String codigoTransacaoMock;

    @Column(name = "data_processamento", nullable = false)
    private LocalDateTime dataProcessamento = LocalDateTime.now();

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getCodigoTransacaoMock() { return codigoTransacaoMock; }
    public void setCodigoTransacaoMock(String codigoTransacaoMock) { this.codigoTransacaoMock = codigoTransacaoMock; }

    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(LocalDateTime dataProcessamento) { this.dataProcessamento = dataProcessamento; }
}