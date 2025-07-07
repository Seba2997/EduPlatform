package com.eduplatform.apiReporte.services;

import com.eduplatform.apiReporte.models.entities.Reporte;
import com.eduplatform.apiReporte.models.request.ReporteCrear;
import com.eduplatform.apiReporte.repositories.ReporteRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepo;

    private final WebClient webClient;

    @Value("${reporte.pdf.path:src/main/resources/static/pdfs}")
    private String rutaPdf;

    public ReporteService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build(); // Cambia por la URL base de tu otra API
    }

    public Reporte crearReporteDesdeApi(String endpoint, ReporteCrear dto) {
        // Llamar a la otra API (por ejemplo /api/usuarios)
        Mono<String> respuestaMono = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class);

        String contenidoExterno = respuestaMono.block(); // Esperar respuesta sincrónicamente

        // Crear objeto reporte
        Reporte reporte = new Reporte();
        reporte.setTitulo(dto.getTitulo());
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setContenidoTexto(contenidoExterno);

        if (dto.isExportarPdf()) {
            String nombreArchivo = generarPdfDesdeTexto(dto.getTitulo(), contenidoExterno);
            reporte.setArchivoPdf(nombreArchivo);
        }

        return reporteRepo.save(reporte);
    }

    private String generarPdfDesdeTexto(String titulo, String contenido) {
        try {
            File folder = new File(rutaPdf);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String nombreArchivo = "reporte_" + System.currentTimeMillis() + ".pdf";
            String rutaCompleta = rutaPdf + File.separator + nombreArchivo;

            PdfWriter writer = new PdfWriter(new FileOutputStream(rutaCompleta));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Título: " + titulo));
            document.add(new Paragraph("Contenido:"));
            document.add(new Paragraph(contenido));
            document.close();

            return nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}