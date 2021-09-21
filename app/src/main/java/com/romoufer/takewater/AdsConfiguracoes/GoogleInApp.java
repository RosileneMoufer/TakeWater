package com.romoufer.takewater.AdsConfiguracoes;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

public class GoogleInApp {

    public static final GoogleInApp instance = new GoogleInApp();
    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener;

    public static GoogleInApp getInstance() {
        return instance;
    }

    public BillingClient getBillingClient() {
        return billingClient;
    }

    public PurchasesUpdatedListener getPurchasesUpdatedListener() {
        return purchasesUpdatedListener;
    }

    public void setBillingClient(BillingClient billingClient) {
        this.billingClient = billingClient;
    }

    public void setPurchasesUpdatedListener(PurchasesUpdatedListener purchasesUpdatedListener) {
        this.purchasesUpdatedListener = purchasesUpdatedListener;
    }
}
