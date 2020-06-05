package prova3.helderServio;

public class PlaylistNaoExistente extends Exception {
    public PlaylistNaoExistente() {
        super("A playlist nao existe\n");
    }
}
