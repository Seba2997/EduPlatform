package com.eduplatform.apiReporte.services;

import com.eduplatform.apiReporte.models.entities.Reporte;
import com.eduplatform.apiReporte.models.request.ReporteCrear;
import com.eduplatform.apiReporte.repositories.ReporteRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepo;

    public Reporte crearReporteDesdeApi(ReporteCrear dto) {
        // Simulación de contenido externo si no se envía
        String contenido = dto.getContenido();
        if (contenido == null || contenido.isEmpty()) {
            contenido = """
                {
                    "usuario": "Juan Pérez",
                    "curso": "Spring Boot Avanzado",
                    "notaFinal": 6.7,
                    "fecha": "2025-07-07"
                }
                """;
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

    private String generarPdfDesdeTexto(String titulo, String contenido) {
        String nombreArchivo = "reporte_" + UUID.randomUUID() + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(nombreArchivo);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Título: " + titulo).setBold());
            document.add(new Paragraph("\nReporte de Contenido:"));
            document.add(new Paragraph(contenido));

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return nombreArchivo;
    }
}