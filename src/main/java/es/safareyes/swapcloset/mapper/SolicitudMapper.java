package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.dtos.response.SolicitudResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    @Mapping(source = "tipoSolicitud", target = "tipo")
    @Mapping(source = "prenda.id", target = "prendaId")
    @Mapping(source = "prenda.titulo", target = "prendaTitulo")
    @Mapping(source = "prendaOfrecida.id", target = "prendaOfrecidaId")
    @Mapping(source = "prendaOfrecida.titulo", target = "prendaOfrecidaTitulo")
    @Mapping(source = "solicitante.id", target = "solicitanteId")
    @Mapping(source = "solicitante.nombreUsuario", target = "solicitanteNombreUsuario")
    @Mapping(source = "destinatario.id", target = "propietariaId")
    @Mapping(source = "destinatario.nombreUsuario", target = "propietariaNombreUsuario")
    SolicitudResponse toResponse(Solicitudes solicitud);

    List<SolicitudResponse> toResponses(List<Solicitudes> solicitudes);
}