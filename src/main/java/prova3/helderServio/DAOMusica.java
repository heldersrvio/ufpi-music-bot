package prova3.helderServio;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DAOMusica {

    public void inserir(Musica m) throws SQLException, ClassNotFoundException, MusicaJaCadastrada, ParseException {
        Connection con;
        BigInteger k = new BigInteger((new SimpleDateFormat("yyyymmdd")).format(m.getDataDeLancamento()));
        try{
            pesquisarPor(m.getNomeMusica(), m.getIdArtista());
            throw new MusicaJaCadastrada();
        }catch (MusicaNaoCadastrada e1){
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "insert into musicas (nome, artista, lancamento, duracao, link, estilo) values (" + "\'" + m.getNomeMusica().toLowerCase() + "\'" + ", " + "\'" + m.getIdArtista().toLowerCase() + "\'" + ", " + k + ", " + m.getDuracao() + ", " + "\'" + m.getLink().toLowerCase() + "\'" + ", " + "\'" + m.getEstiloMusical().toLowerCase() + "\'" + ")";
            st.execute(cmd);
        }
    }

    public Musica pesquisarPor(String nomeMusica, String idArtista) throws MusicaNaoCadastrada{
        Connection con;
        Musica m = null;
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from musicas where nome = " + "\'" + nomeMusica.toLowerCase() + "\'" + " and artista = " + "\'" + idArtista.toLowerCase() + "\'";
            st.execute(cmd);
            ResultSet rs = st.executeQuery(cmd);
            if (rs.next()){
                m = new Musica(rs.getString("nome"), rs.getString("artista"), rs.getDate("lancamento"), rs.getInt("duracao"), rs.getString("estilo"), rs.getString("link"));
                return m;
            }
        } catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new MusicaNaoCadastrada();
    }

    public void removerTodos() throws ClassNotFoundException, SQLException {
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from musicas";
        st.execute(cmd);
        st.close();
    }

    public void remover(String nomeMusica, String idArtista) throws ClassNotFoundException, SQLException {
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from musicas where nome = " + "\'" + nomeMusica.toLowerCase() + "\'" + " and artista = " + "\'" + idArtista.toLowerCase() + "\'";
        st.execute(cmd);
        cmd = "delete from musicasplaylists where musica = " + "\'" + nomeMusica.toLowerCase() + "\'" + " and artista = " + "\'" + idArtista.toLowerCase() + "\'";
        st.execute(cmd);
        st.close();
    }

    public void alterar(Musica m){
        try{
            Connection con = Conexao.getConnection();
            Statement st = con.createStatement();
            BigInteger k = new BigInteger((new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")).format(m.getDataDeLancamento()));
            String cmd = "update musicas set estilo = " + "\'" + m.getEstiloMusical() + "\'" + ", link = " + "\'" + m.getLink() + "\'" + ", lancamento = " + k + ", duracao = " + m.getDuracao() + " where nome = " + "\'" + m.getNomeMusica() + "\'" + " and artista = " + "\'" + m.getIdArtista() + "\'";
            st.execute(cmd);
            st.close();
        }catch (Exception e){
            e.printStackTrace(System.out);
        }
    }

    public ArrayList<Musica> pesquisarPorEstilo(String est) throws ClassNotFoundException, SQLException {
        Connection con;
        ArrayList<Musica> lista_m = new ArrayList<Musica>();
        Musica m = null;
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from musicas where estilo = " + "\'" + est.toLowerCase() + "\'";
            st.execute(cmd);
            ResultSet rs = st.executeQuery(cmd);
            while (rs.next()){
                m = new Musica(rs.getString("nome"), rs.getString("artista"), rs.getDate("lancamento"), rs.getInt("duracao"), rs.getString("estilo"), rs.getString("link"));
                lista_m.add(m);
            }
        } catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista_m;
    }

    public ArrayList<Musica> pesquisarPorData(Date d) throws ClassNotFoundException, SQLException {
        Connection con;
        ArrayList<Musica> lista_m = new ArrayList<Musica>();
        Musica m = null;
        BigInteger k = new BigInteger((new SimpleDateFormat("yyyymmdd")).format(d));
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from musicas where lancamento > " + k;
            st.execute(cmd);
            ResultSet rs = st.executeQuery(cmd);
            while (rs.next()){
                m = new Musica(rs.getString("nome"), rs.getString("artista"), rs.getDate("lancamento"), rs.getInt("duracao"), rs.getString("estilo"), rs.getString("link"));
                lista_m.add(m);
            }
        } catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista_m;
    }

    public ArrayList<Musica> pesquisarPorArtista(String idArtista) throws ClassNotFoundException, SQLException {
        Connection con;
        ArrayList<Musica> lista_m = new ArrayList<Musica>();
        Musica m = null;
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from musicas where artista = " + "\'" + idArtista.toLowerCase() + "\'";
            st.execute(cmd);
            ResultSet rs = st.executeQuery(cmd);
            while (rs.next()){
                m = new Musica(rs.getString("nome"), rs.getString("artista"), rs.getDate("lancamento"), rs.getInt("duracao"), rs.getString("estilo"), rs.getString("link"));
                lista_m.add(m);
            }
        } catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lista_m;
    }
}
