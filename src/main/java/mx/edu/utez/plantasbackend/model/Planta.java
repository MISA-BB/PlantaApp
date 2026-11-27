package mx.edu.utez.plantasbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plantas")
public class Planta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String nombreComun;

    private String nombreCientifico;

    private String categoria;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 1000)
    private String caracteristicasPrincipales;

    private Integer frecuenciaRiegoDias;
    private String frecuenciaRiegoTexto;
    private String cantidadAgua;
    private String necesidadLuz;
    private String tipoIluminacion;
    private String temperatura;
    private String humedad;
    private String tipoSuelo;

    @Column(length = 1500)
    private String recomendacionesGenerales;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imagenBase64;

    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoRiego;
    private LocalDateTime proximoRiego;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.ultimoRiego == null) {
            this.ultimoRiego = LocalDateTime.now();
        }
        int dias = (this.frecuenciaRiegoDias != null && this.frecuenciaRiegoDias > 0) ? this.frecuenciaRiegoDias : 7;
        this.proximoRiego = this.ultimoRiego.plusDays(dias);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreComun() { return nombreComun; }
    public void setNombreComun(String nombreComun) { this.nombreComun = nombreComun; }

    public String getNombreCientifico() { return nombreCientifico; }
    public void setNombreCientifico(String nombreCientifico) { this.nombreCientifico = nombreCientifico; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCaracteristicasPrincipales() { return caracteristicasPrincipales; }
    public void setCaracteristicasPrincipales(String caracteristicasPrincipales) { this.caracteristicasPrincipales = caracteristicasPrincipales; }

    public Integer getFrecuenciaRiegoDias() { return frecuenciaRiegoDias; }
    public void setFrecuenciaRiegoDias(Integer frecuenciaRiegoDias) { this.frecuenciaRiegoDias = frecuenciaRiegoDias; }

    public String getFrecuenciaRiegoTexto() { return frecuenciaRiegoTexto; }
    public void setFrecuenciaRiegoTexto(String frecuenciaRiegoTexto) { this.frecuenciaRiegoTexto = frecuenciaRiegoTexto; }

    public String getCantidadAgua() { return cantidadAgua; }
    public void setCantidadAgua(String cantidadAgua) { this.cantidadAgua = cantidadAgua; }

    public String getNecesidadLuz() { return necesidadLuz; }
    public void setNecesidadLuz(String necesidadLuz) { this.necesidadLuz = necesidadLuz; }

    public String getTipoIluminacion() { return tipoIluminacion; }
    public void setTipoIluminacion(String tipoIluminacion) { this.tipoIluminacion = tipoIluminacion; }

    public String getTemperatura() { return temperatura; }
    public void setTemperatura(String temperatura) { this.temperatura = temperatura; }

    public String getHumedad() { return humedad; }
    public void setHumedad(String humedad) { this.humedad = humedad; }

    public String getTipoSuelo() { return tipoSuelo; }
    public void setTipoSuelo(String tipoSuelo) { this.tipoSuelo = tipoSuelo; }

    public String getRecomendacionesGenerales() { return recomendacionesGenerales; }
    public void setRecomendacionesGenerales(String recomendacionesGenerales) { this.recomendacionesGenerales = recomendacionesGenerales; }

    public String getImagenBase64() { return imagenBase64; }
    public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimoRiego() { return ultimoRiego; }
    public void setUltimoRiego(LocalDateTime ultimoRiego) { this.ultimoRiego = ultimoRiego; }

    public LocalDateTime getProximoRiego() { return proximoRiego; }
    public void setProximoRiego(LocalDateTime proximoRiego) { this.proximoRiego = proximoRiego; }
}