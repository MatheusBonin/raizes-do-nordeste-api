package com.raizes.nordeste_api.dto;

import jakarta.validation.constraints.NotNull;

public class PagamentoRequestDTO {

    @NotNull(message = "O pedidoId é obrigatório")
    private Long pedidoId;

    @NotNull(message = "Informe 'simularAprovacao' (true para aprovar, false para recusar no mock)")
    private Boolean simularAprovacao;

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public Boolean getSimularAprovacao() { return simularAprovacao; }
    public void setSimularAprovacao(Boolean simularAprovacao) { this.simularAprovacao = simularAprovacao; }
}