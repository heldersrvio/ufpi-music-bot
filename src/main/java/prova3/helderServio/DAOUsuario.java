package prova3.helderServio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOUsuario {

    public void inserir(Usuario c) throws SQLException, ClassNotFoundException, UsuarioJaCadastrado {
        Connection con;
        try {
            pesquisarPor(c.getIdUsu());
            throw new UsuarioJaCadastrado();
        } catch (UsuarioNaoCadastrado e1) {
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "insert into usuarios values (" + "\'" + c.getIdUsu().toLowerCase() + "\'" + ", \'" + c.getNome().toLowerCase() + "\'" + ", \'" + c.getEmail().toLowerCase() + "\'" + ", \'" + c.getSenha() + "\'";;
            if (c instanceof Assinante) {
                cmd = cmd + ", null, 0)";
                st.execute(cmd);
            }
            else if (c instanceof Artista) {
                cmd = cmd + ", null, 1)";
                st.execute(cmd);
            }
            else{
                for (int i = 0; i < ((Banda) c).getMembros().size(); i++){
                    String cmd2 = "update usuarios set idbanda = " + "\'" + c.getIdUsu() + "\'" + " where identificador = " + "\'" + ((Banda) c).getMembros().get(i).getIdUsu() + "\'";
                    st.execute(cmd2);
                }
                cmd = cmd + ", null, 2)";
                st.execute(cmd);
            }
            st.close();
        }
    }

    public void alterar(Usuario c) {
        try {
            remover(c.getIdUsu());
            inserir(c);
            if (c instanceof Banda){
                Connection con = Conexao.getConnection();
                Statement st = con.createStatement();
                String cmd = "select * from usuarios where idbanda = " + "\'" + c.getIdUsu().toLowerCase() + "\'";
                ResultSet rs = st.executeQuery(cmd);
                while (rs.next()){
                    String idUsu = rs.getString("identificador");
                    Usuario u = pesquisarPor(idUsu);
                    if (!((Banda) c).getMembros().contains((Artista) u)){
                        cmd = "update usuarios set idbanda = null where identificador = " + "\'" + idUsu + "\'";
                        st.execute(cmd);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void remover(String idUsu) throws ClassNotFoundException, SQLException {
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        try {
            if (pesquisarPor(idUsu) instanceof Banda) {
                String cmd = "update usuarios set idbanda = null where idbanda = " + "\'" + idUsu + "\'";
                st.execute(cmd);
            }
        }catch (Exception e){
            e.printStackTrace(System.out);
        }
        String cmd = "delete from usuarios where identificador = " + "\'" + idUsu.toLowerCase() + "\'";
        st.execute(cmd);
        st.close();
    }

    public void removerTodos() throws ClassNotFoundException, SQLException {
        Connection con = Conexao.getConnection();
        Statement st = con.createStatement();
        String cmd = "delete from usuarios";
        st.execute(cmd);
        st.close();
    }

    public Usuario pesquisarPor(String idUsu) throws UsuarioNaoCadastrado {
        Connection con;
        Usuario u = null;
        try {
            con = Conexao.getConnection();
            Statement st = con.createStatement();
            String cmd = "select * from usuarios where identificador = " + "\'" + idUsu.toLowerCase() + "\'";
            ResultSet rs = st.executeQuery(cmd);
            if (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String senha = rs.getString("senha");
                int tipo = rs.getInt("tipo");
                if (tipo == 0)
                    u = new Assinante(idUsu, nome, email, senha);
                else if (tipo == 1)
                    u = new Artista(idUsu, nome, email, senha);
                else{
                    cmd = "select * from usuarios where idbanda = " + "\'" + idUsu.toLowerCase() + "\'";
                    ResultSet rs2 = st.executeQuery(cmd);
                    ArrayList<Artista> membros = new ArrayList<>();
                    while (rs2.next()){
                        String id_m = rs2.getString("identificador");
                        String nome_m = rs2.getString("nome");
                        String email_m = rs2.getString("email");
                        String senha_m = rs2.getString("senha");
                        u = new Artista(id_m, nome_m, email_m, senha_m);
                        membros.add((Artista) u);
                    }
                    u = new Banda(idUsu, nome, email, senha, membros);
                }
                return u;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new UsuarioNaoCadastrado();
    }

    public void juntarUsuarios(String idUsu1, String idUsu2, String idUsu3) throws UsuarioNaoCadastrado, SQLException, ClassNotFoundException, UsuarioJaCadastrado{
        DAOPlaylist daoP = new DAOPlaylist();
        Usuario u1 = pesquisarPor(idUsu1);
        Usuario u2 = pesquisarPor(idUsu2);
        Usuario u3 = pesquisarPor(idUsu3);
        try {
            ArrayList<Playlist> playlists1 = daoP.pesquisarPorUsuario(u1.getIdUsu());
            for (Playlist p: playlists1){
                daoP.remover(p.getNomeLista(), u1.getIdUsu());
                daoP.inserir(p, u3);
            }
            try{
                ArrayList<Playlist> playlists2 = daoP.pesquisarPorUsuario(u2.getIdUsu());
                for (Playlist p: playlists2){
                    daoP.remover(p.getNomeLista(), u2.getIdUsu());
                    daoP.inserir(p, u3);
                }
            }catch (PlaylistNaoExistente | PlaylistExistente e2){
                e2.printStackTrace();
            }
        }catch (PlaylistNaoExistente | PlaylistExistente e1){
            try{
                ArrayList<Playlist> playlists2 = daoP.pesquisarPorUsuario(u2.getIdUsu());
                for (Playlist p: playlists2){
                    daoP.remover(p.getNomeLista(), u2.getIdUsu());
                    daoP.inserir(p, u3);
                }
            }catch (PlaylistNaoExistente | PlaylistExistente e2){
                e2.printStackTrace();
            }
        }
        remover(u1.getIdUsu());
        remover(u2.getIdUsu());
    }
}
