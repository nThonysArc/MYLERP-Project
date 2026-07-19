package com.MYLERP.finanzas.ingreso.controller;

import com.MYLERP.finanzas.ingreso.dto.IngresoDTO;
import com.MYLERP.finanzas.ingreso.dto.IngresoRequest;
import com.MYLERP.finanzas.ingreso.service.IngresoService;
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
@RequestMapping("/api/finanzas/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private static final int TAMANO_PAGINA_MAXIMO = 100;

    private final IngresoService ingresoService;

    @GetMapping
    public ResponseEntity<PaginacionUtil<IngresoDTO>> listar(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(ingresoService.listar(usuarioId, desde, hasta, construirPageable(page, size)));
    }

    @PostMapping
    public ResponseEntity<IngresoDTO> crear(Authentication authentication, @Valid @RequestBody IngresoRequest request) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingresoService.crear(usuarioId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresoDTO> actualizar(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody IngresoRequest request
    ) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(ingresoService.actualizar(usuarioId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication authentication, @PathVariable Long id) {
        Long usuarioId = obtenerUsuarioId(authentication);
        ingresoService.eliminar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerUsuarioId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }

    private Pageable construirPageable(int page, int size) {
        int tamanoSeguro = Math.min(Math.max(size, 1), TAMANO_PAGINA_MAXIMO);
        int paginaSegura = Math.max(page, 0);
        return PageRequest.of(paginaSegura, tamanoSeguro, Sort.by(Sort.Direction.DESC, "fecha"));
    }
}
