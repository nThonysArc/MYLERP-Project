package com.MYLERP.core.usuario.service;

import com.MYLERP.core.usuario.dto.RegistroRequest;
import com.MYLERP.core.usuario.dto.UsuarioDTO;
import com.MYLERP.core.usuario.model.Usuario;

public interface UsuarioService {

    UsuarioDTO registrar(RegistroRequest request);

    // Uso interno de otros modulos (ej. AuthService) - nunca expuesto directo por controller.
    Usuario buscarPorEmailOLanzar(String email);
}
