package controller;

import jakarta.validation.Valid;
import model.Paciente;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.LaboratorioService;
import java.util.List;

@Controller
public class PacienteWebController {
    private final LaboratorioService service;

    public PacienteWebController(LaboratorioService service) { this.service = service; }

    @InitBinder("paciente")
    public void configurarFormulario(WebDataBinder binder) {
        binder.setAllowedFields("dni", "nombres", "apellidos", "fechaNacimiento", "telefono", "email");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/")
    public String inicio() { return "redirect:/pacientes"; }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("paciente", new Paciente());
        cargarListado(model);
        return "pacientes";
    }

    @PostMapping("/pacientes")
    public String registrar(@Valid @ModelAttribute("paciente") Paciente paciente,
                            BindingResult binding, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            cargarListado(model);
            return "pacientes";
        }
        try {
            int id = service.registrarPaciente(paciente);
            flash.addFlashAttribute("exito", "Paciente registrado correctamente. ID: " + id);
            return "redirect:/pacientes";
        } catch (Exception ex) {
            model.addAttribute("errorRegistro", "No se pudo confirmar el registro. Consulta el listado antes de volver a intentarlo.");
            cargarListado(model);
            return "pacientes";
        }
    }

    @GetMapping("/pacientes/{id}/editar")
    public String editar(@PathVariable int id, Model model, RedirectAttributes flash) {
        if (id <= 0) {
            flash.addFlashAttribute("errorRegistro", "Paciente no encontrado.");
            return "redirect:/pacientes";
        }
        try {
            var paciente = service.obtenerPacientePorId(id);
            if (paciente.isEmpty()) {
                flash.addFlashAttribute("errorRegistro", "Paciente no encontrado.");
                return "redirect:/pacientes";
            }
            model.addAttribute("paciente", paciente.get());
            model.addAttribute("idEdicion", id);
            cargarListado(model);
            return "pacientes";
        } catch (org.springframework.dao.DataAccessException ex) {
            flash.addFlashAttribute("errorRegistro", "No se pudo cargar el paciente para editar.");
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/pacientes/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute("paciente") Paciente paciente,
                             BindingResult binding, Model model, RedirectAttributes flash) {
        if (id <= 0) {
            flash.addFlashAttribute("errorRegistro", "Paciente no encontrado.");
            return "redirect:/pacientes";
        }
        model.addAttribute("idEdicion", id);
        if (binding.hasErrors()) {
            cargarListado(model);
            return "pacientes";
        }
        try {
            if (!service.actualizarPaciente(id, paciente)) {
                flash.addFlashAttribute("errorRegistro", "El paciente ya no existe.");
                return "redirect:/pacientes";
            }
            flash.addFlashAttribute("exito", "Datos del paciente actualizados correctamente.");
            return "redirect:/pacientes";
        } catch (org.springframework.dao.DataAccessException ex) {
            model.addAttribute("errorRegistro", "No se pudo confirmar la actualización. Revisa el listado antes de volver a intentarlo.");
            cargarListado(model);
            return "pacientes";
        }
    }

    private void cargarListado(Model model) {
        try {
            model.addAttribute("pacientes", service.listarPacientes());
        } catch (org.springframework.dao.DataAccessException ex) {
            model.addAttribute("pacientes", List.of());
            model.addAttribute("errorConsulta", "No se pudo cargar el listado. Comprueba la conexión con la base de datos.");
        }
    }
}
