package es.safareyes.swapcloset.dtos;

/** Proyección por interfaz de la consulta nativa del informe de actividad. */
public interface ActividadView {
    String getCategoria();

    String getMes();

    Long getPublicaciones();

    Long getIntercambios();

    Long getPrestamos();
}
