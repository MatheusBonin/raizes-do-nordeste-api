package com.raizes.nordeste_api.dto;

import com.raizes.nordeste_api.domain.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "O canalPedido é obrigatório")
    private CanalPedido canalPedido;

    @NotNull(message = "O clienteId é obrigatório")
    private Long clienteId;

    private String formaPagamento;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    private List<ItemRequestDTO> itens;

    public static class ItemRequestDTO {
        @NotNull(message = "O produtoId é obrigatório")
        private Long produtoId;

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        private Integer quantidade;

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }

    public CanalPedido getCanalPedido() { return canalPedido; }
    public void setCanalPedido(CanalPedido canalPedido) { this.canalPedido = canalPedido; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public List<ItemRequestDTO> getItens() { return itens; }
    public void setItens(List<ItemRequestDTO> itens) { this.itens = itens; }

    @NotNull(message = "O unidadeId é obrigatório")
    private Long unidadeId;

    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId;}
}