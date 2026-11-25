package es.safareyes.swapcloset.service;

import es.safareyes.swapcloset.dtos.response.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResponse> listar();
}
