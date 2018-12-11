package com.shutipro.sdk.models;

import java.util.ArrayList;

public class DocumentVerification {

    private static DocumentVerification instance = null;
    private ArrayList<String> supportedTypes;
    private boolean extractName;
    private boolean extractDob;
    private boolean extractDocumentNumber;
    private boolean extractExpiryDate;
    private boolean extractIssueDate;

    private DocumentVerification(){

    }

    public static DocumentVerification getInstance() {

        if (instance == null){
            instance = new DocumentVerification();
        }
        return instance;
    }

    public ArrayList<String> getSupportedTypes() {
        return supportedTypes;
    }

    public void setSupportedTypes(ArrayList<String> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public boolean isExtractName() {
        return extractName;
    }

    public void extractName(boolean extractName) {
        this.extractName = extractName;
    }

    public boolean isExtractDob() {
        return extractDob;
    }

    public void extractDob(boolean extractDob) {
        this.extractDob = extractDob;
    }

    public boolean isExtractDocumentNumber() {
        return extractDocumentNumber;
    }

    public void extractDocumentNumber(boolean extractDocumentNumber) {
        this.extractDocumentNumber = extractDocumentNumber;
    }

    public boolean isExtractExpiryDate() {
        return extractExpiryDate;
    }

    public void extractExpiryDate(boolean extractExpiryDate) {
        this.extractExpiryDate = extractExpiryDate;
    }

    public boolean isExtractIssueDate() {
        return extractIssueDate;
    }

    public void extractIssueDate(boolean extractIssueDate) {
        this.extractIssueDate = extractIssueDate;
    }
}
