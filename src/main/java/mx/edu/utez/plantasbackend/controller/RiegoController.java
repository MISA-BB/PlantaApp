package mx.edu.utez.plantasbackend.controller;

import mx.edu.utez.plantasbackend.model.Riego;
import mx.edu.utez.plantasbackend.repository.RiegoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/riegos")
@CrossOrigin(origins = "*")
public class RiegoController {

    private final RiegoRepository riegoRepository;

    public RiegoController(RiegoRepository riegoRepository) {
        this.riegoRepository = riegoRepository;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Riego>> historialPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(riegoRepository.findByUsuarioIdOrderByFechaRiegoDesc(usuarioId));
    }

    @GetMapping("/planta/{plantaId}")
    public ResponseEntity<List<Riego>> historialPorPlanta(@PathVariable Long plantaId) {
        return ResponseEntity.ok(riegoRepository.findByPlantaIdOrderByFechaRiegoDesc(plantaId));
    }
}