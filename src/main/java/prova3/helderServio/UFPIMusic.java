package prova3.helderServio;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class UFPIMusic implements InterfaceStreaming {

    private DAOUsuario daoU = new DAOUsuario();
    private DAOMusica daoM = new DAOMusica();
    private DAOPlaylist daoP = new DAOPlaylist();
    private DAOEstilo daoE = new DAOEstilo();

    public UFPIMusic(){
        try {
            daoU.removerTodos();
            daoM.removerTodos();
            daoP.removerTodos();
            daoE.removerTodos();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario pesquisarUsuario(String idUsu) throws UsuarioNaoCadastrado{
        Usuario u = daoU.pesquisarPor(idUsu);
        return u;
    }

    public Musica pesquisarMusica(String nomeMusica, String idArtista) throws MusicaNaoCadastrada{
        Musica m = daoM.pesquisarPor(nomeMusica, idArtista);
        return m;
    }
    public void cadastrarEstilo(String nome) throws ValorInvalido, EstiloJaCadastrado{
        if (nome == null || nome.length() == 0){
            throw new ValorInvalido();
        }
        try {
            daoE.inserir(nome);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Pesquisa musicas com determinado estilo, independente do artista
    public ArrayList<Musica> pesquisarPorEstilo(String nome){
        ArrayList<Musica> a_m = null;
        try {
            a_m = daoM.pesquisarPorEstilo(nome);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return a_m;
    }

    //Pesquisas musicas com data de lancamento maior que a data informada como parametro
    public ArrayList<Musica> pesquisarPorData(Date inicial){
        ArrayList<Musica> a_m = null;
        try {
            a_m = daoM.pesquisarPorData(inicial);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return a_m;
    }

    //Retorna todas as musicas de um artista
    public ArrayList<Musica> pesquisarPorArtista(String nome){
        ArrayList<Musica> a_m = null;
        try {
            a_m = daoM.pesquisarPorArtista(nome);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return a_m;
    }

    // Permite cadastrar tanto usuarios (assinantes) como artistas (individual ou bandas).
    // O identificador do usuario deve ser unico.
    public void cadastrarUsuario(Usuario usuario) throws ValorInvalido, UsuarioJaCadastrado{
        if (usuario.getIdUsu() == null || usuario.getIdUsu().length() == 0 || usuario.getNome() == null || usuario.getNome().length() == 0 || usuario.getEmail() == null || usuario.getEmail().length() == 0 || usuario.getSenha() == null || usuario.getSenha().length() == 0 || (usuario instanceof Banda && (((Banda) usuario).getMembros() == null || ((Banda) usuario).getMembros().size() == 0)))
            throw new ValorInvalido();
        try {
            daoU.inserir(usuario);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Permite o cadastro de uma musica no arcervo, tornando-a disponivel para inclusao nas playlists dos
    // usuarios do UFPI Music. Uma musica so e igual a outra se for do mesmo artista (nome do usuario ou nome da banda) e contiver o mesmo nome. So artistas podem adicionar musicas ao acervo do UFPIMusic. Tentativa de cadastro de musicas por usuarios nao permitidos devem gerar excecao UsuarioNaoCadastrado.
    public void adicionarMusica(String idUsu, String nomeMusica, String estilo, String link, int duracao, Date lancamento) throws ValorInvalido, UsuarioNaoCadastrado, MusicaJaCadastrada, EstiloNaoCadastrado {
        Date d = new Date(0);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        Date d1 = new Date(gregorianCalendar.getTimeInMillis());
        if (lancamento.compareTo(d) == 0 || lancamento.compareTo(d1) > 0 || idUsu == null || idUsu.length() == 0 || nomeMusica == null || nomeMusica.length() == 0 || estilo == null || estilo.length() == 0 || link == null || link.length() == 0 || duracao <= 0 || lancamento == null)
            throw new ValorInvalido();
        daoE.pesquisarPor(estilo);
        Usuario u = pesquisarUsuario(idUsu);
        if (u instanceof Assinante)
            throw new UsuarioNaoCadastrado();
        Musica m = new Musica(nomeMusica, idUsu, lancamento, duracao, estilo.toLowerCase(), link);
        try {
            daoM.inserir(m);
        } catch (SQLException | ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Permite a criacao de uma playlist para um usuario. Uma playlist so e considerada igual a outra se tiver
    // o mesmo nome para um determinado usuario. Playlists com mesmo nome para usu�rios diferentes sao consideradas diferentes!
    public void criarPlaylist(String idUsu, String nomeLista) throws ValorInvalido, UsuarioNaoCadastrado, PlaylistExistente {
        if (idUsu == null || idUsu.length() == 0 || nomeLista == null || nomeLista.length() == 0)
            throw new ValorInvalido();
        Usuario u = pesquisarUsuario(idUsu);
        try{
            pesquisaPlaylistUsuario(idUsu, nomeLista);
            throw new PlaylistExistente();
        }catch (PlaylistNaoExistente e){
            Playlist p = new Playlist(nomeLista);
            try {
                daoP.inserir(p, u);
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Adiciona musica a uma playlist de um usuario.
    public void adicionarMusicaPlaylist(String idUsu, String nomeLista, String nomeAutor, String nomeMusica) throws UsuarioNaoCadastrado, PlaylistNaoExistente, MusicaNaoCadastrada, MusicaJaCadastrada {
        Musica m = pesquisarMusica(nomeMusica, nomeAutor);
        Usuario u = pesquisarUsuario(idUsu);
        Playlist p = pesquisaPlaylistUsuario(idUsu, nomeLista);
        for (Musica l:  p.getMusicas()){
            if (l.getIdArtista().equals(m.getIdArtista()) && l.getNomeMusica().equals(m.getNomeMusica()))
                throw new MusicaJaCadastrada();
        }
        p.getMusicas().add(m);
        if (!p.getLista_estilos_playlist().contains(m.getEstiloMusical()))
            p.getLista_estilos_playlist().add(m.getEstiloMusical());
        p.setDuracaoTotal(p.getDuracaoTotal() + m.getduracao());
        try {
            daoP.alterar(p, u);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Retorna uma playlist de um usuario. Se nao houver, gera excecao.
    public Playlist pesquisaPlaylistUsuario(String idUsu, String nomeLista) throws UsuarioNaoCadastrado, PlaylistNaoExistente{
        Usuario u = daoU.pesquisarPor(idUsu);
        Playlist p = daoP.pesquisarPor(nomeLista, idUsu);
        return p;
    }

    // Retorna todas as playlists de um usuario com um dado estilo.
    // Se nao houver nenhuma com aquele estilo, retorna excecao.
    public ArrayList<Playlist> pesquisaPlaylistEstilo(String idUsu, String estilo) throws UsuarioNaoCadastrado, PlaylistNaoExistente{
        ArrayList<Playlist> lista_e = new ArrayList<Playlist>();
        Usuario u = pesquisarUsuario(idUsu);
        for (Playlist p: u.getBiblioteca()){
            if (p.getLista_estilos_playlist().contains(estilo.toLowerCase())){
                lista_e.add(p);
            }
        }
        if (lista_e.size() == 0)
            throw new PlaylistNaoExistente();
        return lista_e;
    }

    public void removerEstilo(String nomeEstilo) throws EstiloNaoCadastrado {
        String e = daoE.pesquisarPor(nomeEstilo);
        try {
            daoE.remover(e);
        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    public void juntarUsuarios(String idUsu1, String idUsu2, String idUsu3) throws UsuarioNaoCadastrado{
        try{
            Usuario u1 = daoU.pesquisarPor(idUsu1);
            Usuario u2 = daoU.pesquisarPor(idUsu2);
            Usuario u3 = daoU.pesquisarPor(idUsu3);
            daoU.juntarUsuarios(idUsu1, idUsu2, idUsu3);
        } catch (SQLException | UsuarioJaCadastrado | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
