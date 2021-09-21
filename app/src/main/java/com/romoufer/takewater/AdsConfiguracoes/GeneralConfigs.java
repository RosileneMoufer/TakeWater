package com.romoufer.takewater.AdsConfiguracoes;

import androidx.constraintlayout.widget.ConstraintLayout;

public class GeneralConfigs {

    private int alterAd = 0; // alternar ads
    private boolean versaoPaga = false; // se usuário assinou o app
    private String precoDaVersaoPaga;
    private ConstraintLayout constraintLayoutBloqueio = null;

    public static final GeneralConfigs instance = new GeneralConfigs();

    public static GeneralConfigs getInstance() {
        return instance;
    }

    public int getAlterAd() {
        return alterAd;
    }

    public void setAlterAd(int alterAd) {
        this.alterAd = alterAd;
    }

    public boolean isVersaoPaga() {
        return versaoPaga;
    }

    public void setVersaoPaga(boolean versaoPaga) {
        this.versaoPaga = versaoPaga;
    }

    public String getPrecoDaVersaoPaga() {
        return precoDaVersaoPaga;
    }

    public void setPrecoDaVersaoPaga(String precoDaVersaoPaga) {
        this.precoDaVersaoPaga = precoDaVersaoPaga;
    }

    public ConstraintLayout getConstraintLayoutBloqueio() {
        return constraintLayoutBloqueio;
    }

    public void setConstraintLayoutBloqueio(ConstraintLayout constraintLayoutBloqueio) {
        this.constraintLayoutBloqueio = constraintLayoutBloqueio;
    }

}
