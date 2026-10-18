package model;

public class Medico {
    private int idMedico;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String cmp;

    public Medico() {}

    public Medico(String nombres, String apellidos, String especialidad, String cmp) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.cmp = cmp;
    }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getCmp() { return cmp; }
    public void setCmp(String cmp) { this.cmp = cmp; }
}
