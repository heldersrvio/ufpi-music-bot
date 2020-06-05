package prova3.helderServio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOPlaylist {

    public void inserir(Playlist p, Usuario u) throws SQLException, ClassNotFoundException, PlaylistExistente {
        Connection con;
        try {
            pesquisarPor(p.getNomeLista().toLowerCase(), u.getIdUsu().toLowerCase());
            throw new PlaylistExistente();
        }catch(PlaylistNaoExistente e1){
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "insert into playlists (usuario, nome) values (" + "\'" + u.getIdUsu().toLowerCase() + "\'" + ", " + "\'" + p.getNomeLista().toLowerCase() + "\'" + ")" ;
            st.execute(cmd);
            for (Musica m: p.getMusicas()){
                cmd = "insert into musicasplaylists (usuario, playlist, musica, artista) values (" + "\'" + u.getIdUsu().toLowerCase() + "\'" + ", " + "\'" + p.getNomeLista().toLowerCase() + "\'" + ", " + "\'" + m.getNomeMusica().toLowerCase() + "\'" + ", " + "\'" + m.getIdArtista().toLowerCase() + "\'" + ")";
                st.execute(cmd);
            }
            st.close();
        }
    }

    public Playlist pesquisarPor(String nomeLista, String idUsu) throws PlaylistNaoExistente{
        Connection con;
        DAOMusica m = new DAOMusica();
        int duracaoTotal = 0;
        ArrayList<String> lista_estilos = new ArrayList<String>();
        ArrayList<Musica> lista_musicas = new ArrayList<Musica>();
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from musicasplaylists where playlist = " + "\'" + nomeLista.toLowerCase() + "\'" +  " and usuario = " + "\'" + idUsu.toLowerCase() + "\'";
            ResultSet rs = st.executeQuery(cmd);
            while (rs.next()){
                Musica k = m.pesquisarPor(rs.getString("musica"), rs.getString("artista"));
                duracaoTotal = duracaoTotal + k.getDuracao();
                if (!lista_estilos.contains(k.getEstiloMusical()))
                    lista_estilos.add(k.getEstiloMusical());
                lista_musicas.add(k);
            }
            if ((st.executeQuery("select * from playlists where nome = " + "\'" + nomeLista.toLowerCase() + "\'" +  " and usuario = " + "\'" + idUsu.toLowerCase() + "\'")).next()) {
                Playlist p = new Playlist(nomeLista.toLowerCase());
                p.setDuracaoTotal(duracaoTotal);
                p.setLista_estilos_playlist(lista_estilos);
                p.setLista_m(lista_musicas);
                return p;
            }
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new PlaylistNaoExistente();
    }

    public void removerTodos() throws ClassNotFoundException, SQLException {
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from playlists";
        st.execute(cmd);
        cmd = "delete from musicasplaylists";
        st.execute(cmd);
        st.close();
    }

    public void remover(String nomeLista, String idUsu) throws ClassNotFoundException, SQLException {
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from playlists where usuario = " + "\'" + idUsu.toLowerCase() + "\'" + " and nome = " + "\'" + nomeLista.toLowerCase() + "\'";
        st.execute(cmd);
        cmd = "delete from musicasplaylists where usuario = " + "\'" + idUsu.toLowerCase() + "\'" + " and playlist = " + "\'" + nomeLista.toLowerCase() + "\'";
        st.execute(cmd);
        st.close();
    }

    public void alterar(Playlist p, Usuario u) throws ClassNotFoundException, SQLException {
        try {
            remover(p.getNomeLista().toLowerCase(), u.getIdUsu().toLowerCase());
            inserir(p, u);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public ArrayList<Playlist> pesquisarPorUsuario(String idUsu) throws PlaylistNaoExistente{
        Connection con;
        DAOMusica m = new DAOMusica();
        int duracaoTotal = 0;
        int flag = 0;
        ArrayList<String> lista_estilos = new ArrayList<String>();
        ArrayList<Musica> lista_musicas = new ArrayList<Musica>();
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet rs2 = st.executeQuery("select * from playlists where usuario = " + "\'" + idUsu.toLowerCase() + "\'");
            while (rs2.next()) {
                lista_estilos = new ArrayList<String>();
                lista_musicas = new ArrayList<Musica>();
                flag = 1;
                String cmd = "select * from musicasplaylists where usuario = " + "\'" + idUsu.toLowerCase() + "\'" + " and playlist = " + "\'" + rs2.getString("nome") + "\'";
                ResultSet rs = st2.executeQuery(cmd);
                while (rs.next()){
                    Musica k = m.pesquisarPor(rs.getString("musica"), rs.getString("artista"));
                    duracaoTotal = duracaoTotal + k.getDuracao();
                    if (!lista_estilos.contains(k.getEstiloMusical()))
                        lista_estilos.add(k.getEstiloMusical());
                    lista_musicas.add(k);
                }
                Playlist p = new Playlist(rs2.getString("nome").toLowerCase());
                p.setDuracaoTotal(duracaoTotal);
                p.setLista_estilos_playlist(lista_estilos);
                p.setLista_m(lista_musicas);
                playlists.add(p);
            }
            if(flag == 1)
                return playlists;
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new PlaylistNaoExistente();
    }

}
