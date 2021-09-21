package com.romoufer.takewater.AdsConfiguracoes;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MetodosDoBillingClient implements PurchasesUpdatedListener  {

    private BillingClient billingClient;
    AcknowledgePurchaseResponseListener listener;
    Context context;
    Activity activity;

    AdView adView;
    LinearLayout linearLayoutAds;

    static MetodosDoBillingClient instance;

    public static MetodosDoBillingClient getInstance(Context context, Activity activity) {
        if (instance == null) {
            instance = new MetodosDoBillingClient(context, activity);
        }
        return instance;
    }


    MetodosDoBillingClient(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void setupBillingClient() {
        listener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //GeneralConfigs.getInstance().setVersaoPaga(true);
                    System.out.println("BillingClient.BillingResponseCode.OK");
                }
            }
        };

        billingClient = BillingClientSetup.getInstance(context, this);
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


    public void handleItemAlreadyPurchase(Purchase purchase) {
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
            skuList.add("takewater_sub");

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

                            if (skuDetails.getSku().equals("takewater_sub")) {
                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build();

                                int response = billingClient.launchBillingFlow(activity, billingFlowParams)
                                        .getResponseCode();

                                switch (response) {
                                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                                        //Toast.makeText(MainActivity.this, "BILLING_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                                        //Toast.makeText(MainActivity.this, "DEVELOPER_ERROR", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ERROR:
                                        //Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                                        //Toast.makeText(MainActivity.this, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                                        //Toast.makeText(MainActivity.this, "ITEM_ALREADY_OWNED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                                        //Toast.makeText(MainActivity.this, "ITEM_NOT_OWNED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                                        //Toast.makeText(MainActivity.this, "ITEM_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                                        //Toast.makeText(MainActivity.this, "SERVICE_DISCONNECTED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                                        //Toast.makeText(MainActivity.this, "SERVICE_TIMEOUT", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                                        //Toast.makeText(MainActivity.this, "SERVICE_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();

                                        break;
                                    case BillingClient.BillingResponseCode.USER_CANCELED:
                                        //Toast.makeText(MainActivity.this, "USER_CANCELED", Toast.LENGTH_SHORT).show();

                                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        break;
                                }
                            }

                        }
                    } else {
                        // erro
                        Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // not ready!
            Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "Is not possible to buy now!", Toast.LENGTH_SHORT).show();
        }

    }


}
