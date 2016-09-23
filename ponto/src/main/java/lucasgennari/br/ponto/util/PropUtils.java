package lucasgennari.br.ponto.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.util.Properties;

import lucasgennari.br.ponto.R;

/**
 * Created by Lucas.Gennari on 22/09/2016.
 * Classe que carrega o arquivo de configuração da conexão
 * do aplicativo com o servidor do ponto
 */
@SuppressWarnings("serial")
public class PropUtils extends Properties {

    private static final Properties props = new Properties();

    private PropUtils() {
    }

    public static Properties getInstance() {
        return props;
    }

    public static void loadProps(Context context) {
        try {
            Resources res = context.getResources();
            props.load(res.openRawResource(R.raw.ponto));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}