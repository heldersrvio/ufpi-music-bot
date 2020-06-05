package prova3.helderServio;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;



import java.sql.SQLException;
import java.util.ArrayList;

public class UFPIMusicBot extends TelegramLongPollingBot {

    private String s;
    private DAOPlaylist daoP;
    private DAOUsuario daoU;
    private DAOMusica daoM;
    private Usuario usu;
    private int flag;
    private Playlist my_pl;
    private String song_name;
    private String artist;
    private String name;
    private String menu;
    private WitClient client;

    @Override
    public String getBotUsername() {
        return "ufpimusic2019bot";
    }

    @Override
    public void onUpdateReceived(Update u) {
        SendMessage send = new SendMessage();
        long chat_id = u.getMessage().getChatId();
        String nome = u.getMessage().getFrom().getFirstName();
        send.setChatId(u.getMessage().getChatId());
        send.setChatId(chat_id);
        String message = u.getMessage().getText();
        String intent = client.getIntentViaText(message,null,null,null,null).getOutcomes().get(0).getEntities().firstEntityValue("intent");
        String userID = client.getIntentViaText(message,null,null,null,null).getOutcomes().get(0).getEntities().firstEntityValue("userID");
        String artistID = client.getIntentViaText(message,null,null,null,null).getOutcomes().get(0).getEntities().firstEntityValue("artistID");
        String playlist_name = client.getIntentViaText(message,null,null,null,null).getOutcomes().get(0).getEntities().firstEntityValue("playlist_name");
        String songname = client.getIntentViaText(message,null,null,null,null).getOutcomes().get(0).getEntities().firstEntityValue("songname");
        String greeting = client.getIntentViaText(message,null,null,null,null).getOutcomes().get(0).getEntities().firstEntityValue("wit/greetings");
        System.out.println(intent);
        if (message.equals("/start")){
            send.setChatId(chat_id);
            //send.setText("Hello, I'm UFPIMusicBot. I'm here to help you handle your playlists. Please select an option.\n1- Create playlist\n2- Alter existing playlist\n3- Delete existing playlist\n4- Delete song from playlist\n5- Add song to playlist ");
            send.setText("Hello, I'm UFPIMusicBot. I'm here to help you handle your playlists. Please type your user ID.");
            s = send.getText();
            try {
                execute(send); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println(s);
            if (intent != null && intent.equals("back to menu")) {
                send.setText(menu);
                s = send.getText();
                try{
                    execute(send);
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }else if (s.equals("Hello, I'm UFPIMusicBot. I'm here to help you handle your playlists. Please type your user ID.")){
                try {
                    if (userID != null)
                        usu = daoU.pesquisarPor(userID);
                    else
                        usu = daoU.pesquisarPor(message);
                    send.setChatId(chat_id);
                    send.setText("Found user ID\n" + menu);
                    s = menu;
                } catch (UsuarioNaoCadastrado usuarioNaoCadastrado) {
                    send.setChatId(chat_id);
                    send.setText("Sorry, but this ID is not registered. Please type a different ID.");
                }
                try {
                    execute(send); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if (s.equals(menu)){
                if (intent != null && intent.equals("playlist creation")){
                    if (playlist_name == null) {
                        send.setText("Type the name of your new playlist.");
                        s = send.getText();
                    }else{
                        Playlist p = new Playlist(playlist_name);
                        try {
                            daoP.inserir(p, usu);
                            send.setText("New playlist successfully created\n" + menu);
                            s = menu;
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (PlaylistExistente playlistExistente) {
                            send.setText("There is already a playlist with this name. Please choose another name.");
                        }
                    }
                }else if (intent != null && intent.equals("playlist deletion")){
                    flag = 0;
                    if (playlist_name == null) {
                        send.setText("Type the name of the playlist.");
                        s = send.getText();
                    }else{
                        try {
                            my_pl = daoP.pesquisarPor(playlist_name, usu.getIdUsu());
                            daoP.remover(playlist_name, usu.getIdUsu());
                            send.setText("Playlist successfully removed\n" + menu);
                            s = menu;
                        } catch (PlaylistNaoExistente playlistNaoExistente) {
                            send.setText("Non-existent playlist. Please type the name of the playlist again.");
                        } catch (SQLException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }else if (intent != null && (intent.equals("song deletion") || intent.equals("song addition"))){
                    if (intent.equals("song deletion"))
                        flag = 1;
                    else
                        flag = 2;
                    if (playlist_name == null) {
                        send.setText("Type the name of the playlist.");
                        s = send.getText();
                    }else{
                        try {
                            my_pl = daoP.pesquisarPor(playlist_name, usu.getIdUsu());
                            if (songname == null) {
                                send.setText("What's the name of the song?");
                                s = send.getText();
                            }else{
                                song_name = songname;
                                if (artistID == null) {
                                    send.setText("What's the artist's ID?");
                                    s = send.getText();
                                }else{
                                    int flag2 = 0;
                                    artist = artistID;
                                    Musica nm = null;
                                    try {
                                        ArrayList<Musica> a_m = my_pl.getMusicas();
                                        nm = daoM.pesquisarPor(song_name, artist);
                                        if (flag == 1) {
                                            for (Musica m : a_m) {
                                                if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                                    flag2 = 1;
                                                    a_m.remove(m);
                                                    try {
                                                        daoP.alterar(my_pl, usu);
                                                    } catch (ClassNotFoundException | SQLException e) {
                                                        e.printStackTrace();
                                                    }
                                                    send.setText("Song successfully deleted\n" + menu);
                                                    s = menu;
                                                    break;
                                                }
                                            }
                                            if (flag2 == 0) {
                                                send.setText("Song not found. Type its name and the artist's ID again.");
                                                s = "What's the name of the song?";
                                            }
                                        }else if (flag == 2){
                                            for (Musica m : a_m) {
                                                if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                                    flag2 = 1;
                                                    send.setText("Song already on playlist\nPlease select an option.\n1- Create playlist\n2- Delete existing playlist\n3- Delete song from playlist\n4- Add song to playlist\n5- Alter the name of a playlist");
                                                    s = menu;
                                                }
                                            }
                                            if (flag2 == 0) {
                                                my_pl.getMusicas().add(nm);
                                                try {
                                                    daoP.alterar(my_pl, usu);
                                                } catch (ClassNotFoundException | SQLException e) {
                                                    e.printStackTrace();
                                                }
                                                send.setText("Song successfully added to playlist\n" + menu);
                                                s = menu;
                                            }
                                        }
                                    } catch (MusicaNaoCadastrada musicaNaoCadastrada) {
                                        send.setText("Song not found in the library. Type its name and the artist's ID again.");
                                        s = "What's the name of the song?";
                                    }
                                }
                            }
                        } catch (PlaylistNaoExistente playlistNaoExistente) {
                            send.setText("Non-existent playlist. Please type the name of the playlist again.");
                        }
                    }
                }else if (intent != null && intent.equals("playlist name change")) {
                    flag = 3;
                    if (playlist_name == null) {
                        send.setText("Type the name of the playlist.");
                        s = send.getText();
                    }else{
                        try {
                            my_pl = daoP.pesquisarPor(playlist_name, usu.getIdUsu());
                            send.setText("What do you wish to call " + playlist_name + " from now on?");
                            s = "What do you wish to call...";
                        } catch (PlaylistNaoExistente playlistNaoExistente) {
                            send.setText("Non-existent playlist. Please type the name of the playlist again.");
                        }
                    }
                }else if (intent != null && intent.equals("quit")){
                    send.setText("Hello, I'm UFPIMusicBot. I'm here to help you handle your playlists. Please type your user ID.");
                    s = send.getText();
                }else{
                    send.setText("Sorry, can you phrase that a bit better? I can't understand you.");
                }
                try{
                    execute(send);
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }else if (s.equals("Type the name of your new playlist.")){
                Playlist p;
                if (playlist_name == null)
                    p = new Playlist(message);
                else
                    p = new Playlist(playlist_name);
                try {
                    daoP.inserir(p, usu);
                    send.setText("New playlist successfully created\n" + menu);
                    s = menu;
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (PlaylistExistente playlistExistente) {
                    send.setText("There is already a playlist with this name. Please choose another name.");
                }
                try {
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if (s.equals("Type the name of the playlist.")){
                if (playlist_name == null)
                    name = message;
                else
                    name = playlist_name;
                try {
                    my_pl = daoP.pesquisarPor(name, usu.getIdUsu());
                    if (flag == 0) {
                        daoP.remover(name, usu.getIdUsu());
                        send.setText("Playlist successfully deleted\n" + menu);
                        s = menu;
                    }else if (flag == 1 || flag == 2){
                        if (songname == null) {
                            send.setText("What's the name of the song?");
                            s = send.getText();
                        }else{
                            song_name = songname;
                            if (artistID == null) {
                                send.setText("What's the artist's ID?");
                                s = send.getText();
                            }else{
                                int flag2 = 0;
                                artist = artistID;
                                Musica nm = null;
                                try {
                                    ArrayList<Musica> a_m = my_pl.getMusicas();
                                    nm = daoM.pesquisarPor(song_name, artist);
                                    if (flag == 1) {
                                        for (Musica m : a_m) {
                                            if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                                flag2 = 1;
                                                a_m.remove(m);
                                                try {
                                                    daoP.alterar(my_pl, usu);
                                                } catch (ClassNotFoundException | SQLException e) {
                                                    e.printStackTrace();
                                                }
                                                send.setText("Song successfully deleted\n" + menu);
                                                s = menu;
                                                break;
                                            }
                                        }
                                        if (flag2 == 0) {
                                            send.setText("Song not found. Type its name and the artist's ID again.");
                                            s = "What's the name of the song?";
                                        }
                                    }else if (flag == 2){
                                        for (Musica m : a_m) {
                                            if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                                flag2 = 1;
                                                send.setText("Song already on playlist\nPlease select an option.\n1- Create playlist\n2- Delete existing playlist\n3- Delete song from playlist\n4- Add song to playlist\n5- Alter the name of a playlist");
                                                s = menu;
                                            }
                                        }
                                        if (flag2 == 0) {
                                            my_pl.getMusicas().add(nm);
                                            try {
                                                daoP.alterar(my_pl, usu);
                                            } catch (ClassNotFoundException | SQLException e) {
                                                e.printStackTrace();
                                            }
                                            send.setText("Song successfully added\n" + menu);
                                            s = menu;
                                        }
                                    }
                                } catch (MusicaNaoCadastrada musicaNaoCadastrada) {
                                    send.setText("Song not found in the library. Type its name and the artist's ID again.");
                                    s = "What's the name of the song?";
                                }
                            }
                        }
                    }else if (flag == 3){
                        send.setText("What do you wish to call " + name + " from now on?");
                        s = "What do you wish to call...";
                    }
                }catch (PlaylistNaoExistente playlistNaoExistente) {
                    send.setText("Non-existent playlist. Please type the name of the playlist again.");
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if (s.equals("What's the name of the song?")){
                song_name = message;
                if (artistID == null) {
                    send.setText("What's the artist's ID?");
                    s = send.getText();
                }else{
                    int flag2 = 0;
                    artist = artistID;
                    Musica nm = null;
                    try {
                        ArrayList<Musica> a_m = my_pl.getMusicas();
                        nm = daoM.pesquisarPor(song_name, artist);
                        if (flag == 1) {
                            for (Musica m : a_m) {
                                if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                    flag2 = 1;
                                    a_m.remove(m);
                                    try {
                                        daoP.alterar(my_pl, usu);
                                    } catch (ClassNotFoundException | SQLException e) {
                                        e.printStackTrace();
                                    }
                                    send.setText("Song successfully deleted\n" + menu);
                                    s = menu;
                                    break;
                                }
                            }
                            if (flag2 == 0) {
                                send.setText("Song not found. Type its name and the artist's ID again.");
                                s = "What's the name of the song?";
                            }
                        }else if (flag == 2){
                            for (Musica m : a_m) {
                                if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                    flag2 = 1;
                                    send.setText("Song already on playlist\nPlease select an option.\n1- Create playlist\n2- Delete existing playlist\n3- Delete song from playlist\n4- Add song to playlist\n5- Alter the name of a playlist");
                                    s = menu;
                                }
                            }
                            if (flag2 == 0) {
                                my_pl.getMusicas().add(nm);
                                try {
                                    daoP.alterar(my_pl, usu);
                                } catch (ClassNotFoundException | SQLException e) {
                                    e.printStackTrace();
                                }
                                send.setText("Song successfully added\n" + menu);
                                s = menu;
                            }
                        }
                    } catch (MusicaNaoCadastrada musicaNaoCadastrada) {
                        send.setText("Song not found in the library. Type its name and the artist's ID again.");
                        s = "What's the name of the song?";
                    }
                }
                try{
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if (s.equals("What do you wish to call...")){
                try {
                    daoP.remover(my_pl.getNomeLista(), usu.getIdUsu());
                    my_pl.setNomeLista(message);
                    daoP.inserir(my_pl, usu);
                    send.setText("The name of your playlist has been changed as requested\n" + menu);
                    s = menu;
                } catch (SQLException | ClassNotFoundException | PlaylistExistente e) {
                    e.printStackTrace();
                }
                try{
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if (s.equals("What's the artist's ID?")){
                int flag2 = 0;
                artist = u.getMessage().getText();
                Musica nm = null;
                try {
                    ArrayList<Musica> a_m = my_pl.getMusicas();
                    nm = daoM.pesquisarPor(song_name, artist);
                    if (flag == 1) {
                        for (Musica m : a_m) {
                            if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                flag2 = 1;
                                a_m.remove(m);
                                try {
                                    daoP.alterar(my_pl, usu);
                                } catch (ClassNotFoundException | SQLException e) {
                                    e.printStackTrace();
                                }
                                send.setText("Song successfully deleted\n" + menu);
                                s = menu;
                                break;
                            }
                        }
                        if (flag2 == 0) {
                            send.setText("Song not found. Type its name and the artist's ID again.");
                            s = "What's the name of the song?";
                        }
                    }else if (flag == 2){
                        for (Musica m : a_m) {
                            if (m.getNomeMusica().equals(nm.getNomeMusica()) && m.getIdArtista().equals(nm.getIdArtista())) {
                                flag2 = 1;
                                send.setText("Song already on playlist\nPlease select an option.\n1- Create playlist\n2- Delete existing playlist\n3- Delete song from playlist\n4- Add song to playlist\n5- Alter the name of a playlist");
                                s = menu;
                            }
                        }
                        if (flag2 == 0) {
                            my_pl.getMusicas().add(nm);
                            try {
                                daoP.alterar(my_pl, usu);
                            } catch (ClassNotFoundException | SQLException e) {
                                e.printStackTrace();
                            }
                            send.setText("Song successfully added\n" + menu);
                            s = menu;
                        }
                    }
                } catch (MusicaNaoCadastrada musicaNaoCadastrada) {
                    send.setText("Song not found in the library. Type its name and the artist's ID again.");
                    s = "What's the name of the song?";
                }
                try{
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else{
                send.setText("Sorry, can you phrase that a bit better? I can't understand you.");
                s = menu;
                try{
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(u.getMessage());
//		System.out.println(u.getMessage().getContact().getPhoneNumber());
    }

    @Override
    public String getBotToken() {
        // Token gerado na criação do bot
        return APIKey;
    }

    public UFPIMusicBot(){
        daoP = new DAOPlaylist();
        daoU = new DAOUsuario();
        daoM = new DAOMusica();
        usu = null;
        flag = 0;
        menu = "Please select an option.\n1- Create playlist\n2- Delete existing playlist\n3- Delete song from playlist\n4- Add song to playlist\n5- Alter the name of a playlist\n\n(Reminder: you can always type MENU if you want to see the options again)";
        client = new WitClient("KE25O2D3NOT3ZKFD4UT2YY3RGFG6NKJY");
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBot = new TelegramBotsApi();
        UFPIMusicBot bot = new UFPIMusicBot();

        try {
            telegramBot.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}


