package com.franciscoortega.banco_api.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.franciscoortega.banco_api.usuario.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail (String email);
    
    boolean existsByEmail(String email);
    
}
