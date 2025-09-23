package com.compartetutiempo.backend.dto;

import java.util.List;

import com.compartetutiempo.backend.model.enums.ModalidadServicio;
import com.compartetutiempo.backend.model.enums.TipoIntercambio;

import lombok.Data;

@Data
public class IntercambioDTO {
    private String nombre;
    private String descripcion;
    private Double numeroHoras;
    private TipoIntercambio tipo;
    private ModalidadServicio modalidad;
    private List<Long> categorias; // 👈 solo los ids
}
