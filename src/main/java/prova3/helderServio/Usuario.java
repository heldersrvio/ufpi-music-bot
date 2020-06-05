package prova3.helderServio;

import java.util.ArrayList;

public class Usuario {
    protected String idUsu;
    protected String nome;
    protected String email;
    protected String senha;
    protected ArrayList<Playlist> biblioteca;

    public ArrayList<Playlist> getBiblioteca() {
        DAOPlaylist daoP = new DAOPlaylist();
        try {
            biblioteca = daoP.pesquisarPorUsuario(idUsu);
        } catch (PlaylistNaoExistente playlistNaoExistente) {
            playlistNaoExistente.printStackTrace();
        }
        return biblioteca;
    }

    public void setBiblioteca(ArrayList<Playlist> biblioteca) {
        this.biblioteca = biblioteca;
    }

    public String getIdUsu() {
        return idUsu;
    }

    public void setIdUsu(String idUsu) {
        this.idUsu = idUsu;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
