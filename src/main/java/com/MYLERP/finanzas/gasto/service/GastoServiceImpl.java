package com.MYLERP.finanzas.gasto.service;

import com.MYLERP.finanzas.categoria.model.Categoria;
import com.MYLERP.finanzas.categoria.repository.CategoriaRepository;
import com.MYLERP.finanzas.gasto.dto.GastoDTO;
import com.MYLERP.finanzas.gasto.dto.GastoRequest;
import com.MYLERP.finanzas.gasto.model.Gasto;
import com.MYLERP.finanzas.gasto.repository.GastoRepository;
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
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ResumenService resumenService;

    @Override
    public PaginacionUtil<GastoDTO> listar(Long usuarioId, LocalDate desde, LocalDate hasta, Pageable pageable) {
        var page = (desde != null && hasta != null)
                ? gastoRepository.findByUsuarioIdAndFechaBetweenAndDeletedAtIsNull(usuarioId, desde, hasta, pageable)
                : gastoRepository.findByUsuarioIdAndDeletedAtIsNull(usuarioId, pageable);

        return PaginacionUtil.desde(page, this::mapearADTO);
    }

    @Override
    @Transactional
    public GastoDTO crear(Long usuarioId, GastoRequest request) {
        Categoria categoria = obtenerCategoriaValida(usuarioId, request.categoriaId());

        Gasto gasto = Gasto.builder()
                .usuarioId(usuarioId)
                .categoria(categoria)
                .monto(request.monto())
                .fecha(request.fecha())
                .descripcion(request.descripcion())
                .build();

        Gasto guardado = gastoRepository.save(gasto);

        // El agregado se actualiza en la MISMA transaccion: si algo falla despues,
        // todo hace rollback junto (el gasto no queda "huerfano" sin reflejarse en el resumen).
        resumenService.registrarGasto(usuarioId, guardado.getFecha(), guardado.getMonto());

        return mapearADTO(guardado);
    }

    @Override
    @Transactional
    public GastoDTO actualizar(Long usuarioId, Long gastoId, GastoRequest request) {
        Gasto gasto = obtenerGastoDelUsuarioOLanzar(usuarioId, gastoId);
        Categoria categoria = obtenerCategoriaValida(usuarioId, request.categoriaId());

        // Revertimos el efecto del monto/fecha anteriores antes de aplicar los nuevos,
        // para que el resumen agregado no quede desincronizado tras la edicion.
        resumenService.registrarGasto(usuarioId, gasto.getFecha(), gasto.getMonto().negate());

        gasto.setCategoria(categoria);
        gasto.setMonto(request.monto());
        gasto.setFecha(request.fecha());
        gasto.setDescripcion(request.descripcion());
        Gasto actualizado = gastoRepository.save(gasto);

        resumenService.registrarGasto(usuarioId, actualizado.getFecha(), actualizado.getMonto());

        return mapearADTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long usuarioId, Long gastoId) {
        Gasto gasto = obtenerGastoDelUsuarioOLanzar(usuarioId, gastoId);

        gasto.setDeletedAt(OffsetDateTime.now());
        gastoRepository.save(gasto);

        // Revierte el monto del resumen: un gasto borrado no debe seguir contando en el presupuesto.
        resumenService.registrarGasto(usuarioId, gasto.getFecha(), gasto.getMonto().negate());
    }

    private Gasto obtenerGastoDelUsuarioOLanzar(Long usuarioId, Long gastoId) {
        return gastoRepository.findByIdAndUsuarioIdAndDeletedAtIsNull(gastoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado: " + gastoId));
    }

    private Categoria obtenerCategoriaValida(Long usuarioId, Long categoriaId) {
        return categoriaRepository.buscarValidaParaUsuario(categoriaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria invalida o no pertenece al usuario"));
    }

    private GastoDTO mapearADTO(Gasto gasto) {
        return GastoDTO.builder()
                .id(gasto.getId())
                .categoriaId(gasto.getCategoria().getId())
                .categoriaNombre(gasto.getCategoria().getNombre())
                .monto(gasto.getMonto())
                .fecha(gasto.getFecha())
                .descripcion(gasto.getDescripcion())
                .createdAt(gasto.getCreatedAt())
                .updatedAt(gasto.getUpdatedAt())
                .build();
    }
}
