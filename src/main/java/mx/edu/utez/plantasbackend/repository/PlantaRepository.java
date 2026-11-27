package mx.edu.utez.plantasbackend.repository;

import mx.edu.utez.plantasbackend.model.Planta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlantaRepository extends JpaRepository<Planta, Long> {
    List<Planta> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);
}