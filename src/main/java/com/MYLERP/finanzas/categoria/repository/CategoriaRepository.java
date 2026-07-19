package com.MYLERP.finanzas.categoria.repository;

import com.MYLERP.finanzas.categoria.model.Categoria;
import com.MYLERP.finanzas.categoria.model.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Trae las del sistema (usuario_id NULL) + las propias del usuario, filtradas por tipo de uso.
    @Query("""
        SELECT c FROM Categoria c
        WHERE (c.usuarioId IS NULL OR c.usuarioId = :usuarioId)
          AND c.tipo IN (:tipo, com.MYLERP.finanzas.categoria.model.TipoCategoria.AMBOS)
        ORDER BY c.nombre
        """)
    List<Categoria> buscarDisponiblesParaUsuario(@Param("usuarioId") Long usuarioId, @Param("tipo") TipoCategoria tipo);

    // Valida que la categoria exista Y sea usable por ese usuario (del sistema o propia).
    @Query("""
        SELECT c FROM Categoria c
        WHERE c.id = :categoriaId
          AND (c.usuarioId IS NULL OR c.usuarioId = :usuarioId)
        """)
    Optional<Categoria> buscarValidaParaUsuario(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId);
}
