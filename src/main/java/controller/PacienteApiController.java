package controller;

import jakarta.validation.Valid;
import model.Paciente;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import service.LaboratorioService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteApiController {
    private final LaboratorioService service;
    public PacienteApiController(LaboratorioService service) { this.service = service; }

    @GetMapping
    public List<Paciente> listar() { return service.listarPacientes(); }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable int id) {
        if (id <= 0) return noEncontrado();
        return service.obtenerPacientePorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(this::noEncontrado);
    }

    @PostMapping
    public ResponseEntity<?> agregar(@Valid @RequestBody Paciente paciente) throws Exception {
        normalizar(paciente);
        int id = service.registrarPaciente(paciente);
        paciente.setIdPaciente(id);
        return ResponseEntity.status(201).body(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable int id, @Valid @RequestBody Paciente paciente) {
        if (id <= 0) return noEncontrado();
        normalizar(paciente);
        if (!service.actualizarPaciente(id, paciente)) return noEncontrado();
        paciente.setIdPaciente(id);
        return ResponseEntity.ok(paciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        if (id <= 0 || !service.eliminarPaciente(id)) return noEncontrado();
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("mensaje", "Revisa los campos del formulario.", "errores", errores));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> formato() {
        return ResponseEntity.badRequest().body(Map.of("mensaje", "Revisa los datos y el formato de la fecha."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflicto() {
        return ResponseEntity.status(409).body(Map.of("mensaje", "La operación no es posible: el paciente tiene registros asociados o los datos incumplen una restricción."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> fallo(Exception ex) {
        return ResponseEntity.status(ex instanceof DataAccessException ? 503 : 500)
                .body(Map.of("mensaje", "No se pudo confirmar la operación. Actualiza el listado antes de volver a intentarlo."));
    }

    private ResponseEntity<?> noEncontrado() {
        return ResponseEntity.status(404).body(Map.of("mensaje", "Paciente no encontrado."));
    }

    private static void normalizar(Paciente p) {
        p.setDni(p.getDni().trim()); p.setNombres(p.getNombres().trim()); p.setApellidos(p.getApellidos().trim());
        p.setTelefono(p.getTelefono() == null || p.getTelefono().isBlank() ? null : p.getTelefono().trim());
        p.setEmail(p.getEmail() == null || p.getEmail().isBlank() ? null : p.getEmail().trim());
    }
}
