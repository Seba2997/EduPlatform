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

    public Reporte generarReporteInscripciones() {
        List<Inscripcion> inscripciones = webClientConToken()
                .get()
                .uri("http://localhost:8083/inscripciones") // Ajusta el puerto si es distinto
                .retrieve()
                .bodyToFlux(Inscripcion.class)
                .collectList()
                .block();

        if (inscripciones == null || inscripciones.isEmpty()) {
            throw new RuntimeException("No se encontraron inscripciones para el reporte.");
        }

        int totalInscripciones = inscripciones.size();
        int totalRecaudado = inscripciones.stream()
                .mapToInt(i -> i.getCurso().getPrecio())
                .sum();

        StringBuilder contenido = new StringBuilder();
        contenido.append("REPORTE DE INSCRIPCIONES\n\n");
        contenido.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        contenido.append("Total de inscripciones: ").append(totalInscripciones).append("\n");
        contenido.append("Total recaudado: $").append(totalRecaudado).append("\n\n");
        contenido.append("Detalle de inscripciones:\n");

        for (Inscripcion insc : inscripciones) {
            contenido.append("- Estudiante: ").append(insc.getNombreEstudiante())
                    .append(" | Email: ").append(insc.getEmailEstudiante())
                    .append(" | Curso: ").append(insc.getNombreCurso())
                    .append(" | Fecha de Inscripcion: ").append(insc.getFechaInscripcion())
                    .append("\n");
        }

        String nombreArchivo = generarPdfDesdeTexto("Reporte Inscripciones", contenido.toString());

        Reporte reporte = new Reporte();
        reporte.setTitulo("Reporte de Inscripciones");
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setContenidoTexto(contenido.toString());
        reporte.setArchivoPdf(nombreArchivo);

        return reporteRepo.save(reporte);
    }

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
}