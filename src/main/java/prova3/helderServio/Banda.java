package prova3.helderServio;

import java.util.ArrayList;

public class Banda extends Usuario{
    public ArrayList<Artista> getMembros() {
        return membros;
    }

    public void setMembros(ArrayList<Artista> membros) {
        this.membros = membros;
    }

    public ArrayList<Artista> membros;

    public Banda(String idUsu, String nome, String email, String senha, ArrayList<Artista> membros) {
        this.membros = membros;
        this.idUsu = idUsu;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}
