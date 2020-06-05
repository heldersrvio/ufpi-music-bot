package prova3.helderServio;

public class UsuarioNaoCadastrado extends Exception {
    public UsuarioNaoCadastrado() {
        super("O usuario nao foi cadastrado\n");
    }
}
