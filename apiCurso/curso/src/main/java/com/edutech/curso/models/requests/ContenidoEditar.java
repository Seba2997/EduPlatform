package com.edutech.curso.models.requests;

import lombok.Data;

@Data
public class ContenidoEditar {
    
    private int idContenido;
    private Integer  numeroUnidad;
    private String tituloUnidad;
    private Integer  numeroContenido;
    private String contenido;
}
