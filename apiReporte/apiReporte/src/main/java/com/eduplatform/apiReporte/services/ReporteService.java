package com.eduplatform.apiReporte.services;

import com.eduplatform.apiReporte.models.entities.Reporte;
import com.eduplatform.apiReporte.models.request.ReporteCrear;
import com.eduplatform.apiReporte.repositories.ReporteRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

import java.util.UUID;

import com.eduplatform.apiReporte.models.external.Boleta;
import com.eduplatform.apiReporte.models.external.Inscripcion;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepo;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private WebClient webClientConToken() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return webClientBuilder
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }


    //Funcion 1: crearReporteDesdeApi
    public Reporte crearReporteDesdeApi(ReporteCrear dto) {
        String contenido = dto.getContenido();

        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del reporte no puede estar vacío");
        }

        Reporte reporte = new Reporte();
        reporte.setTitulo(dto.getTitulo());
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setContenidoTexto(contenido);

        if (dto.isExportarPdf()) {
            String nombreArchivo = generarPdfDesdeTexto(dto.getTitulo(), contenido);
            reporte.setArchivoPdf(nombreArchivo);
        }

        return reporteRepo.save(reporte);
    }

    //Funcion 2: generarPdfDesdeTexto
        private String generarPdfDesdeTexto(String titulo, String contenido) {
        String carpeta = "reportes";
        File directorio = new File(carpeta);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String nombreArchivo = carpeta + "/reporte_" + UUID.randomUUID() + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(nombreArchivo);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Edutech: " + titulo).setBold());
            document.add(new Paragraph("\nReporte de inscripciones y boletas:\n"));
            document.add(new Paragraph(contenido));

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return nombreArchivo;
    }

    //Funcion 3: generarReporteInscripciones
    public Reporte generarReporteInscripciones() {
    // Traer inscripciones
    List<Inscripcion> inscripciones = webClientConToken()
            .get()
            .uri("http://localhost:8083/inscripciones/reporte")
            .retrieve()
            .bodyToFlux(Inscripcion.class)
            .collectList()
            .block();

        
    // Validación
    if (inscripciones == null || inscripciones.isEmpty()) {
        throw new RuntimeException("No se encontraron inscripciones para el reporte.");
    }

    int totalInscripciones = inscripciones.size();

    // Traer boletas
    List<Boleta> boletas = webClientConToken()
            .get()
            .uri("http://localhost:8083/boletas/reporte")
            .retrieve()
            .bodyToFlux(Boleta.class)
            .collectList()
            .block();

    int totalRecaudado = 0;
    if (boletas != null && !boletas.isEmpty()) {
        totalRecaudado = boletas.stream().mapToInt(Boleta::getPrecio).sum();
    }

    // Armar contenido
    StringBuilder contenidoReporte = new StringBuilder();
    contenidoReporte.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
    contenidoReporte.append("Total de inscripciones: ").append(totalInscripciones).append("\n");
    contenidoReporte.append("Total recaudado: $").append(totalRecaudado).append("\n\n");
    contenidoReporte.append("Detalle de inscripciones:\n");

contenidoReporte.append("REPORTE DE INSCRIPCIONES Y BOLETAS\n\n");
contenidoReporte.append("Fecha de generación: ")
        .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        .append("\n\n");




// Recorrer inscripciones y vincular con su boleta
for (Inscripcion inscripcion : inscripciones) {
        contenidoReporte.append("INSCRIPCIÓN ").append(inscripcion.getId()).append(":\n")
        .append("Estudiante: ").append(inscripcion.getNombreEstudiante()).append("\n")
        .append("Email: ").append(inscripcion.getEmailEstudiante()).append("\n")
        .append("Curso: ").append(inscripcion.getNombreCurso()).append("\n")
        .append("Fecha de Inscripción: ").append(inscripcion.getFechaInscripcion()).append("\n");

    if (boletas != null) {
        for (Boleta boleta : boletas) {
            if (boleta.getInscripcionId() == inscripcion.getId()) {
                    contenidoReporte.append("N° Boleta: ").append(boleta.getNumeroBoleta()).append("\n")
                    .append("Precio: $").append(boleta.getPrecio()).append("\n")
                    .append("Fecha Compra: ").append(boleta.getFechaCompra()).append("\n");
                    
            }
        }
    }

    contenidoReporte.append("\n");
}

    // Generar PDF
    String nombreArchivo = generarPdfDesdeTexto("Reportes", contenidoReporte.toString());

    // Guardar en BD
    Reporte reporte = new Reporte();
    reporte.setTitulo("Reporte de Inscripciones");
    reporte.setFechaGeneracion(LocalDateTime.now());
    reporte.setContenidoTexto(contenidoReporte.toString());
    reporte.setArchivoPdf(nombreArchivo);

    return reporteRepo.save(reporte);
}

}


