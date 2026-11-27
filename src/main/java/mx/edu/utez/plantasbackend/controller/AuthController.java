package mx.edu.utez.plantasbackend.controller;

import mx.edu.utez.plantasbackend.dto.AuthDtos.*;
import mx.edu.utez.plantasbackend.model.Usuario;
import mx.edu.utez.plantasbackend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (request.getCorreo() == null || request.getCorreo().isBlank() ||
                request.getFullName() == null || request.getFullName().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Todos los campos son obligatorios", null));
        }

        String correoLimpio = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Este correo ya está registrado", null));
        }

        Usuario nuevoUsuario = new Usuario(
                request.getFullName().trim(),
                correoLimpio,
                request.getPassword()
        );

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(new AuthResponse("Registro exitoso", guardado));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        if (request.getCorreo() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Faltan credenciales", null));
        }

        String correoLimpio = request.getCorreo().trim().toLowerCase();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoLimpio);

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Correo o contraseña incorrectos", null));
        }

        return ResponseEntity.ok(new AuthResponse("Inicio de sesión exitoso", usuarioOpt.get()));
    }
}
