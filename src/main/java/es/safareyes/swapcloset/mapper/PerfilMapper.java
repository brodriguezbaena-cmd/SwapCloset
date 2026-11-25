package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Estilo;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.dtos.request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dtos.request.RegistroRequest;
import es.safareyes.swapcloset.dtos.response.PerfilPublicoResponse;
import es.safareyes.swapcloset.dtos.response.PrendaTarjetaResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = PrendaMapper.class)
public interface PerfilMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "nombreUsuario", target = "nombre")
    @Mapping(source = "tallaZapatos", target = "talla_zapatos")
    @Mapping(source = "tallaPantalon", target = "talla_pantalon")
    @Mapping(target = "estilos", ignore = true)
    @Mapping(target = "prendas", ignore = true)
    Perfil toEntity(RegistroRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    @Mapping(source = "tallaZapatos", target = "talla_zapatos")
    @Mapping(source = "tallaPantalon", target = "talla_pantalon")
    @Mapping(target = "estilos", ignore = true)
    @Mapping(target = "prendas", ignore = true)
    void actualizar(PerfilActualizarRequest request, @MappingTarget Perfil perfil);

    @Mapping(source = "nombre", target = "nombreUsuario")
    @Mapping(source = "talla_zapatos", target = "tallaZapatos")
    @Mapping(source = "talla_pantalon", target = "tallaPantalon")
    @Mapping(target = "reputacion", ignore = true)
    @Mapping(target = "prendas", ignore = true)
    PerfilPublicoResponse toPublico(Perfil perfil);

    List<PrendaTarjetaResponse> toTarjetas(List<Prenda> prendas);

    default String estiloANombre(Estilo estilo) {
        return estilo == null ? null : estilo.getNombre();
    }
}