package lucasgennari.br.ponto.comm;

import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import lucasgennari.br.ponto.util.PropUtils;

/**
 * Created by Lucas.Gennari on 22/09/2016.
 * Gerenciador de conexões com o servidor do ponto
 */
public class HttpRetriever {

    private static HttpRetriever instance = new HttpRetriever();

    private final Properties properties = PropUtils.getInstance();

    public static HttpRetriever getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new HttpRetriever();
            return instance;
        }
    }

    public String getLogin(String login, String password) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URI uri = new URI(properties.getProperty("protocol"),
                    properties.getProperty("server") + ":" + properties.getProperty("port"),
                    "/" + properties.getProperty("context") + "/" + properties.getProperty("pathLogin") + "?login="
                            + login + "&passwd=" + password,
                    null, null);

            String urlStr = URLDecoder.decode(uri.toString(), "UTF-8");

            System.out.println("Requisicao a ser enviada: [" + urlStr + "]");
            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            String txt = readStream(con.getInputStream());
            System.out.println("Text retornado pelo servidor: " + txt);

            return txt;
        } catch (Exception e) {
            System.out.println("ERRO ao tentar logar - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getPontoDia(String cpf, String date) {
        try {
            URI uri = new URI(properties.getProperty("protocol"),
                    properties.getProperty("server") + ":" + properties.getProperty("port"),
                    "/" + properties.getProperty("context") + "/" + properties.getProperty("pathPonto") + "?cpf="
                            + cpf + "&tipo=1&date=" + date,
                    null, null);

            String urlStr = URLDecoder.decode(uri.toString(), "UTF-8");

            System.out.println("Requisicao a ser enviada: [" + urlStr + "]");
            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            String txt = readStream(con.getInputStream());
            System.out.println("Texto retornado pelo servidor: " + txt);

            return txt;
        } catch (Exception e) {

            return null;
        }

    }

    public String getPontoSemana(String cpf, String firstDay, String lastDay) {
        try {
            URI uri = new URI(properties.getProperty("protocol"),
                    properties.getProperty("server") + ":" + properties.getProperty("port"),
                    "/" + properties.getProperty("context") + "/" + properties.getProperty("pathPonto") + "?cpf="
                            + cpf + "&tipo=2&datestart=" + firstDay + "&dateend=" + lastDay,
                    null, null);

            String urlStr = URLDecoder.decode(uri.toString(), "UTF-8");

            System.out.println("Requisicao a ser enviada: [" + urlStr + "]");
            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            String txt = readStream(con.getInputStream());
            System.out.println("Texto retornado pelo servidor: " + txt);

            return txt;
        } catch (Exception e) {

            return null;
        }

    }

    public String getPontoMes(String cpf, String firstDay, String lastDay, int diasUteis) {
        try {
            URI uri = new URI(properties.getProperty("protocol"),
                    properties.getProperty("server") + ":" + properties.getProperty("port"),
                    "/" + properties.getProperty("context") + "/" + properties.getProperty("pathPonto") + "?cpf="
                            + cpf + "&tipo=3&datestart=" + firstDay + "&dateend=" + lastDay + "&diasuteis=" + diasUteis,
                    null, null);

            String urlStr = URLDecoder.decode(uri.toString(), "UTF-8");

            System.out.println("Requisicao a ser enviada: [" + urlStr + "]");
            URL url = new URL(urlStr);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            String txt = readStream(con.getInputStream());
            System.out.println("Texto retornado pelo servidor: " + txt);

            return txt;
        } catch (Exception e) {

            return null;
        }

    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        String out = "";
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                out += line;
            }
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
