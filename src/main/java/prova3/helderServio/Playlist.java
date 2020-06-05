package prova3.helderServio;
import java.util.ArrayList;

public class Playlist {
    private ArrayList<Musica> lista_m;
    private String nomeLista;
    private ArrayList<String> lista_estilos_playlist;
    private int duracaoTotal;

    public ArrayList<String> getLista_estilos_playlist() {
        return lista_estilos_playlist;
    }

    public void setLista_estilos_playlist(ArrayList<String> lista_estilos_playlist) {
        this.lista_estilos_playlist = lista_estilos_playlist;
    }

    public int getDuracaoTotal() {
        return duracaoTotal;
    }

    public void setDuracaoTotal(int duracaoTotal) {
        this.duracaoTotal = duracaoTotal;
    }

    public Playlist(String nomeLista) {
        this.nomeLista = nomeLista;
        lista_m = new ArrayList<Musica>();
        lista_estilos_playlist = new ArrayList<String>();
        duracaoTotal = 0;
    }

    public ArrayList<Musica> getMusicas() {
        return lista_m;
    }

    public void setLista_m(ArrayList<Musica> lista_m) {
        this.lista_m = lista_m;
    }

    public String getNomeLista() {
        return nomeLista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }
}
