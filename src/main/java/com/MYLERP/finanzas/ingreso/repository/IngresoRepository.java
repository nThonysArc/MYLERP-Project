package com.MYLERP.finanzas.ingreso.repository;

import com.MYLERP.finanzas.ingreso.model.Ingreso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    Page<Ingreso> findByUsuarioIdAndDeletedAtIsNull(Long usuarioId, Pageable pageable);

    Page<Ingreso> findByUsuarioIdAndFechaBetweenAndDeletedAtIsNull(
            Long usuarioId, LocalDate desde, LocalDate hasta, Pageable pageable
    );

    Optional<Ingreso> findByIdAndUsuarioIdAndDeletedAtIsNull(Long id, Long usuarioId);
}
