package com.shutipro.sdk.models;

import android.app.Activity;

import com.shutipro.sdk.listeners.ShuftiVerifyListener;


public class ShuftiVerificationRequestModel {
    private String clientId;
    private String secretKey;
    private String reference;
    private String country;
    private String language;
    private String email;
    private String callback_url;
    private String redirect_url;
    private boolean isToMakeFaceVerification;

    private boolean isToPerformDocumentationVerification;
    private boolean isSupportPassportType;
    private boolean isSupportDocIdCardType;
    private boolean isSupportDrivingLicenseType;
    private boolean isSupportCreditCardType;

    private boolean documentName;
    private boolean dob;
    private boolean documentNumber;
    private boolean expiryDate;
    private boolean issueDate;

    private boolean isToPerformAddressVerification;
    private boolean isUtilityBillSupportedType;
    private boolean isIdCardSupportedType;
    private boolean isBankStatementSupportedType;
    private boolean fullAddress;
    private boolean addressDocumentName;

    private Activity parentActivity;
    private ShuftiVerifyListener shuftiVerifyListener;
    private boolean asyncRequest;

    public ShuftiVerificationRequestModel() {
    }

    public ShuftiVerificationRequestModel(String reference, String country, String language, String email, String callback_url, String redirect_url,
                                          boolean isToMakeFaceVerification,
                                          boolean isToPerformDocumentationVerification, boolean isSupportPassportType,
                                          boolean isSupportDocIdCardType, boolean isSupportDrivingLicenseType, boolean isSupportCreditCardType, boolean documentName,
                                          boolean dob, boolean documentNumber, boolean expiryDate, boolean issueDate,
                                          boolean isToPerformAddressVerification,
                                          boolean fullAddress, boolean addressDocumentName, boolean isUtilityBillSupportedType, boolean isIdCardSupportedType,
                                          boolean isBankStatementSupportedType,
                                          Activity parentActivity, ShuftiVerifyListener shuftiVerifyListener, boolean asyncRequest) {
        this.reference = reference;
        this.country = country;
        this.language = language;
        this.email = email;
        this.callback_url = callback_url;
        this.redirect_url = redirect_url;
        this.isToMakeFaceVerification = isToMakeFaceVerification;

        this.isToPerformDocumentationVerification = isToPerformDocumentationVerification;
        this.isSupportPassportType = isSupportPassportType;
        this.isSupportDocIdCardType = isSupportDocIdCardType;
        this.isSupportDrivingLicenseType = isSupportDrivingLicenseType;
        this.isSupportCreditCardType = isSupportCreditCardType;
        this.documentName = documentName;
        this.dob = dob;
        this.documentNumber = documentNumber;
        this.expiryDate = expiryDate;
        this.issueDate = issueDate;

        this.isToPerformAddressVerification = isToPerformAddressVerification;
        this.fullAddress = fullAddress;
        this.addressDocumentName = addressDocumentName ;
        this.isUtilityBillSupportedType = isUtilityBillSupportedType;
        this.isIdCardSupportedType = isIdCardSupportedType;
        this.isBankStatementSupportedType = isBankStatementSupportedType;

        this.parentActivity = parentActivity;
        this.shuftiVerifyListener = shuftiVerifyListener;
        this.asyncRequest = asyncRequest;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public boolean isToMakeFaceVerification() {
        return isToMakeFaceVerification;
    }

    public void setToMakeFaceVerification(boolean toMakeFaceVerification) {
        isToMakeFaceVerification = toMakeFaceVerification;
    }

    public boolean isToPerformDocumentationVerification() {
        return isToPerformDocumentationVerification;
    }

    public void setToPerformDocumentationVerification(boolean toPerformDocumentationVerification) {
        isToPerformDocumentationVerification = toPerformDocumentationVerification;
    }

    public boolean isSupportPassportType() {
        return isSupportPassportType;
    }

    public void setSupportPassportType(boolean supportPassportType) {
        isSupportPassportType = supportPassportType;
    }

    public boolean isSupportDocIdCardType() {
        return isSupportDocIdCardType;
    }

    public void setSupportDocIdCardType(boolean supportDocIdCardType) {
        isSupportDocIdCardType = supportDocIdCardType;
    }

    public boolean isSupportDrivingLicenseType() {
        return isSupportDrivingLicenseType;
    }

    public void setSupportDrivingLicenseType(boolean supportDrivingLicenseType) {
        isSupportDrivingLicenseType = supportDrivingLicenseType;
    }

    public boolean isSupportCreditCardType() {
        return isSupportCreditCardType;
    }

    public void setSupportCreditCardType(boolean supportCreditCardType) {
        isSupportCreditCardType = supportCreditCardType;
    }

    public boolean isDocumentName() {
        return documentName;
    }

    public void setDocumentName(boolean documentName) {
        this.documentName = documentName;
    }

    public boolean isDob() {
        return dob;
    }

    public void setDob(boolean dob) {
        this.dob = dob;
    }

    public boolean isDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(boolean documentNumber) {
        this.documentNumber = documentNumber;
    }

    public boolean isExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(boolean expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isIssueDate() {
        return issueDate;
    }

    public void setIssueDate(boolean issueDate) {
        this.issueDate = issueDate;
    }

    public boolean isToPerformAddressVerification() {
        return isToPerformAddressVerification;
    }

    public void setToPerformAddressVerification(boolean toPerformAddressVerification) {
        isToPerformAddressVerification = toPerformAddressVerification;
    }

    public boolean isUtilityBillSupportedType() {
        return isUtilityBillSupportedType;
    }

    public void setUtilityBillSupportedType(boolean utilityBillSupportedType) {
        isUtilityBillSupportedType = utilityBillSupportedType;
    }

    public boolean isIdCardSupportedType() {
        return isIdCardSupportedType;
    }

    public void setIdCardSupportedType(boolean idCardSupportedType) {
        isIdCardSupportedType = idCardSupportedType;
    }

    public boolean isBankStatementSupportedType() {
        return isBankStatementSupportedType;
    }

    public void setBankStatementSupportedType(boolean bankStatementSupportedType) {
        isBankStatementSupportedType = bankStatementSupportedType;
    }

    public boolean isFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(boolean fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean isAddressDocumentName() {
        return addressDocumentName;
    }

    public void setAddressDocumentName(boolean addressDocumentName) {
        this.addressDocumentName = addressDocumentName;
    }

    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public ShuftiVerifyListener getShuftiVerifyListener() {
        return shuftiVerifyListener;
    }

    public void setShuftiVerifyListener(ShuftiVerifyListener shuftiVerifyListener) {
        this.shuftiVerifyListener = shuftiVerifyListener;
    }

    public boolean isAsyncRequest() {
        return asyncRequest;
    }

    public void setAsyncRequest(boolean asyncRequest) {
        this.asyncRequest = asyncRequest;
    }
}
