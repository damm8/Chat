package com.company.chat;

import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    public Mensaje(QueryDocumentSnapshot document){
        this.autorEmail = document.getString("autorEmail");
        this.autorNombre = document.getString("autorNombre");
        this.autorFoto = document.getString("autorFoto");
        this.mensaje = document.getString("mensaje");
        this.fecha = document.getString("fecha");
    }
}
