package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Mensaje;
import es.safareyes.swapcloset.dtos.response.MensajeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    @Mapping(source = "autor.id", target = "autorId")
    @Mapping(source = "autor.nombreUsuario", target = "autorNombreUsuario")
    @Mapping(target = "enviadoEn", expression = "java(enviadoEn(mensaje))")
    MensajeResponse toResponse(Mensaje mensaje);

    default LocalDateTime enviadoEn(Mensaje mensaje) {
        if (mensaje.getFechaEnvio() == null || mensaje.getHoraEnvio() == null) {
            return null;
        }
        return LocalDateTime.of(mensaje.getFechaEnvio(), mensaje.getHoraEnvio());
    }
}