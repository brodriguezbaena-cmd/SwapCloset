package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.dtos.response.SolicitudResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    @Mapping(source = "tipoSolicitud", target = "tipo")
    @Mapping(target = "prendaId", ignore = true)
    @Mapping(target = "prendaTitulo", ignore = true)
    @Mapping(target = "prendaOfrecidaId", ignore = true)
    @Mapping(target = "prendaOfrecidaTitulo", ignore = true)
    @Mapping(source = "perfil.id", target = "solicitanteId")
    @Mapping(source = "perfil.nombre", target = "solicitanteNombreUsuario")
    @Mapping(source = "destinatarios", target = "propietariaId", qualifiedByName = "primerDestinatarioId")
    @Mapping(source = "destinatarios", target = "propietariaNombreUsuario", qualifiedByName = "primerDestinatarioNombre")
    SolicitudResponse toResponse(Solicitudes solicitud);

    List<SolicitudResponse> toResponses(List<Solicitudes> solicitudes);

    @Named("primerDestinatarioId")
    default Integer primerDestinatarioId(Set<Perfil> destinatarios) {
        if (destinatarios == null || destinatarios.isEmpty()) return null;
        return destinatarios.iterator().next().getId();
    }

    @Named("primerDestinatarioNombre")
    default String primerDestinatarioNombre(Set<Perfil> destinatarios) {
        if (destinatarios == null || destinatarios.isEmpty()) return null;
        return destinatarios.iterator().next().getNombre();
    }
}