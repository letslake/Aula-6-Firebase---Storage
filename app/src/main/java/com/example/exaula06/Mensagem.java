package com.example.exaula06;

import java.util.Date;

class Mensagem implements Comparable <Mensagem>{
    private String usuario;
    private Date data;
    private String texto;

    @Override
    public int compareTo(Mensagem o) {
        return data.compareTo(o.data);
    }

    public Mensagem(String usuario, Date data, String texto) {
        this.usuario = usuario;
        this.data = data;
        this.texto = texto;
    }
    public Mensagem (){

    }


    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
