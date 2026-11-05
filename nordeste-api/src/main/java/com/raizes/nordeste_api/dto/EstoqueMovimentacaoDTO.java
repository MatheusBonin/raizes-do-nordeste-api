package com.raizes.nordeste_api.dto;

import jakarta.validation.constraints.NotNull;

public class EstoqueMovimentacaoDTO {

    @NotNull(message = "O unidadeId é obrigatório")
    private Long unidadeId;

    @NotNull(message = "O produtoId é obrigatório")
    private Long produtoId;

    @NotNull(message = "A quantidade a movimentar é obrigatória (use positivo para entrada, negativo para saída)")
    private Integer quantidade;

    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}