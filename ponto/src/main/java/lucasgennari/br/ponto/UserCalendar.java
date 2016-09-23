package lucasgennari.br.ponto;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lucasgennari.br.ponto.comm.HttpRetriever;

/**
 * Created by Lucas.Gennari on 22/09/2016.
 * Classe que manipula a view do calendário utilizada
 * pelo usuário
 */
@SuppressWarnings("deprecation")
@SuppressLint("SimpleDateFormat")
public class UserCalendar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String cpf;
    private String nome;
    private Date date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            cpf = extra.getString("cpf");
            nome = extra.getString("nome");
            System.out.println("Extra - Cpf [" + cpf + "]");
            System.out.println("Extra - Nome [" + nome + "]");
        }

        TextView titulo = (TextView) findViewById(R.id.welcome_user);
        CalendarView calendarView = (CalendarView) findViewById(R.id.userCalendarView);
        Button totalHorasSemana = (Button) findViewById(R.id.btnHorasSemana);
        Button totalHorasMes = (Button) findViewById(R.id.btnHorasMes);

        titulo.setText(getString(R.string.welcome, nome));

        date = new Date();

        calendarView.setOnDateChangeListener(dateChangeListener());
        totalHorasSemana.setOnClickListener(horasSemanaListener());
        totalHorasMes.setOnClickListener(horasMesListener());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        String title = item.getTitle().toString();
        System.out.println("Title do item selecionado [ " + title + " ]");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private CalendarView.OnDateChangeListener dateChangeListener() {
        return new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dia = (year + "-" + month + "-" + dayOfMonth);

                date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = dateFormat.parse(dia);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String pontoDia = HttpRetriever.getInstance().getPontoDia(cpf, dia);
                String[] firstSplit = pontoDia.replace("\"", "").replace("{", "").replace("}", "").split(",");
                String[] message = firstSplit[0].split(":");
                System.out.println("Mensagem: " + message[1]);
                String[] batidas = firstSplit[1].replace("message:", "").split(";");
                String totalHorasDia = firstSplit[2].replace("total_horas:", "");
                List<String> arrayList = new ArrayList<>();
                int i = 1;
                for (String batida : batidas) {
                    if (i % 2 != 0) {
                        arrayList.add(batida.replace("1-", "Entrada -    "));
                    } else {
                        arrayList.add(batida.replace("2-", "Saída     -    "));
                    }
                    i++;
                }
                arrayList.add("Total de horas - " + totalHorasDia);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserCalendar.this, R.layout.list_item, arrayList);

                new AlertDialog.Builder(UserCalendar.this).setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("DISMISS", null).setTitle("Dia " + dayOfMonth).show();
            }
        };
    }

    private View.OnClickListener horasSemanaListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar week = Calendar.getInstance();
                week.setTime(date);
                week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                String firstDayOfWeek = week.get(Calendar.YEAR) + "-" + (week.get(Calendar.MONTH) + 1) + "-" + week.get(Calendar.DATE);
                String lastDayOfWeek = "";
                for (int i = 6; i >= 0; i--) {
                    if (week.getActualMaximum(Calendar.DAY_OF_MONTH) == (week.get(Calendar.DATE) + i)) {
                        lastDayOfWeek = week.get(Calendar.YEAR) + "-" + (week.get(Calendar.MONTH) + 1) + "-" + (week.get(Calendar.DATE) + i);
                        break;
                    } else {
                        lastDayOfWeek = week.get(Calendar.YEAR) + "-" + (week.get(Calendar.MONTH) + 1) + "-" + (week.get(Calendar.DATE) + 6);
                    }
                }

                String pontoSemana = HttpRetriever.getInstance().getPontoSemana(cpf, firstDayOfWeek, lastDayOfWeek);
                String[] firstSplit = pontoSemana.replace("\"", "").replace("{", "").replace("}", "").split(",");
                String[] message = firstSplit[0].split(":");
                System.out.println("Mensagem: " + message[1]);
                String[] batidas = firstSplit[1].replace("message:", "").split(";");
                String totalHorasSemana = firstSplit[2].replace("total_horas:", "");
                List<String> arrayList = new ArrayList<>();
                arrayList.add("Horas realizadas - " + batidas[0]);
                arrayList.add("Carga horária       - " + batidas[1]);
                arrayList.add("Horas restantes  - " + totalHorasSemana.replace("-", "Faltam "));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserCalendar.this, R.layout.list_item, arrayList);

                new AlertDialog.Builder(UserCalendar.this).setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("DISMISS", null).setTitle("De " + firstDayOfWeek + " a " + lastDayOfWeek).show();
            }
        };
    }

    private View.OnClickListener horasMesListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar month = Calendar.getInstance();
                month.setTime(date);
                String firstDayOfMonth = month.get(Calendar.YEAR) + "-" + (month.get(Calendar.MONTH) + 1) + "-" + month.getActualMinimum(Calendar.DAY_OF_MONTH);
                String lastDayOfMonth = month.get(Calendar.YEAR) + "-" + (month.get(Calendar.MONTH) + 1) + "-" + month.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("Primeiro dia mes: " + firstDayOfMonth);
                System.out.println("Ultimo dia mes:   " + lastDayOfMonth);
                int diasUteis = 0;
                Date first = new Date();
                Date last = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    first = dateFormat.parse(firstDayOfMonth);
                    last = dateFormat.parse(lastDayOfMonth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(first);
                end.setTime(last);
                do {
                    if (start.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        diasUteis++;
                    }
                    start.add(Calendar.DAY_OF_MONTH, 1);
                } while (start.get(Calendar.DAY_OF_YEAR) <= end.get(Calendar.DAY_OF_YEAR));

                String pontoMes = HttpRetriever.getInstance().getPontoMes(cpf, firstDayOfMonth, lastDayOfMonth, diasUteis);
                String[] firstSplit = pontoMes.replace("\"", "").replace("{", "").replace("}", "").split(",");
                String[] message = firstSplit[0].split(":");
                System.out.println("Mensagem: " + message[1]);
                String[] batidas = firstSplit[1].replace("message:", "").split(";");
                String totalHorasSemana = firstSplit[2].replace("total_horas:", "");
                List<String> arrayList = new ArrayList<>();
                arrayList.add("Horas realizadas - " + batidas[0]);
                arrayList.add("Carga horária       - " + batidas[1]);
                arrayList.add("Horas restantes  - " + totalHorasSemana.replace("-", "Faltam "));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserCalendar.this, R.layout.list_item, arrayList);

                new AlertDialog.Builder(UserCalendar.this).setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("DISMISS", null).setTitle("De " + firstDayOfMonth + " a " + lastDayOfMonth).show();
            }
        };
    }
}
