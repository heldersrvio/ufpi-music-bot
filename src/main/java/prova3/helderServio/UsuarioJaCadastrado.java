package prova3.helderServio;

public class UsuarioJaCadastrado extends Exception {
    public UsuarioJaCadastrado() {
        super("O usuario ja foi cadastrado\n");
    }
}
