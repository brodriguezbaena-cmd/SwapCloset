package es.safareyes.swapcloset.mapper;

import es.safareyes.swapcloset.Modelos.Foto;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.dtos.request.PrendaActualizarRequest;
import es.safareyes.swapcloset.dtos.request.PrendaCrearRequest;
import es.safareyes.swapcloset.dtos.response.FotoResponse;
import es.safareyes.swapcloset.dtos.response.PrendaDetalleResponse;
import es.safareyes.swapcloset.dtos.response.PrendaTarjetaResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrendaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "situacion", ignore = true)
    @Mapping(target = "disponibilidad", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    Prenda toEntity(PrendaCrearRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "situacion", ignore = true)
    @Mapping(target = "disponibilidad", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    void actualizar(PrendaActualizarRequest request, @MappingTarget Prenda prenda);

    @Mapping(source = "categoria.nombre", target = "categoria")
    @Mapping(source = "perfil.ciudad", target = "ciudad")
    @Mapping(source = "perfil.id", target = "propietariaId")
    @Mapping(source = "perfil.nombreUsuario", target = "propietariaNombreUsuario")
    @Mapping(source = "fotos", target = "fotoPortadaId", qualifiedByName = "portada")
    PrendaTarjetaResponse toTarjeta(Prenda prenda);

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nombre", target = "categoria")
    @Mapping(target = "propietaria", ignore = true)
    PrendaDetalleResponse toDetalle(Prenda prenda);

    @Mapping(target = "url", expression = "java(\"/api/v1/fotos/\" + foto.getId())")
    FotoResponse toFoto(Foto foto);

    @Named("portada")
    default Integer portada(List<Foto> fotos) {
        return (fotos == null || fotos.isEmpty()) ? null : fotos.get(0).getId();
    }
}