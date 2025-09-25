package com.compartetutiempo.backend.dto;

import com.compartetutiempo.backend.model.Conversacion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversacionDTO {
    private Integer id;

    public static ConversacionDTO fromEntity(Conversacion c) {
        return new ConversacionDTO(c.getId());
    }
}

