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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            document.add(new Paragraph("Título: " + titulo).setBold());
            document.add(new Paragraph("\nContenido del reporte:"));
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
            .uri("http://localhost:8083/inscripciones/")
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
            .uri("http://localhost:8083/boletas/todas")
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
    contenidoReporte.append("REPORTE DE INSCRIPCIONES\n\n");
    contenidoReporte.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
    contenidoReporte.append("Total de inscripciones: ").append(totalInscripciones).append("\n");
    contenidoReporte.append("Total recaudado (según boletas): $").append(totalRecaudado).append("\n\n");
    contenidoReporte.append("Detalle de inscripciones:\n");

contenidoReporte.append("REPORTE DE INSCRIPCIONES Y BOLETAS\n\n");
contenidoReporte.append("Fecha de generación: ")
        .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        .append("\n\n");




// Crear un mapa para acceso rápido a las boletas por ID de inscripción
Map<Long, Boleta> boletasPorInscripcion = new HashMap<>();
for (Boleta boleta : boletas) {
    boletasPorInscripcion.put(Long.valueOf(boleta.getId()), boleta); // conversión explícita
}

// Recorrer inscripciones y vincular con su boleta
for (Inscripcion inscripcion : inscripciones) {
    contenidoReporte.append("INSCRIPCIÓN:\n");
    contenidoReporte.append("- Estudiante: ").append(inscripcion.getNombreEstudiante())
            .append(" | Email: ").append(inscripcion.getEmailEstudiante())
            .append(" | Curso: ").append(inscripcion.getNombreCurso())
            .append(" | Fecha de Inscripción: ").append(inscripcion.getFechaInscripcion())
            .append("\n");

    Boleta boleta = boletasPorInscripcion.get(Long.valueOf(inscripcion.getIdInscripcion())); // conversión explícita
    if (boleta != null) {
        contenidoReporte.append("- N° Boleta: ").append(boleta.getNumeroBoleta())
                .append(" | Precio: $").append(boleta.getPrecio())
                .append(" | Fecha Compra: ").append(boleta.getFechaCompra());
    } else {
        contenidoReporte.append("- Boleta: No disponible");
    }

    contenidoReporte.append("\n\n");
}

    // Generar PDF
    String nombreArchivo = generarPdfDesdeTexto("Reporte Inscripciones", contenidoReporte.toString());

    // Guardar en BD
    Reporte reporte = new Reporte();
    reporte.setTitulo("Reporte de Inscripciones");
    reporte.setFechaGeneracion(LocalDateTime.now());
    reporte.setContenidoTexto(contenidoReporte.toString());
    reporte.setArchivoPdf(nombreArchivo);

    return reporteRepo.save(reporte);
}

}

