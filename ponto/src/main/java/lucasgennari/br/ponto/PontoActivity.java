package lucasgennari.br.ponto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import lucasgennari.br.ponto.comm.HttpRetriever;
import lucasgennari.br.ponto.util.PropUtils;

/**
 * Created by Lucas.Gennari on 22/09/2016.
 * Classe que administra a tela de login e identifica
 * se o login foi feito por um usuário comum ou por
 * um administrador do sistema
 */
public class PontoActivity extends AppCompatActivity {

    private static final String SPF_NAME = "pontoLogin";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private EditText emailLogin;
    private EditText paswdLogin;
    private SwitchCompat rememberMe;
    private String cpfUser;
    private String nomeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        PropUtils.loadProps(this);

        Button loginButton = (Button) findViewById(R.id.btnLogin);
        emailLogin = (EditText) findViewById(R.id.email);
        paswdLogin = (EditText) findViewById(R.id.password);
        rememberMe = (SwitchCompat) findViewById(R.id.switch_remember);

        SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        if (!loginPreferences.getString(USERNAME, "").contentEquals("")) {
            rememberMe.setChecked(true);
        }
        emailLogin.setText(loginPreferences.getString(USERNAME, ""));
        paswdLogin.setText(loginPreferences.getString(PASSWORD, ""));

        loginButton.setOnClickListener(loginListener());
    }

    private OnClickListener loginListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getCallingPackage(), "Email [" + emailLogin.getText().toString() + "] - Senha ["
                        + paswdLogin.getText().toString() + "]");
                fetchLogin(emailLogin.getText().toString(), paswdLogin.getText().toString());
            }
        };
    }

    private void fetchLogin(String login, String password) {
        try {
            String returnLogin = HttpRetriever.getInstance().getLogin(login, password);
            System.out.println(returnLogin);
            String[] firstSplit = returnLogin.replace("\"", "").replace("}", "").split(",");
            String[] cpfSplit = firstSplit[0].split(":");
            String[] nomeSplit = firstSplit[1].split(":");
            String[] perfilSplit = firstSplit[2].split(":");

            cpfUser = cpfSplit[1];
            nomeUser = nomeSplit[1];
            String perfilUser = perfilSplit[1];

            if (!cpfUser.isEmpty()) {
                if (rememberMe.isChecked()) {
                    SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
                    loginPreferences.edit().putString(USERNAME, login).putString(PASSWORD, password).apply();
                }
                System.out.println("CPF: " + cpfUser);
                System.out.println("Nome: " + nomeUser);
                System.out.println("Perfil: " + perfilUser);

                if (perfilUser.equals("USR")) {
                    System.out.println("Perfil de Usuario");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(PontoActivity.this, UserCalendar.class);
                            Bundle extra = new Bundle();
                            extra.putString("cpf", cpfUser);
                            extra.putString("nome", nomeUser);
                            mainIntent.putExtras(extra);
                            PontoActivity.this.startActivity(mainIntent);
                        }
                    });
                } else if (perfilUser.equals("ADM")) {
                    System.out.println("Perfil de Administrador");
                }
            } else {
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
