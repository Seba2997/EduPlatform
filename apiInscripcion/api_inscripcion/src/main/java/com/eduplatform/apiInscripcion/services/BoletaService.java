package com.eduplatform.apiInscripcion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduplatform.apiInscripcion.models.entities.Boleta;
import com.eduplatform.apiInscripcion.repositories.BoletaRepository;

@Service
public class BoletaService {
    
    @Autowired
    private BoletaRepository boletaRepo;

    public Boleta obtenerBoletasPorInscripcionId(int inscripcionId) {
        return boletaRepo.findByInscripcionId(inscripcionId);
    }

    public List<Boleta> obtenerTodasBoletas() {
        return boletaRepo.findAll();
    }
}
