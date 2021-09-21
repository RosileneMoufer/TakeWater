package com.romoufer.takewater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.romoufer.takewater.AdsConfiguracoes.GeneralConfigs;
import com.romoufer.takewater.AdsConfiguracoes.MetodosDoBillingClient;
import com.romoufer.takewater.listadapter.ListAdapterWater;
import com.romoufer.takewater.listadapter.ListModelWater;
import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;
import com.romoufer.takewater.water.Water;
import com.romoufer.takewater.water.WaterDAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SeeStatisticsActivity extends AppCompatActivity {

    ImageButton btnComeBackToMainActivity;
    Spinner spinnerPeriodoEstatistica;

    ListAdapterWater listAdapter;
    ArrayList<ListModelWater> listModels;
    RecyclerView recyclerView;
    LinearLayout linearLayoutList;
    ConstraintLayout constraintLayoutAllStatistic;

    List<Water> waterList;
    WaterDAO waterDAO;

    float media;
    float soma;
    TextView textViewMedia;
    TextView textViewTotal;

    ConstraintLayout constraintLayoutRootStatisticsActivity;

    AdView adView;
    LinearLayout linearLayoutAds;

    ImageButton btnUpgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        linearLayoutAds = findViewById(R.id.linearlayout_ads);
        adView = (AdView) findViewById(R.id.adView);
        btnUpgrade = findViewById(R.id.btnUpgrade);

        recyclerView = findViewById(R.id.recycler_view_statistics);
        linearLayoutList = findViewById(R.id.linear_layout_list_statistic);
        btnComeBackToMainActivity = findViewById(R.id.btnComeBackToMainActivity);
        spinnerPeriodoEstatistica = findViewById(R.id.spinner_periodo_estatistica);
        constraintLayoutAllStatistic = findViewById(R.id.linear_layout_root_statistics_activity);
        textViewMedia = findViewById(R.id.textview_media);
        textViewTotal = findViewById(R.id.textview_total);

        waterDAO = new WaterDAO(this);
        waterList = new ArrayList<>();

        MetodosDoBillingClient.getInstance(SeeStatisticsActivity.this, SeeStatisticsActivity.this).setupBillingClient();

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

        ArrayAdapter<CharSequence> arrayAdapterPeriodoEstatistica = ArrayAdapter.createFromResource(SeeStatisticsActivity.this, R.array.periodo_estatistica, android.R.layout.simple_spinner_item);
        arrayAdapterPeriodoEstatistica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriodoEstatistica.setAdapter(arrayAdapterPeriodoEstatistica);
        spinnerPeriodoEstatistica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String[] datePeriodo;

                switch (position) {
                    case 0:
                        datePeriodo = getDateString(2);
                        changeInforLayout(datePeriodo[0]);

                        break;
                    case 1:
                        datePeriodo = getDateString(6);
                        changeInforLayout(datePeriodo[0]);

                        break;
                    case 2:
                        datePeriodo = getDateString(29);
                        changeInforLayout(datePeriodo[0]);

                        break;
                    case 3:
                        datePeriodo = getDateString(89);
                        changeInforLayout(datePeriodo[0]);

                        break;
                    case 4:
                        datePeriodo = getDateString(179);
                        changeInforLayout(datePeriodo[0]);

                        break;
                    case 5:
                        datePeriodo = getDateString(364);
                        changeInforLayout(datePeriodo[0]);

                        break;
                    default:
                        datePeriodo = getDateString(2);
                        changeInforLayout(datePeriodo[0]);

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnComeBackToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainActivity = new Intent(SeeStatisticsActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
                finish();
            }
        });

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verfica se app está na versão paga
                if (GeneralConfigs.getInstance().isVersaoPaga()) {
                    Toast.makeText(SeeStatisticsActivity.this, "Your app is in the PRO version.", Toast.LENGTH_SHORT).show();
                } else {

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    linearLayoutAds = (LinearLayout) layoutInflater.inflate(R.layout.activity_version_pro_announcement, constraintLayoutAllStatistic, false);
                    constraintLayoutAllStatistic.addView(linearLayoutAds);

                    ImageButton btnCloseBuyNow = (ImageButton) findViewById(R.id.btn_close_buy_now);
                    Button btnBuyNow = (Button) findViewById(R.id.btn_buy_now);
                    TextView txtPriceAppValue = (TextView) findViewById(R.id.propaganda_preco);

                    txtPriceAppValue.setText(GeneralConfigs.getInstance().getPrecoDaVersaoPaga());

                    btnCloseBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            constraintLayoutAllStatistic.removeView(linearLayoutAds);
                        }
                    });

                    btnBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // efetuar uma compra

                            MetodosDoBillingClient.getInstance(SeeStatisticsActivity.this, SeeStatisticsActivity.this).loadPurchases();

                        }
                    });

                }
            }
        });
    }

    public void changeInforLayout(String minDate) {

        // lá no DAO quando chegar na min date pega os valores dela e dá um break. A chamada é decrescente
        // então dá certo desse jeito.

        LoadingDialog loadingDialog = new LoadingDialog(SeeStatisticsActivity.this);

        LinearLayoutManager linearLayoutManager;

        waterList = waterDAO.getWaterIntervalDate(minDate);
        listModels = new ArrayList<>();

        linearLayoutList.removeAllViews();

        if (waterList.size() == 0) {
            Toast.makeText(SeeStatisticsActivity.this, getResources().getString(R.string.there_is_no_data), Toast.LENGTH_SHORT).show();
        } else {
            loadingDialog.startLoadingDialog();

            linearLayoutList.addView(recyclerView);

            linearLayoutManager = new LinearLayoutManager(SeeStatisticsActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            media = 0f;
            soma = 0f;

            for (int i = 0; i < waterList.size(); i++) {
                media = media + waterList.get(i).getMl();

                ListModelWater listModel = new ListModelWater(waterList.get(i).getId(), waterList.get(i).getDate(), waterList.get(i).getHour(), waterList.get(i).getMl());

                listModels.add(listModel);
                listAdapter = new ListAdapterWater(this, constraintLayoutAllStatistic, listModels);
                recyclerView.setAdapter(listAdapter);
            }

            soma = media;
            media = media / waterList.size();

            textViewMedia.setText(getResources().getString(R.string.average) + ": " + media + " ML");
            textViewTotal.setText(getResources().getString(R.string.total) + ": " + soma + " ML");

            loadingDialog.dismissLoadingDialog();
        }

    }

    private String[] getDateString(int min) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -min);

        String[] result = new String[2];
        String data_hour = dateFormat.format(cal.getTime());
        result = data_hour.split(" ");

        return result;
    }
}