package com.MYLERP.core.usuario.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MYLERP.core.usuario.dto.RegistroRequest;
import com.MYLERP.core.usuario.dto.UsuarioDTO;
import com.MYLERP.core.usuario.model.Usuario;
import com.MYLERP.core.usuario.repository.UsuarioRepository;
import com.MYLERP.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            // 409 se traduce en el GlobalExceptionHandler; aqui solo comunicamos la regla de negocio.
            throw new IllegalArgumentException("Ya existe una cuenta registrada con ese email");
        }

        Usuario usuario = Usuario.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearADTO(guardado);
    }

    @Override
    public Usuario buscarPorEmailOLanzar(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }

    private UsuarioDTO mapearADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .zonaHoraria(usuario.getZonaHoraria())
                .build();
    }
}
