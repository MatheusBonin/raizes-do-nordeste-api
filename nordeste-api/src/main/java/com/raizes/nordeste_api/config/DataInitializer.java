package com.raizes.nordeste_api.config;

import com.raizes.nordeste_api.domain.PerfilUsuario;
import com.raizes.nordeste_api.domain.Usuario;
import com.raizes.nordeste_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository) {
        return args -> {
            // Verifica se já existe um usuário com este e-mail para evitar duplicidade ao reiniciar a API
            if (usuarioRepository.findByEmail("gerente@raizesnordeste.com").isEmpty()) {
                Usuario gerente = new Usuario();
                gerente.setNomeCompleto("Gerente Master Raízes");
                gerente.setEmail("gerente@raizesnordeste.com");
                gerente.setSenha("admin123"); // Em produção usaremos criptografia, por ora em texto plano para testes
                gerente.setAceiteLgpd(true);
                gerente.setPerfil(PerfilUsuario.GERENTE);

                usuarioRepository.save(gerente);
                System.out.println(">>> Usuário GERENTE padrão criado com sucesso no banco de dados! <<<");
            }
        };
    }
}