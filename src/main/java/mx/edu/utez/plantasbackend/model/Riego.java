package mx.edu.utez.plantasbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "riegos")
public class Riego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plantaId;

    @Column(nullable = false)
    private Long usuarioId;

    private String nombrePlanta;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imagenPlantaBase64;

    private LocalDateTime fechaRiego;

    @PrePersist
    public void prePersist() {
        if (this.fechaRiego == null) {
            this.fechaRiego = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlantaId() { return plantaId; }
    public void setPlantaId(Long plantaId) { this.plantaId = plantaId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombrePlanta() { return nombrePlanta; }
    public void setNombrePlanta(String nombrePlanta) { this.nombrePlanta = nombrePlanta; }

    public String getImagenPlantaBase64() { return imagenPlantaBase64; }
    public void setImagenPlantaBase64(String imagenPlantaBase64) { this.imagenPlantaBase64 = imagenPlantaBase64; }

    public LocalDateTime getFechaRiego() { return fechaRiego; }
    public void setFechaRiego(LocalDateTime fechaRiego) { this.fechaRiego = fechaRiego; }
}