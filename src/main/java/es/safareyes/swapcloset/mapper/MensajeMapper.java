package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Mensaje;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.dtos.response.MensajeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    @Mapping(source = "remitentes", target = "autorId", qualifiedByName = "primerRemitenteId")
    @Mapping(source = "remitentes", target = "autorNombreUsuario", qualifiedByName = "primerRemitenteNombre")
    @Mapping(target = "enviadoEn", expression = "java(enviadoEn(mensaje))")
    MensajeResponse toResponse(Mensaje mensaje);

    @Named("primerRemitenteId")
    default Integer primerRemitenteId(Set<Perfil> remitentes) {
        if (remitentes == null || remitentes.isEmpty()) return null;
        return remitentes.iterator().next().getId();
    }

    @Named("primerRemitenteNombre")
    default String primerRemitenteNombre(Set<Perfil> remitentes) {
        if (remitentes == null || remitentes.isEmpty()) return null;
        return remitentes.iterator().next().getNombre();
    }

    default LocalDateTime enviadoEn(Mensaje mensaje) {
        if (mensaje.getFechaEnvio() == null || mensaje.getHoraEnvio() == null) {
            return null;
        }
        return LocalDateTime.of(mensaje.getFechaEnvio(), mensaje.getHoraEnvio());
    }
}