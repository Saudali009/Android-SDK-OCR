package com.shutipro.sdk.models;

import java.util.ArrayList;

public class AddressVerification {

    private static AddressVerification instance = null;
    private ArrayList<String> supportedTypes;
    private boolean extractFullAddress;
    private boolean extractName;

    private AddressVerification(){

    }

    public static AddressVerification getInstance() {
        if (instance == null){
            instance = new AddressVerification();
        }
        return instance;
    }

    public ArrayList<String> getSupportedTypes() {
        return supportedTypes;
    }

    public void setSupportedTypes(ArrayList<String> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public boolean isExtractFullAddress() {
        return extractFullAddress;
    }

    public void extractFullAddress(boolean extractFullAddress) {
        this.extractFullAddress = extractFullAddress;
    }

    public boolean isExtractName() {
        return extractName;
    }

    public void extractName(boolean extractName) {
        this.extractName = extractName;
    }
}
