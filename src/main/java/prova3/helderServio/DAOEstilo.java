package prova3.helderServio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOEstilo {
    public void inserir(String est) throws SQLException, ClassNotFoundException, EstiloJaCadastrado{
        Connection con;
        try{
            String e = pesquisarPor(est);
            throw new EstiloJaCadastrado();
        }catch (EstiloNaoCadastrado e1){
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "insert into estilos values (\'" + est.toLowerCase() + "\')";
            st.execute(cmd);
        }
    }

    public String pesquisarPor(String est) throws EstiloNaoCadastrado{
        Connection con;
        try{
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from estilos where nome = " + "\'" + est.toLowerCase() + "\'";
            ResultSet rs = st.executeQuery(cmd);
            if (rs.next())
                return est;
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new EstiloNaoCadastrado();
    }

    public void removerTodos() throws SQLException, ClassNotFoundException {
        Connection con;
        con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from estilos";
        st.execute(cmd);
        st.close();
    }

    public void remover(String est) throws SQLException, ClassNotFoundException {
        DAOMusica daoM = new DAOMusica();
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from estilos where nome = " + "\'" + est.toLowerCase() + "\'";
        st.execute(cmd);
        ArrayList<Musica> songs = daoM.pesquisarPorEstilo(est);
        for (Musica m: songs){
            daoM.remover(m.getNomeMusica(), m.getIdArtista());
        }
        st.close();
    }
}
