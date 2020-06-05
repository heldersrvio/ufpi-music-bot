package prova3.helderServio;
import java.util.Date;

public class Musica {
    private String nomeMusica;
    private String idArtista;
    private Date dataDeLancamento;
    private int duracao;
    private String estiloMusical;
    private String link;

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public String getNomeArtista() {
        return nomeArtista;
    }

    public void setNomeArtista(String nomeArtista) {
        this.nomeArtista = nomeArtista;
    }

    private String nomeArtista;

    public String getNomeMusica() {
        return nomeMusica;
    }

    public void setNomeMusica(String nome) {
        this.nomeMusica = nome;
    }

    public String getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(String idArtista) {
        this.idArtista = idArtista;
    }

    public Date getDataDeLancamento() {
        return dataDeLancamento;
    }

    public void setDataDeLancamento(Date dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }

    public int getduracao() {
        return duracao;
    }

    public void setduracao(int duracao) {
        this.duracao = duracao;
    }

    public String getEstiloMusical() {
        return estiloMusical;
    }

    public void setEstiloMusical(String estiloMusical) {
        this.estiloMusical = estiloMusical;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Musica(String nomeMusica, String idArtista, Date dataDeLancamento, int duracao, String estiloMusical, String link) {
        this.nomeMusica = nomeMusica;
        this.idArtista = idArtista;
        this.dataDeLancamento = dataDeLancamento;
        this.duracao = duracao;
        this.estiloMusical = estiloMusical;
        this.link = link;
    }
}
