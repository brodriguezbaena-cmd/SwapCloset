package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Valoracion;
import es.safareyes.swapcloset.dtos.response.ValoracionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ValoracionMapper {

    @Mapping(source = "solicitud.id", target = "solicitudId")
    @Mapping(source = "autor.id", target = "autorId")
    @Mapping(source = "valorado.id", target = "valoradoId")
    ValoracionResponse toResponse(Valoracion valoracion);
}