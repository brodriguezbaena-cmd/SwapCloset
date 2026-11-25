package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.dtos.response.ReputacionResponse;
import es.safareyes.swapcloset.repositorios.IValoracionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** RN-09: la reputación es la media con un decimal junto al número de valoraciones. */
@Component
@RequiredArgsConstructor
class Reputaciones {

    private final IValoracionRepositorio valoracionRepositorio;

    ReputacionResponse de(Integer perfilId) {
        ReputacionResponse bruta = valoracionRepositorio.reputacionDe(perfilId);
        return bruta == null ? redondear(null, 0L) : redondear(bruta.media(), bruta.total());
    }

    static ReputacionResponse redondear(Double media, Long total) {
        Double redondeada = media == null ? null : Math.round(media * 10.0) / 10.0;
        return new ReputacionResponse(redondeada, total == null ? 0L : total);
    }
}
