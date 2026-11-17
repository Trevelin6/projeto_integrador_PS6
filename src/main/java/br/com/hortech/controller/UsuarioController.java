package br.com.hortech.controller;

import br.com.hortech.model.Usuario;
import br.com.hortech.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // GET http://localhost:8080/api/usuarios -> Lista todos os usuários
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // GET http://localhost:8080/api/usuarios/1 -> Busca um usuário específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST http://localhost:8080/api/usuarios -> Cadastra um novo usuário
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario novoUsuario) {
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return ResponseEntity.status(201).body(salvo);
    }
}