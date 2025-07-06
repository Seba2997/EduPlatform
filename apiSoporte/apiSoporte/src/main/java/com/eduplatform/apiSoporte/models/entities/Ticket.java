package com.eduplatform.apiSoporte.models.entities;

import java.util.Date;

import com.eduplatform.apiSoporte.models.EstadoTicket;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String asunto;

    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoTicket estado;
    private String emailUsuario;

    private String respuesta;
    
    private String respondidoPor;
    
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = new Date();
        this.estado = EstadoTicket.ABIERTO;;
    }

}
