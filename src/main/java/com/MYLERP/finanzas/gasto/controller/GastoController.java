package com.MYLERP.finanzas.gasto.controller;

import com.MYLERP.finanzas.gasto.dto.GastoDTO;
import com.MYLERP.finanzas.gasto.dto.GastoRequest;
import com.MYLERP.finanzas.gasto.service.GastoService;
import com.MYLERP.shared.util.PaginacionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/finanzas/gastos")
@RequiredArgsConstructor
public class GastoController {

    private static final int TAMANO_PAGINA_MAXIMO = 100;

    private final GastoService gastoService;

    @GetMapping
    public ResponseEntity<PaginacionUtil<GastoDTO>> listar(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long usuarioId = obtenerUsuarioId(authentication);
        Pageable pageable = construirPageable(page, size);
        return ResponseEntity.ok(gastoService.listar(usuarioId, desde, hasta, pageable));
    }

    @PostMapping
    public ResponseEntity<GastoDTO> crear(Authentication authentication, @Valid @RequestBody GastoRequest request) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(gastoService.crear(usuarioId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoDTO> actualizar(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody GastoRequest request
    ) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(gastoService.actualizar(usuarioId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication authentication, @PathVariable Long id) {
        Long usuarioId = obtenerUsuarioId(authentication);
        gastoService.eliminar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    // El principal es el usuarioId (Long), tal como lo dejo JwtAuthFilter en el SecurityContext.
    private Long obtenerUsuarioId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }

    private Pageable construirPageable(int page, int size) {
        int tamanoSeguro = Math.min(Math.max(size, 1), TAMANO_PAGINA_MAXIMO);
        int paginaSegura = Math.max(page, 0);
        return PageRequest.of(paginaSegura, tamanoSeguro, Sort.by(Sort.Direction.DESC, "fecha"));
    }
}
