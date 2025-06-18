package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PutMapping("/{id}")
    public Usuario editar(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.editarUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }

    @GetMapping("/buscar/{nombreUsuario}")
    public Usuario obtenerPorNombre(@PathVariable String nombreUsuario) {
        return usuarioService.obtenerPorNombreUsuario(nombreUsuario);
    }
}
