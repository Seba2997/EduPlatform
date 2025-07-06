package com.eduplatform.apiSoporte.models.request;

import com.eduplatform.apiSoporte.models.EstadoTicket;

public class EstadoRequest {
    private EstadoTicket nuevoEstado;

    public EstadoTicket getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(EstadoTicket nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
}
