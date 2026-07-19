package com.MYLERP.finanzas.ingreso.service;

import com.MYLERP.finanzas.ingreso.dto.IngresoDTO;
import com.MYLERP.finanzas.ingreso.dto.IngresoRequest;
import com.MYLERP.shared.util.PaginacionUtil;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IngresoService {

    PaginacionUtil<IngresoDTO> listar(Long usuarioId, LocalDate desde, LocalDate hasta, Pageable pageable);

    IngresoDTO crear(Long usuarioId, IngresoRequest request);

    IngresoDTO actualizar(Long usuarioId, Long ingresoId, IngresoRequest request);

    void eliminar(Long usuarioId, Long ingresoId);
}
