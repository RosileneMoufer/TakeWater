package com.romoufer.takewater.AdsConfiguracoes;

import android.content.Context;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

public class BillingClientSetup {

    private static BillingClient instance;

    public static BillingClient getInstance(Context context, PurchasesUpdatedListener purchasesUpdatedListener) {
        if (instance == null) {
            instance = BillingClient.newBuilder(context)
                    .enablePendingPurchases()
                    .setListener(purchasesUpdatedListener)
                    .build();
        }

        return instance;
    }
}
