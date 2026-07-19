package com.MYLERP.shared.util;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Envoltorio de respuesta paginada consistente para todos los endpoints de listado.
 * Evita filtrar el objeto Page<T> de Spring Data directamente en la API (expone
 * detalles internos de paginacion que no queremos versionar como contrato publico).
 */
public record PaginacionUtil<T>(
        List<T> contenido,
        int paginaActual,
        int totalPaginas,
        long totalElementos,
        boolean esUltimaPagina
) {
    public static <E, D> PaginacionUtil<D> desde(Page<E> page, Function<E, D> mapeador) {
        return new PaginacionUtil<>(
                page.getContent().stream().map(mapeador).toList(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast()
        );
    }
}
