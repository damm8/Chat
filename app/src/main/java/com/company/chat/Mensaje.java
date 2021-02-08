package com.company.chat;

public class Mensaje {
    public String autorEmail;
    public String autorNombre;
    public String autorFoto;
    public String mensaje;
    public String fecha;

    public Mensaje(String autorEmail, String autorNombre, String autorFoto, String mensaje, String fecha) {
        this.autorEmail = autorEmail;
        this.autorNombre = autorNombre;
        this.autorFoto = autorFoto;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }
}
