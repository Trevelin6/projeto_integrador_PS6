package br.com.hortech.controller;

import br.com.hortech.model.Usuario;
import br.com.hortech.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario req) {
        return usuarioRepository.findByEmail(req.getEmail())
                .filter(u -> u.getSenha().equals(req.getSenha()))
                .map(u -> ResponseEntity.ok().body((Object) u))
                .orElseGet(() -> ResponseEntity.status(401).body("Credenciais invalidas"));
    }
}
