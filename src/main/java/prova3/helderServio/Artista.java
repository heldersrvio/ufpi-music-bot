package prova3.helderServio;

import java.util.ArrayList;

public class Artista extends Usuario {
    public Artista(String idUsu, String nome, String email, String senha){
        this.idUsu = idUsu;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.biblioteca = new ArrayList<Playlist>();
    }
}