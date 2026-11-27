package mx.edu.utez.plantasbackend.repository;

import mx.edu.utez.plantasbackend.model.Riego;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RiegoRepository extends JpaRepository<Riego, Long> {
    List<Riego> findByUsuarioIdOrderByFechaRiegoDesc(Long usuarioId);
    List<Riego> findByPlantaIdOrderByFechaRiegoDesc(Long plantaId);
}