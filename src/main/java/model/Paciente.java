package model;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

public class Paciente {
    private int idPaciente;
    @NotBlank(message = "Ingresa el DNI.")
    @Size(max = 15, message = "El DNI admite hasta 15 caracteres.")
    private String dni;
    @NotBlank(message = "Ingresa los nombres.")
    @Size(max = 100, message = "Los nombres admiten hasta 100 caracteres.")
    private String nombres;
    @NotBlank(message = "Ingresa los apellidos.")
    @Size(max = 100, message = "Los apellidos admiten hasta 100 caracteres.")
    private String apellidos;
    @NotNull(message = "Ingresa la fecha de nacimiento.")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;
    @Size(max = 20, message = "El teléfono admite hasta 20 caracteres.")
    private String telefono;
    @Email(message = "Ingresa un correo válido.")
    @Size(max = 100, message = "El correo admite hasta 100 caracteres.")
    private String email;

    public Paciente() {}

    public Paciente(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
    }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
