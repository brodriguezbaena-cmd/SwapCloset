package es.safareyes.swapcloset.controller;

import es.safareyes.swapcloset.dtos.response.ActividadCategoriaResponse;
import es.safareyes.swapcloset.service.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;

    // 16. Publicaciones, intercambios y préstamos por categoría y mes (fechas ISO: 2026-01-31)
    @GetMapping("/actividad")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")
    public List<ActividadCategoriaResponse> actividad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return informeService.actividad(desde, hasta);
    }
}
