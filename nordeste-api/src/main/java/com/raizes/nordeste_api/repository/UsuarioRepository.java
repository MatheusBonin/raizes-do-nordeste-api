package com.raizes.nordeste_api.repository;

import com.raizes.nordeste_api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Método auxiliar para buscar usuário por e-mail
    Optional<Usuario> findByEmail(String email);
}