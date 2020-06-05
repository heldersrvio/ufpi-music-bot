package prova3.helderServio;

public class PlaylistExistente extends Exception {
    public PlaylistExistente() {
        super("A playlist ja existe\n");
    }
}
