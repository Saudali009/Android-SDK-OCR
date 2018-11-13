package com.shutipro.sdk;

import android.app.Activity;
import android.content.Intent;

import com.shutipro.sdk.activities.ShuftiVerifyActivity;
import com.shutipro.sdk.constants.Constants;
import com.shutipro.sdk.helpers.IntentHelper;
import com.shutipro.sdk.listeners.ShuftiVerifyListener;
import com.shutipro.sdk.models.ShuftiVerificationRequestModel;

public class Shuftipro {

    private static Shuftipro shuftipro = null;
    private String clientId;
    private String secretKey;
    private boolean async;

    private Shuftipro(String clientId, String secretKey, boolean async){
        this.clientId = clientId;
        this.secretKey = secretKey;
        this.async = async;
    }

    public static Shuftipro getInstance(String clientId, String secretKey, boolean async){
        if(shuftipro == null){
            shuftipro = new Shuftipro(clientId,secretKey,async);
        }

        return shuftipro;
    }
    public void shuftiproVerification(String reference, String country, String language, String email, String callback_url, String redirect_url,
                                      boolean isToMakeFaceVerification,
                                      boolean isToPerformDocumentationVerification, boolean isSupportPassportType,
                                      boolean isSupportDocIdCardType, boolean isSupportDrivingLicenseType, boolean isSupportCreditCardType, boolean documentName,
                                      boolean dob, boolean documentNumber, boolean expiryDate, boolean issueDate,
                                      boolean isToPerformAddressVerification,
                                      boolean fullAddress, boolean addressDocumentName, boolean isUtilityBillSupportedType, boolean isIdCardSupportedType,
                                      boolean isBankStatementSupportedType,
                                      Activity parentActivity, ShuftiVerifyListener shuftiVerifyListener){

        ShuftiVerificationRequestModel verificationRequestModel = new ShuftiVerificationRequestModel();
        verificationRequestModel.setClientId(clientId);
        verificationRequestModel.setSecretKey(secretKey);
        verificationRequestModel.setReference(reference);
        verificationRequestModel.setCountry(country);
        verificationRequestModel.setLanguage(language);
        verificationRequestModel.setEmail(email);
        verificationRequestModel.setCallback_url(callback_url);
        verificationRequestModel.setRedirect_url(redirect_url);

        //Starting face verification object
        verificationRequestModel.setToMakeFaceVerification(isToMakeFaceVerification);

        //Starting document verification object
        verificationRequestModel.setToPerformDocumentationVerification(isToPerformDocumentationVerification);
        verificationRequestModel.setSupportPassportType(isSupportPassportType);
        verificationRequestModel.setSupportDocIdCardType(isSupportDocIdCardType);
        verificationRequestModel.setSupportDrivingLicenseType(isSupportDrivingLicenseType);
        verificationRequestModel.setSupportCreditCardType(isSupportCreditCardType);
        verificationRequestModel.setDocumentName(documentName);
        verificationRequestModel.setDob(dob);
        verificationRequestModel.setDocumentNumber(documentNumber);
        verificationRequestModel.setExpiryDate(expiryDate);
        verificationRequestModel.setIssueDate(issueDate);

        //Starting address verification object
        verificationRequestModel.setToPerformAddressVerification(isToPerformAddressVerification);
        verificationRequestModel.setFullAddress(fullAddress);
        verificationRequestModel.setAddressDocumentName(addressDocumentName);
        verificationRequestModel.setUtilityBillSupportedType(isUtilityBillSupportedType);
        verificationRequestModel.setIdCardSupportedType(isIdCardSupportedType);
        verificationRequestModel.setBankStatementSupportedType(isBankStatementSupportedType);

        verificationRequestModel.setShuftiVerifyListener(shuftiVerifyListener);
        verificationRequestModel.setAsyncRequest(async);

        //Pas this request to the ShuftiVerification Activity
        IntentHelper.getInstance().insertObject(Constants.KEY_DATA_MODEL,verificationRequestModel);
        Intent intent = new Intent(parentActivity, ShuftiVerifyActivity.class);
        parentActivity.startActivity(intent);
    }
}
