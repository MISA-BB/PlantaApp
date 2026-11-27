package mx.edu.utez.plantasbackend.controller;

import mx.edu.utez.plantasbackend.model.Planta;
import mx.edu.utez.plantasbackend.model.Riego;
import mx.edu.utez.plantasbackend.repository.PlantaRepository;
import mx.edu.utez.plantasbackend.repository.RiegoRepository;
import mx.edu.utez.plantasbackend.service.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/plantas")
@CrossOrigin(origins = "*")
public class PlantaController {

    private final PlantaRepository plantaRepository;
    private final RiegoRepository riegoRepository;
    private final GeminiService geminiService;

    public PlantaController(PlantaRepository plantaRepository,
                            RiegoRepository riegoRepository,
                            GeminiService geminiService) {
        this.plantaRepository = plantaRepository;
        this.riegoRepository = riegoRepository;
        this.geminiService = geminiService;
    }

    // PASO 1: Solo analiza con Gemini y devuelve la vista previa SIN guardar en MySQL
    @PostMapping("/analizar")
    public ResponseEntity<?> analizarPlanta(
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("imagen") MultipartFile imagen) {
        try {
            if (imagen == null || imagen.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "La imagen es obligatoria"));
            }

            String base64 = Base64.getEncoder().encodeToString(imagen.getBytes());
            String mimeType = imagen.getContentType() != null ? imagen.getContentType() : "image/jpeg";

            Planta plantaIdentificada = geminiService.analizarPlanta(base64, mimeType);
            plantaIdentificada.setUsuarioId(usuarioId);
            plantaIdentificada.setImagenBase64(base64);

            return ResponseEntity.ok(plantaIdentificada);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al analizar la planta: " + e.getMessage()));
        }
    }

    // PASO 2: Guarda en MySQL cuando el usuario confirma en la app
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarPlanta(@RequestBody Planta planta) {
        try {
            if (planta.getUsuarioId() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "El usuarioId es obligatorio"));
            }
            if (planta.getFechaRegistro() == null) {
                planta.setFechaRegistro(LocalDateTime.now());
            }
            Planta guardada = plantaRepository.save(planta);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al guardar la planta: " + e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Planta>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(plantaRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return plantaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Planta no encontrada")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPlanta(@PathVariable Long id, @RequestBody Planta datos) {
        Optional<Planta> optPlanta = plantaRepository.findById(id);
        if (optPlanta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Planta no encontrada"));
        }

        Planta planta = optPlanta.get();
        if (datos.getNombreComun() != null && !datos.getNombreComun().isBlank()) {
            planta.setNombreComun(datos.getNombreComun().trim());
        }

        Planta actualizada = plantaRepository.save(planta);
        return ResponseEntity.ok(actualizada);
    }

    @PostMapping("/{id}/regar")
    public ResponseEntity<?> registrarRiego(@PathVariable Long id) {
        Optional<Planta> optPlanta = plantaRepository.findById(id);
        if (optPlanta.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Planta no encontrada"));
        }

        Planta planta = optPlanta.get();
        LocalDateTime ahora = LocalDateTime.now();
        int dias = (planta.getFrecuenciaRiegoDias() != null && planta.getFrecuenciaRiegoDias() > 0)
                ? planta.getFrecuenciaRiegoDias() : 7;

        planta.setUltimoRiego(ahora);
        planta.setProximoRiego(ahora.plusDays(dias));
        Planta plantaActualizada = plantaRepository.save(planta);

        Riego riego = new Riego();
        riego.setPlantaId(planta.getId());
        riego.setUsuarioId(planta.getUsuarioId());
        riego.setNombrePlanta(planta.getNombreComun());
        riego.setImagenPlantaBase64(planta.getImagenBase64());
        riego.setFechaRiego(ahora);
        Riego riegoGuardado = riegoRepository.save(riego);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("planta", plantaActualizada);
        respuesta.put("riego", riegoGuardado);
        respuesta.put("message", "¡Riego registrado correctamente!");

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPlanta(@PathVariable Long id) {
        if (!plantaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        plantaRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Planta eliminada"));
    }
}