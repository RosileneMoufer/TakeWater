package com.romoufer.takewater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.romoufer.takewater.AdsConfiguracoes.BillingClientSetup;
import com.romoufer.takewater.AdsConfiguracoes.GeneralConfigs;
import com.romoufer.takewater.settings.SettingsApp;
import com.romoufer.takewater.settings.SettingsDAO;
import com.romoufer.takewater.singleton.Alarm;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Switch switchDoAll;
    Switch switchOnlyNotify;
    Switch switchDoNothing;

    EditText editTextValueML;

    ImageButton btnComeBackToMainActivity;
    ImageButton btnEditValueIntervalNotification;
    ImageButton btnUpgrade;
    Button btnSaveSettings;

    SettingsDAO settingsDAO;
    List<SettingsApp> settings;

    TextView textViewIntervalNotification;

    int valueTimeInterval = 0;
    int valueML;
    String frequenciaIngestao = "0";

    ConstraintLayout constraintLayoutRootSettingsActivity;

    private BillingClient billingClient;
    AcknowledgePurchaseResponseListener listener;
    AdView adView;
    LinearLayout linearLayoutAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        linearLayoutAds = findViewById(R.id.linearlayout_ads);
        adView = (AdView) findViewById(R.id.adView);
        btnUpgrade = findViewById(R.id.btnUpgrade);

        constraintLayoutRootSettingsActivity = findViewById(R.id.layout_root_activity_settings);

        switchDoAll = findViewById(R.id.switchDoAll);
        switchOnlyNotify = findViewById(R.id.switchOnlyNotify);
        switchDoNothing = findViewById(R.id.switchDoNothing);

        editTextValueML = findViewById(R.id.edittext_value_ml);

        textViewIntervalNotification = findViewById(R.id.textview_interval_notification);

        btnComeBackToMainActivity = findViewById(R.id.btnComeBackToMainActivity);
        btnEditValueIntervalNotification = findViewById(R.id.btn_edit_interval_notification);
        btnSaveSettings = findViewById(R.id.btn_save_settings);

        settingsDAO = new SettingsDAO(this);
        settings = settingsDAO.obterTodos();

        setupBillingClient();

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

        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).getSwitchDoAll().equals("true")) {
                switchDoAll.setChecked(true);
                switchOnlyNotify.setChecked(false);
                switchDoNothing.setChecked(false);
            } else if (settings.get(i).getSwitchOnlyNotify().equals("true")) {
                switchDoAll.setChecked(false);
                switchOnlyNotify.setChecked(true);
                switchDoNothing.setChecked(false);
            } else if (settings.get(i).getSwitchDoNothing().equals("true")) {
                switchDoAll.setChecked(false);
                switchOnlyNotify.setChecked(false);
                switchDoNothing.setChecked(true);
            }

            long[] value = printDifference(settings.get(i).getTimeInterval());

            valueTimeInterval = Math.toIntExact(value[1]);
            textViewIntervalNotification.setText(value[1] + " " + getResources().getString(R.string.hour) + " " + value[2] + " " + getResources().getString(R.string.minute));
            valueML = settings.get(i).getValueML();
            editTextValueML.setText(String.valueOf(valueML));
        }

        switchDoAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    switchOnlyNotify.setChecked(false);
                    switchDoNothing.setChecked(false);
                }
            }
        });

        switchOnlyNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    switchDoAll.setChecked(false);
                    switchDoNothing.setChecked(false);
                }
            }
        });

        switchDoNothing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    switchDoAll.setChecked(false);
                    switchOnlyNotify.setChecked(false);
                }
            }
        });


        btnEditValueIntervalNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
                final View linearLayoutHourMinute = layoutInflater.inflate(R.layout.popup_choose_interval_notification, null);
                linearLayoutHourMinute.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                constraintLayoutRootSettingsActivity.addView(linearLayoutHourMinute);

                Spinner spinnerTimeIntervalNotification = findViewById(R.id.spinner_time_interval);
                Button btnSaveTimeInterval = findViewById(R.id.btn_save_time_interval);

                ArrayAdapter<CharSequence> arrayAdapterFrequenciaDaIngestao = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.array_time_interval, android.R.layout.simple_spinner_item);
                arrayAdapterFrequenciaDaIngestao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTimeIntervalNotification.setAdapter(arrayAdapterFrequenciaDaIngestao);
                spinnerTimeIntervalNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        frequenciaIngestao = adapterView.getItemAtPosition(position).toString();

                        String[] splitResult = frequenciaIngestao.split(" ");

                        if (splitResult[1].equals("min")) {
                            valueTimeInterval = Integer.parseInt(splitResult[0]) * 60 * 1000;
                        } else {
                            valueTimeInterval = Integer.parseInt(splitResult[0]) * 60 * 60 * 1000;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                btnSaveTimeInterval.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        constraintLayoutRootSettingsActivity.removeView(linearLayoutHourMinute);
                        textViewIntervalNotification.setText(frequenciaIngestao);
                    }
                });

            }
        });

        btnComeBackToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMainActivity = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
                finish();
            }
        });

        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueML = Integer.parseInt(editTextValueML.getText().toString());

                Intent intentMainActivity = new Intent(SettingsActivity.this, MainActivity.class);

                if (switchDoAll.isChecked()) {
                    int i = settingsDAO.updade(1, "true", "false", "false", valueTimeInterval, valueML);

                    if (i == 0) {
                        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.couldnt_save_changes), Toast.LENGTH_SHORT).show();
                    } else {
                        Alarm.getInstance().ativarAlarme(SettingsActivity.this);
                    }

                    startActivity(intentMainActivity);
                    finish();
                } else if (switchOnlyNotify.isChecked()) {
                    int i = settingsDAO.updade(1, "false", "true", "false", valueTimeInterval, valueML);

                    if (i == 0) {
                        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.couldnt_save_changes), Toast.LENGTH_SHORT).show();
                    } else {
                        Alarm.getInstance().ativarAlarme(SettingsActivity.this);
                    }

                    startActivity(intentMainActivity);
                    finish();
                } else if (switchDoNothing.isChecked()) {
                    int i = settingsDAO.updade(1, "false", "false", "true", valueTimeInterval, valueML);

                    if (i == 0) {
                        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.couldnt_save_changes), Toast.LENGTH_SHORT).show();
                    } else {
                        Alarm.getInstance().ativarAlarme(SettingsActivity.this);
                    }

                    startActivity(intentMainActivity);
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.choose_an_option), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verfica se app está na versão paga
                if (GeneralConfigs.getInstance().isVersaoPaga()) {
                    Toast.makeText(SettingsActivity.this, "Your app is in the PRO version.", Toast.LENGTH_SHORT).show();
                } else {

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    linearLayoutAds = (LinearLayout) layoutInflater.inflate(R.layout.activity_version_pro_announcement, constraintLayoutRootSettingsActivity, false);
                    constraintLayoutRootSettingsActivity.addView(linearLayoutAds);

                    ImageButton btnCloseBuyNow = (ImageButton) findViewById(R.id.btn_close_buy_now);
                    Button btnBuyNow = (Button) findViewById(R.id.btn_buy_now);
                    TextView txtPriceAppValue = (TextView) findViewById(R.id.propaganda_preco);

                    txtPriceAppValue.setText(GeneralConfigs.getInstance().getPrecoDaVersaoPaga());

                    btnCloseBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            constraintLayoutRootSettingsActivity.removeView(linearLayoutAds);
                        }
                    });

                    btnBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // efetuar uma compra

                            loadPurchases();

                        }
                    });

                }
            }
        });

    }

    public long[] printDifference(long temp) {

        long[] result = new long[4];

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = temp / daysInMilli;
        temp = temp % daysInMilli;

        long elapsedHours = temp / hoursInMilli;
        temp = temp % hoursInMilli;

        long elapsedMinutes = temp / minutesInMilli;
        temp = temp % minutesInMilli;

        long elapsedSeconds = temp / secondsInMilli;

        result[0] = elapsedDays;
        result[1] = elapsedHours;
        result[2] = elapsedMinutes;
        result[3] = elapsedSeconds;

        return result;
    }

    private void setupBillingClient() {
        listener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //GeneralConfigs.getInstance().setVersaoPaga(true);
                    //System.out.println("BillingClient.BillingResponseCode.OK");
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();

                    if (purchases.size() > 0) {

                        for (Purchase purchase : purchases) {
                            handleItemAlreadyPurchase(purchase);
                        }
                    } else {
                        // carregar as informacoes dos itens

                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }


    private void handleItemAlreadyPurchase(Purchase purchase) {
        // validando a compra
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.acknowledgePurchase(acknowledgePurchaseParams, listener);


            } else {
                GeneralConfigs.getInstance().setVersaoPaga(true);


            }
        }
    }

    public void loadPurchases() {
        if(billingClient.isReady()) {

            List<String> skuList = new ArrayList<>();
            skuList.add("filterapp_sub");

            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build();

            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // aqui o carinha passa pra recycle view o context, list e billingClient


                        for (SkuDetails skuDetails : list) {

                            if (skuDetails.getSku().equals("filterapp_sub")) {
                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build();

                                int response = billingClient.launchBillingFlow(SettingsActivity.this, billingFlowParams)
                                        .getResponseCode();

                                switch (response) {
                                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                                        //Toast.makeText(MainActivity.this, "BILLING_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                                        //Toast.makeText(MainActivity.this, "DEVELOPER_ERROR", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ERROR:
                                        //Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                                        //Toast.makeText(MainActivity.this, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                                        //Toast.makeText(MainActivity.this, "ITEM_ALREADY_OWNED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                                        //Toast.makeText(MainActivity.this, "ITEM_NOT_OWNED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                                        //Toast.makeText(MainActivity.this, "ITEM_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                                        //Toast.makeText(MainActivity.this, "SERVICE_DISCONNECTED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                                        //Toast.makeText(MainActivity.this, "SERVICE_TIMEOUT", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                                        //Toast.makeText(MainActivity.this, "SERVICE_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.USER_CANCELED:
                                        //Toast.makeText(MainActivity.this, "USER_CANCELED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        break;
                                }
                            }

                        }
                    } else {
                        // erro
                        Toast.makeText(SettingsActivity.this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // not ready!
            Toast.makeText(this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if ((billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) && (list != null)) {
            for (Purchase purchase : list) {
                handleItemAlreadyPurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

        } else {
            // erro qualquer
            Toast.makeText(this, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
        }

    }
}