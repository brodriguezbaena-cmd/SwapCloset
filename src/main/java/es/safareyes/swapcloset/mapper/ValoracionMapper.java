package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Valoracion;
import es.safareyes.swapcloset.dtos.response.ValoracionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ValoracionMapper {

    @Mapping(source = "solicitudes.id", target = "solicitudId")
    @Mapping(source = "perfil.id", target = "autorId")
    @Mapping(target = "valoradoId", ignore = true)
    ValoracionResponse toResponse(Valoracion valoracion);
}