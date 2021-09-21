package com.romoufer.takewater;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.romoufer.takewater.AdsConfiguracoes.GeneralConfigs;
import com.romoufer.takewater.AdsConfiguracoes.MetodosDoBillingClient;
import com.romoufer.takewater.listadapter.ListAdapterWater;
import com.romoufer.takewater.listadapter.ListModelWater;
import com.romoufer.takewater.singleton.Alarm;
import com.romoufer.takewater.water.Water;
import com.romoufer.takewater.water.WaterDAO;
import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;

    int section = 0;

    Calendar currentTime;

    Button btnToday;
    Button btnYestarday;
    Button btnAll;
    Button btnSeeStatistics;

    ImageButton btnSettings;
    ImageButton btnAddMoreWater;

    TextView textViewTextDescription;

    ListAdapterWater listAdapter;
    ArrayList<ListModelWater> listModels;
    RecyclerView recyclerView;
    LinearLayout linearLayoutList;

    List<Water> waterList;
    List<SettingsApp> settingsList;

    WaterDAO waterDAO;
    SettingsDAO settingsDAO;

    AdView adView;
    LinearLayout linearLayoutAds;

    ImageButton btnUpgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayoutAds = findViewById(R.id.linearlayout_ads);
        adView = (AdView) findViewById(R.id.adView);
        btnUpgrade = findViewById(R.id.btnUpgrade);

        constraintLayout = findViewById(R.id.layout_main_activity);

        waterDAO = new WaterDAO(this);
        settingsDAO = new SettingsDAO(this);
        settingsList = settingsDAO.obterTodos();

        recyclerView = findViewById(R.id.recycler_view);

        currentTime = Calendar.getInstance();

        btnToday = findViewById(R.id.btn_today);
        btnYestarday = findViewById(R.id.btn_yestarday);
        btnAll = findViewById(R.id.btn_all);
        btnSeeStatistics = findViewById(R.id.btn_see_statistics);
        btnAddMoreWater = findViewById(R.id.btn_add_more_water);

        btnSettings = findViewById(R.id.btnSettings);

        linearLayoutList = findViewById(R.id.linear_layout_list);

        textViewTextDescription = findViewById(R.id.text_description);

        MetodosDoBillingClient.getInstance(MainActivity.this, MainActivity.this).setupBillingClient();

        // verifica se app está na versão paga
        if (GeneralConfigs.getInstance().isVersaoPaga()) {
            linearLayoutAds.setEnabled(false);
        } else {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        if ( settingsList.size() == 0 ) {
            settingsDAO.inserir();
            Alarm.getInstance().ativarAlarme(this);
        }

        changeInforLayout();

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                section = 0;

                changeInforLayout();
            }
        });

        btnYestarday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                section = 1;

                changeInforLayout();
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                section = 2;

                changeInforLayout();
            }
        });

        btnSeeStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SeeStatisticsActivity.class);
                startActivity(i);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSetting);
            }
        });

        btnAddMoreWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsDAO settingsDAO = new SettingsDAO(MainActivity.this);
                List<SettingsApp> listSettingsApp = settingsDAO.obterTodos();

                int valueML = 0;

                for (int i = 0; i < listSettingsApp.size(); i++) {
                    valueML = listSettingsApp.get(i).getValueML();
                }

                String[] dateHour = getDateString();

                WaterDAO waterDAO = new WaterDAO(MainActivity.this);
                Water water = new Water();
                water.setDate(dateHour[0]);
                water.setHour(dateHour[1]);
                water.setMl(valueML);
                waterDAO.inserir(water);

                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verfica se app está na versão paga
                if (GeneralConfigs.getInstance().isVersaoPaga()) {
                    Toast.makeText(MainActivity.this, "Your app is in the PRO version.", Toast.LENGTH_SHORT).show();
                } else {

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    linearLayoutAds = (LinearLayout) layoutInflater.inflate(R.layout.activity_version_pro_announcement, constraintLayout, false);
                    constraintLayout.addView(linearLayoutAds);

                    ImageButton btnCloseBuyNow = (ImageButton) findViewById(R.id.btn_close_buy_now);
                    Button btnBuyNow = (Button) findViewById(R.id.btn_buy_now);
                    TextView txtPriceAppValue = (TextView) findViewById(R.id.propaganda_preco);

                    txtPriceAppValue.setText(GeneralConfigs.getInstance().getPrecoDaVersaoPaga());

                    btnCloseBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            constraintLayout.removeView(linearLayoutAds);
                        }
                    });

                    btnBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // efetuar uma compra

                            MetodosDoBillingClient.getInstance(MainActivity.this, MainActivity.this).loadPurchases();

                        }
                    });

                }
            }
        });

    }

    public void changeInforLayout() {
        stateButtonMenu(section);

        LinearLayoutManager linearLayoutManager;

        switch (section) {
            case 0:
                String[] dateToday = getTodayDateString();

                textViewTextDescription.setText(getResources().getString(R.string.every_time_you_drank_water_today) + ", " + dateToday[0]);

                waterList = waterDAO.getWaterThroughTheDate(dateToday[0]);
                listModels = new ArrayList<>();

                linearLayoutList.removeAllViews();

                if ( waterList.size() == 0 ) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.there_is_no_data), Toast.LENGTH_SHORT).show();
                } else {
                    linearLayoutList.addView(recyclerView);

                    linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    for (int i = 0; i < waterList.size(); i++) {
                        ListModelWater listModel = new ListModelWater(waterList.get(i).getId(), waterList.get(i).getDate(), waterList.get(i).getHour(), waterList.get(i).getMl());

                        listModels.add(listModel);
                        listAdapter = new ListAdapterWater(this, constraintLayout, listModels);
                        recyclerView.setAdapter(listAdapter);
                    }
                }

                break;
            case 1:
                String[] dateYestarday = getYesterdayDateString();

                textViewTextDescription.setText(getResources().getString(R.string.every_time_you_drank_water_yestarday) + ", " + dateYestarday[0]);

                linearLayoutList.removeAllViews();

                waterList = null;
                waterList = waterDAO.getWaterThroughTheDate(dateYestarday[0]);

                if ( waterList.size() == 0 ) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.there_is_no_data), Toast.LENGTH_SHORT).show();
                } else {
                    linearLayoutList.addView(recyclerView);

                    linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    listModels = new ArrayList<>();

                    for (int i = 0; i < waterList.size(); i++) {
                        ListModelWater listModel = new ListModelWater(waterList.get(i).getId(), waterList.get(i).getDate(), waterList.get(i).getHour(), waterList.get(i).getMl());

                        listModels.add(listModel);
                        listAdapter = new ListAdapterWater(this, constraintLayout, listModels);
                        recyclerView.setAdapter(listAdapter);
                    }
                }

                break;
            case 2:
                textViewTextDescription.setText(getResources().getString(R.string.every_time_you_drank_water));

                linearLayoutList.removeAllViews();

                waterList = waterDAO.obterTodos();

                if ( waterList.size() == 0 ) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.there_is_no_data), Toast.LENGTH_SHORT).show();
                } else {
                    linearLayoutList.addView(recyclerView);

                    linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    listModels = new ArrayList<>();

                    for (int i = 0; i < waterList.size(); i++) {
                        ListModelWater listModel = new ListModelWater(waterList.get(i).getId(), waterList.get(i).getDate(), waterList.get(i).getHour(), waterList.get(i).getMl());

                        listModels.add(listModel);
                        listAdapter = new ListAdapterWater(this, constraintLayout, listModels);
                        recyclerView.setAdapter(listAdapter);
                    }
                }

                break;
            default:
                break;
        }

    }

    public void stateButtonMenu(int optionSelect) {
        switch (optionSelect) {
            case 0:
                btnToday.setActivated(true);
                btnYestarday.setActivated(false);
                btnAll.setActivated(false);

                break;
            case 1:
                btnToday.setActivated(false);
                btnYestarday.setActivated(true);
                btnAll.setActivated(false);

                break;
            case 2:
                btnToday.setActivated(false);
                btnYestarday.setActivated(false);
                btnAll.setActivated(true);

                break;
            default:
                break;
        }
    }

    private String[] getTodayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        String[] result;
        String data_hour = dateFormat.format(cal.getTime());
        result = data_hour.split(" ");

        return result;
    }

    private String[] getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        String[] result;
        String data_hour = dateFormat.format(cal.getTime());
        result = data_hour.split(" ");

        return result;
    }

    private String[] getDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        String[] result;
        String data_hour = dateFormat.format(cal.getTime());
        result = data_hour.split(" ");

        return result;
    }

}