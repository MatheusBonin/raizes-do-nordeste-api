package com.raizes.nordeste_api.controller;

import com.raizes.nordeste_api.domain.Unidade;
import com.raizes.nordeste_api.repository.UnidadeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    @Autowired
    private UnidadeRepository unidadeRepository;

    @GetMapping
    public List<Unidade> listarTodas() {
        return unidadeRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Unidade> criarUnidade(@Valid @RequestBody Unidade unidade) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadeRepository.save(unidade));
    }
}