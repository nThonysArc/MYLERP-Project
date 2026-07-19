package com.MYLERP.finanzas.ingreso.service;

import com.MYLERP.finanzas.categoria.model.Categoria;
import com.MYLERP.finanzas.categoria.repository.CategoriaRepository;
import com.MYLERP.finanzas.ingreso.dto.IngresoDTO;
import com.MYLERP.finanzas.ingreso.dto.IngresoRequest;
import com.MYLERP.finanzas.ingreso.model.Ingreso;
import com.MYLERP.finanzas.ingreso.repository.IngresoRepository;
import com.MYLERP.finanzas.resumen.service.ResumenService;
import com.MYLERP.shared.exception.ResourceNotFoundException;
import com.MYLERP.shared.util.PaginacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ResumenService resumenService;

    @Override
    public PaginacionUtil<IngresoDTO> listar(Long usuarioId, LocalDate desde, LocalDate hasta, Pageable pageable) {
        var page = (desde != null && hasta != null)
                ? ingresoRepository.findByUsuarioIdAndFechaBetweenAndDeletedAtIsNull(usuarioId, desde, hasta, pageable)
                : ingresoRepository.findByUsuarioIdAndDeletedAtIsNull(usuarioId, pageable);

        return PaginacionUtil.desde(page, this::mapearADTO);
    }

    @Override
    @Transactional
    public IngresoDTO crear(Long usuarioId, IngresoRequest request) {
        Categoria categoria = obtenerCategoriaValida(usuarioId, request.categoriaId());

        Ingreso ingreso = Ingreso.builder()
                .usuarioId(usuarioId)
                .categoria(categoria)
                .monto(request.monto())
                .fecha(request.fecha())
                .fuente(request.fuente())
                .recurrente(request.recurrente())
                .build();

        Ingreso guardado = ingresoRepository.save(ingreso);
        resumenService.registrarIngreso(usuarioId, guardado.getFecha(), guardado.getMonto());

        return mapearADTO(guardado);
    }

    @Override
    @Transactional
    public IngresoDTO actualizar(Long usuarioId, Long ingresoId, IngresoRequest request) {
        Ingreso ingreso = obtenerIngresoDelUsuarioOLanzar(usuarioId, ingresoId);
        Categoria categoria = obtenerCategoriaValida(usuarioId, request.categoriaId());

        // Revertir el efecto anterior antes de aplicar el nuevo, igual que en GastoServiceImpl.
        resumenService.registrarIngreso(usuarioId, ingreso.getFecha(), ingreso.getMonto().negate());

        ingreso.setCategoria(categoria);
        ingreso.setMonto(request.monto());
        ingreso.setFecha(request.fecha());
        ingreso.setFuente(request.fuente());
        ingreso.setRecurrente(request.recurrente());
        Ingreso actualizado = ingresoRepository.save(ingreso);

        resumenService.registrarIngreso(usuarioId, actualizado.getFecha(), actualizado.getMonto());

        return mapearADTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long usuarioId, Long ingresoId) {
        Ingreso ingreso = obtenerIngresoDelUsuarioOLanzar(usuarioId, ingresoId);

        ingreso.setDeletedAt(OffsetDateTime.now());
        ingresoRepository.save(ingreso);

        resumenService.registrarIngreso(usuarioId, ingreso.getFecha(), ingreso.getMonto().negate());
    }

    private Ingreso obtenerIngresoDelUsuarioOLanzar(Long usuarioId, Long ingresoId) {
        return ingresoRepository.findByIdAndUsuarioIdAndDeletedAtIsNull(ingresoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso no encontrado: " + ingresoId));
    }

    private Categoria obtenerCategoriaValida(Long usuarioId, Long categoriaId) {
        return categoriaRepository.buscarValidaParaUsuario(categoriaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria invalida o no pertenece al usuario"));
    }

    private IngresoDTO mapearADTO(Ingreso ingreso) {
        return IngresoDTO.builder()
                .id(ingreso.getId())
                .categoriaId(ingreso.getCategoria().getId())
                .categoriaNombre(ingreso.getCategoria().getNombre())
                .monto(ingreso.getMonto())
                .fecha(ingreso.getFecha())
                .fuente(ingreso.getFuente())
                .recurrente(ingreso.getRecurrente())
                .createdAt(ingreso.getCreatedAt())
                .updatedAt(ingreso.getUpdatedAt())
                .build();
    }
}
