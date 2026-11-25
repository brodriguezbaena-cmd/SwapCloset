package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.dtos.response.ActividadCategoriaResponse;

import java.time.LocalDate;
import java.util.List;

public interface InformeService {

    List<ActividadCategoriaResponse> actividad(LocalDate desde, LocalDate hasta);
}
