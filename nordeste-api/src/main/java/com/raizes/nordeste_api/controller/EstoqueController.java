package com.raizes.nordeste_api.controller;

import com.raizes.nordeste_api.domain.Estoque;
import com.raizes.nordeste_api.domain.Produto;
import com.raizes.nordeste_api.domain.Unidade;
import com.raizes.nordeste_api.dto.EstoqueMovimentacaoDTO;
import com.raizes.nordeste_api.repository.EstoqueRepository;
import com.raizes.nordeste_api.repository.ProdutoRepository;
import com.raizes.nordeste_api.repository.UnidadeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Estoque> consultarEstoque(@RequestParam(required = false) Long unidadeId) {
        if (unidadeId != null) {
            return estoqueRepository.findByUnidadeId(unidadeId);
        }
        return estoqueRepository.findAll();
    }

    @PostMapping("/movimentar")
    public ResponseEntity<Estoque> movimentarEstoque(@Valid @RequestBody EstoqueMovimentacaoDTO dto) {
        Unidade unidade = unidadeRepository.findById(dto.getUnidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidade.getId(), produto.getId())
                .orElseGet(() -> {
                    Estoque novo = new Estoque();
                    novo.setUnidade(unidade);
                    novo.setProduto(produto);
                    novo.setQuantidade(0);
                    return novo;
                });

        int novoSaldo = estoque.getQuantidade() + dto.getQuantidade();
        if (novoSaldo < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo de stock insuficiente para a saída. Saldo atual: " + estoque.getQuantidade());
        }

        estoque.setQuantidade(novoSaldo);
        return ResponseEntity.ok(estoqueRepository.save(estoque));
    }
}