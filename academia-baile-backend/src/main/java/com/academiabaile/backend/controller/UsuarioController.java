package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private PasswordEncoder passwordEncoder;


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
    @Autowired
    private com.academiabaile.backend.repository.UsuarioRepository usuarioRepository;

    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id).orElseThrow();
    }
    @Autowired
    private com.academiabaile.backend.service.EmailService emailService;

    @PostMapping("/recuperar")
    public String recuperarContrasena() {
        // Buscar al usuario con rol ADMIN
        Usuario admin = usuarioRepository.findByRol(Usuario.Rol.ADMIN);

        if (admin == null || admin.getCorreoRecuperacion() == null || admin.getCorreoRecuperacion().isBlank()) {
            return "No se encontró un administrador con correo de recuperación configurado.";
        }

        // Generar código de recuperación (6 dígitos)
        String codigo = generarCodigoRecuperacion();
        String mensaje = "Este es tu código de recuperación: " + codigo;

        // Guardar el código en la base de datos
        admin.setCodigoRecuperacion(codigo);
        usuarioRepository.save(admin);

        // Enviar el correo
        emailService.enviarCorreo(admin.getCorreoRecuperacion(), "Recuperación de contraseña", mensaje);

        return "Se envió un código de recuperación al correo del administrador.";
    }



    // Método auxiliar para generar código de recuperación
    private String generarCodigoRecuperacion() {
        int codigo = (int)(Math.random() * 900000) + 100000; // 6 dígitos aleatorios
        return String.valueOf(codigo);
    }
        @PostMapping("/validar-codigo")
public String validarCodigoYActualizar(@RequestBody Map<String, String> datos) {
    String correo = datos.get("correo");
    String codigo = datos.get("codigo");
    String nuevaContrasena = datos.get("nuevaContrasena");

    Usuario admin = usuarioRepository.findByCorreoRecuperacion(correo);

    if (admin == null || admin.getRol() != Usuario.Rol.ADMIN) {
        return "Correo no válido o no pertenece a un administrador.";
    }

    if (!codigo.equals(admin.getCodigoRecuperacion())) {
        return "Código incorrecto.";
    }

    String nuevaPassEncriptada = passwordEncoder.encode(nuevaContrasena);
    admin.setContrasena(nuevaPassEncriptada);
    admin.setCodigoRecuperacion(null); // eliminar código

    usuarioRepository.save(admin);

    return "Contraseña actualizada correctamente.";
}


@GetMapping("/public/usuario-por-nombre/{username}")
public ResponseEntity<Usuario> obtenerPorNombreUsuario(@PathVariable String username) {
    Usuario user = usuarioRepository.findByNombreUsuario(username);
    if (user == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(user);
}

}
