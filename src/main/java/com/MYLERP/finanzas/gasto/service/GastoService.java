package com.MYLERP.finanzas.gasto.service;

import com.MYLERP.finanzas.gasto.dto.GastoDTO;
import com.MYLERP.finanzas.gasto.dto.GastoRequest;
import com.MYLERP.shared.util.PaginacionUtil;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GastoService {

    PaginacionUtil<GastoDTO> listar(Long usuarioId, LocalDate desde, LocalDate hasta, Pageable pageable);

    GastoDTO crear(Long usuarioId, GastoRequest request);

    GastoDTO actualizar(Long usuarioId, Long gastoId, GastoRequest request);

    void eliminar(Long usuarioId, Long gastoId);
}
