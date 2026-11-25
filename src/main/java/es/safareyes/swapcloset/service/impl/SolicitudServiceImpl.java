package es.safareyes.swapcloset.service.impl;

import es.safareyes.swapcloset.Modelos.EstadoPrenda;
import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import es.safareyes.swapcloset.dtos.request.Bandeja;
import es.safareyes.swapcloset.dtos.request.SolicitudCrearRequest;
import es.safareyes.swapcloset.dtos.response.SolicitudResponse;
import es.safareyes.swapcloset.exception.ConflictoException;
import es.safareyes.swapcloset.exception.PermisoDenegadoException;
import es.safareyes.swapcloset.exception.RecursoNoEncontradoException;
import es.safareyes.swapcloset.exception.ReglaNegocioException;
import es.safareyes.swapcloset.mapper.SolicitudMapper;
import es.safareyes.swapcloset.repositorios.IPerfilRepositorio;
import es.safareyes.swapcloset.repositorios.IPrendaRepositorio;
import es.safareyes.swapcloset.repositorios.ISolicitudRepositorio;
import es.safareyes.swapcloset.security.UsuarioAutenticado;
import es.safareyes.swapcloset.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private static final long MAX_DIAS_PRESTAMO = 30;
    private static final long MAX_PRESTAMOS_ACTIVOS = 3;
    // Préstamos "a la vez": ya aceptados y aún sin devolver
    private static final Set<EstadoSolicitud> PRESTAMO_ACTIVO = Set.of(EstadoSolicitud.ACEPTADA, EstadoSolicitud.RETRASADA);

    private final ISolicitudRepositorio solicitudRepositorio;
    private final IPrendaRepositorio prendaRepositorio;
    private final IPerfilRepositorio perfilRepositorio;
    private final AccesoSolicitudes accesoSolicitudes;
    private final SolicitudMapper solicitudMapper;
    private final Clock clock;

    // ------------------------------------------------------------------ endpoint 10

    @Override
    @Transactional
    public SolicitudResponse crear(SolicitudCrearRequest request, UsuarioAutenticado actual) {
        Integer yo = actual.getPerfilId();
        LocalDate hoy = LocalDate.now(clock);

        Prenda prenda = prendaRepositorio.findById(request.prendaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la prenda con id " + request.prendaId()));

        // RN-04
        if (prenda.getPerfil().getId().equals(yo)) {
            throw new ReglaNegocioException("No puedes solicitar una prenda tuya");
        }
        if (prenda.getSituacion() != EstadoPrenda.DISPONIBLE) {
            throw new ReglaNegocioException("La prenda no está disponible");
        }
        boolean admiteTipo = request.tipo() == TipoSolicitud.INTERCAMBIO
                ? Boolean.TRUE.equals(prenda.getPermiteIntercambio())
                : Boolean.TRUE.equals(prenda.getPermitePrestamo());
        if (!admiteTipo) {
            throw new ReglaNegocioException("La prenda no admite " + request.tipo().name().toLowerCase());
        }
        if (solicitudRepositorio.existsBySolicitanteIdAndPrendaIdAndEstado(yo, prenda.getId(), EstadoSolicitud.PENDIENTE)) {
            throw new ConflictoException("Ya tienes una solicitud pendiente para esta prenda");
        }

        Solicitudes solicitud = new Solicitudes();
        solicitud.setTipoSolicitud(request.tipo());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaCreacion(hoy);
        solicitud.setSolicitante(perfilRepositorio.getReferenceById(yo));
        solicitud.setPrenda(prenda);
        solicitud.getDestinatarios().add(prenda.getPerfil());

        // RN-05
        if (request.tipo() == TipoSolicitud.INTERCAMBIO) {
            solicitud.setPrendaOfrecida(validarPrendaOfrecida(request.prendaOfrecidaId(), prenda, yo));
        } else {
            validarPrestamo(request, yo, hoy);
            solicitud.setFechaInicio(request.fechaInicio());
            solicitud.setFechaFin(request.fechaFin());
        }

        solicitudRepositorio.save(solicitud);
        log.info("Solicitud {} ({}) creada por el perfil {} sobre la prenda {}",
                solicitud.getId(), request.tipo(), yo, prenda.getId());
        return solicitudMapper.toResponse(solicitud);
    }

    private Prenda validarPrendaOfrecida(Integer ofrecidaId, Prenda solicitada, Integer yo) {
        if (ofrecidaId == null) {
            throw new ReglaNegocioException("En un intercambio debes ofrecer una prenda tuya");
        }
        Prenda ofrecida = prendaRepositorio.findById(ofrecidaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la prenda con id " + ofrecidaId));
        if (ofrecida.getId().equals(solicitada.getId())) {
            throw new ReglaNegocioException("No puedes ofrecer la misma prenda que solicitas");
        }
        if (!ofrecida.getPerfil().getId().equals(yo)) {
            throw new ReglaNegocioException("La prenda ofrecida debe ser tuya");
        }
        if (ofrecida.getSituacion() != EstadoPrenda.DISPONIBLE) {
            throw new ReglaNegocioException("La prenda que ofreces no está disponible");
        }
        return ofrecida;
    }

    private void validarPrestamo(SolicitudCrearRequest request, Integer yo, LocalDate hoy) {
        LocalDate inicio = request.fechaInicio();
        LocalDate fin = request.fechaFin();
        if (inicio == null || fin == null) {
            throw new ReglaNegocioException("En un préstamo debes indicar fecha de inicio y de fin");
        }
        if (inicio.isBefore(hoy)) {
            throw new ReglaNegocioException("El préstamo no puede empezar antes de hoy");
        }
        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException("La fecha de fin debe ser posterior a la de inicio");
        }
        if (ChronoUnit.DAYS.between(inicio, fin) > MAX_DIAS_PRESTAMO) {
            throw new ReglaNegocioException("Un préstamo no puede durar más de " + MAX_DIAS_PRESTAMO + " días");
        }
        if (prestamosActivos(yo) >= MAX_PRESTAMOS_ACTIVOS) {
            throw new ReglaNegocioException("No puedes tener más de " + MAX_PRESTAMOS_ACTIVOS + " préstamos activos a la vez");
        }
    }

    private long prestamosActivos(Integer perfilId) {
        return solicitudRepositorio.countBySolicitanteIdAndTipoSolicitudAndEstadoIn(
                perfilId, TipoSolicitud.PRESTAMO, PRESTAMO_ACTIVO);
    }

    // ------------------------------------------------------------------ endpoint 11

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listar(Bandeja bandeja, EstadoSolicitud estado, UsuarioAutenticado actual) {
        Integer yo = actual.getPerfilId();
        List<Solicitudes> solicitudes;
        if (bandeja == Bandeja.RECIBIDAS) {
            solicitudes = estado == null
                    ? solicitudRepositorio.buscarRecibidas(yo)
                    : solicitudRepositorio.buscarRecibidasPorEstado(yo, estado);
        } else {
            solicitudes = estado == null
                    ? solicitudRepositorio.findBySolicitanteIdOrderByFechaCreacionDescIdDesc(yo)
                    : solicitudRepositorio.findBySolicitanteIdAndEstadoOrderByFechaCreacionDescIdDesc(yo, estado);
        }
        return solicitudMapper.toResponses(solicitudes);
    }

    // ------------------------------------------------------------------ endpoint 12

    @Override
    @Transactional
    public SolicitudResponse cambiarEstado(Integer solicitudId, EstadoSolicitud nuevoEstado, UsuarioAutenticado actual) {
        Integer yo = actual.getPerfilId();
        Solicitudes solicitud = accesoSolicitudes.cargarComoParticipante(solicitudId, yo); // 404 / 403
        EstadoSolicitud origen = solicitud.getEstado();
        LocalDate hoy = LocalDate.now(clock);

        // RN-06: quién puede hacer cada transición (403) y desde qué estado (422)
        switch (nuevoEstado) {
            case ACEPTADA, RECHAZADA -> {
                exigirDestinataria(solicitud, yo, "Solo la propietaria puede aceptar o rechazar");
                exigirOrigen(origen, nuevoEstado, EstadoSolicitud.PENDIENTE);
            }
            case CANCELADA -> {
                if (!solicitud.esSolicitante(yo)) {
                    throw new PermisoDenegadoException("Solo quien solicita puede cancelar");
                }
                exigirOrigen(origen, nuevoEstado, EstadoSolicitud.PENDIENTE);
            }
            case FINALIZADA -> {
                exigirDestinataria(solicitud, yo, "Solo la propietaria puede finalizar");
                exigirOrigen(origen, nuevoEstado, EstadoSolicitud.ACEPTADA, EstadoSolicitud.RETRASADA);
            }
            default -> throw new ReglaNegocioException(
                    "El estado " + nuevoEstado + " no se puede establecer manualmente");
        }

        List<Integer> prendasAfectadas = new ArrayList<>();
        switch (nuevoEstado) {
            case ACEPTADA -> {
                aceptar(solicitud);
                prendasAfectadas.add(solicitud.getPrenda().getId());
                if (solicitud.getPrendaOfrecida() != null) {
                    prendasAfectadas.add(solicitud.getPrendaOfrecida().getId());
                }
                solicitud.setFechaRespuesta(hoy);
            }
            case RECHAZADA, CANCELADA -> solicitud.setFechaRespuesta(hoy);
            case FINALIZADA -> finalizar(solicitud);
            default -> { /* ya descartado arriba */ }
        }
        solicitud.setEstado(nuevoEstado);
        solicitudRepositorio.saveAndFlush(solicitud);

        // La respuesta se monta ANTES del UPDATE masivo, que limpia el contexto de persistencia
        SolicitudResponse respuesta = solicitudMapper.toResponse(solicitud);

        if (nuevoEstado == EstadoSolicitud.ACEPTADA) {
            // RN-07: las demás solicitudes pendientes sobre esas prendas se rechazan
            int rechazadas = solicitudRepositorio.rechazarPendientesDePrendas(solicitudId, prendasAfectadas, hoy);
            log.info("Solicitud {} aceptada: {} solicitudes pendientes rechazadas", solicitudId, rechazadas);
        } else {
            log.info("Solicitud {}: {} -> {} por el perfil {}", solicitudId, origen, nuevoEstado, yo);
        }
        return respuesta;
    }

    private void exigirDestinataria(Solicitudes solicitud, Integer yo, String mensaje) {
        if (!solicitud.esDestinatario(yo)) {
            throw new PermisoDenegadoException(mensaje);
        }
    }

    private void exigirOrigen(EstadoSolicitud actual, EstadoSolicitud destino, EstadoSolicitud... validos) {
        for (EstadoSolicitud valido : validos) {
            if (actual == valido) {
                return;
            }
        }
        throw new ReglaNegocioException("No se puede pasar de " + actual + " a " + destino);
    }

    /** RN-07: la prenda queda RESERVADA (intercambio) o PRESTADA (préstamo). */
    private void aceptar(Solicitudes solicitud) {
        Prenda prenda = solicitud.getPrenda();
        if (prenda.getSituacion() != EstadoPrenda.DISPONIBLE) {
            throw new ReglaNegocioException("La prenda ya no está disponible");
        }
        if (solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO) {
            Prenda ofrecida = solicitud.getPrendaOfrecida();
            if (ofrecida == null || ofrecida.getSituacion() != EstadoPrenda.DISPONIBLE) {
                throw new ReglaNegocioException("La prenda ofrecida ya no está disponible");
            }
            prenda.setSituacion(EstadoPrenda.RESERVADA);
            ofrecida.setSituacion(EstadoPrenda.RESERVADA);
        } else {
            // El límite de 3 se vuelve a comprobar: pudo alcanzarse mientras esta solicitud esperaba
            if (prestamosActivos(solicitud.getSolicitante().getId()) >= MAX_PRESTAMOS_ACTIVOS) {
                throw new ReglaNegocioException("Quien solicita ya tiene " + MAX_PRESTAMOS_ACTIVOS + " préstamos activos");
            }
            prenda.setSituacion(EstadoPrenda.PRESTADA);
        }
    }

    /** RN-07: intercambio -> las prendas cambian de propietaria; en ambos casos vuelven a DISPONIBLE. */
    private void finalizar(Solicitudes solicitud) {
        Prenda prenda = solicitud.getPrenda();
        if (solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO) {
            Prenda ofrecida = solicitud.getPrendaOfrecida();
            Perfil duenaOriginal = solicitud.getDestinatario();
            prenda.setPerfil(solicitud.getSolicitante());
            ofrecida.setPerfil(duenaOriginal);
            liberar(ofrecida);
        }
        liberar(prenda);
    }

    // Solo se libera lo que estaba reservado o prestado (una prenda retirada entretanto sigue retirada)
    private void liberar(Prenda prenda) {
        if (prenda.getSituacion() == EstadoPrenda.RESERVADA || prenda.getSituacion() == EstadoPrenda.PRESTADA) {
            prenda.setSituacion(EstadoPrenda.DISPONIBLE);
        }
    }
}
