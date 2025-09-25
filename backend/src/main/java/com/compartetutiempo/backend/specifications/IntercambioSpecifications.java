package com.compartetutiempo.backend.specifications;

import com.compartetutiempo.backend.model.Categoria;
import com.compartetutiempo.backend.model.Intercambio;
import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import java.util.List;

public final class IntercambioSpecifications {

    private IntercambioSpecifications() {}

    public static Specification<Intercambio> conTipo(TipoIntercambio tipo) {
        return (root, query, cb) ->
                tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<Intercambio> conModalidad(ModalidadServicio modalidad) {
        return (root, query, cb) ->
                modalidad == null ? null : cb.equal(root.get("modalidad"), modalidad);
    }

    public static Specification<Intercambio> conCategoriaIds(List<Long> categoriaIds) {
        return (root, query, cb) -> {
            if (categoriaIds == null || categoriaIds.isEmpty()) return null;
            // Evita duplicados por el join
            query.distinct(true);
            Join<Intercambio, Categoria> categorias = root.join("categorias");
            return categorias.get("id").in(categoriaIds);
        };
    }

    public static Specification<Intercambio> conHorasMin(Double minHoras) {
        return (root, query, cb) ->
                minHoras == null ? null : cb.greaterThanOrEqualTo(root.get("numeroHoras"), minHoras);
    }

    public static Specification<Intercambio> conHorasMax(Double maxHoras) {
        return (root, query, cb) ->
                maxHoras == null ? null : cb.lessThanOrEqualTo(root.get("numeroHoras"), maxHoras);
    }

    public static Specification<Intercambio> conTexto(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nombre")), like),
                    cb.like(cb.lower(root.get("descripcion")), like)
            );
        };
    }
}
