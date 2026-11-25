package es.safareyes.swapcloset.controller;

import es.safareyes.swapcloset.dtos.response.CategoriaResponse;
import es.safareyes.swapcloset.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // 6. Categorías con número de prendas disponibles (público)
    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }
}
