package com.shutipro.sdk.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.shuftipro.R;
import com.shutipro.sdk.cloud.HttpConnectionHandler;
import com.shutipro.sdk.constants.Constants;
import com.shutipro.sdk.fragments.CameraRecordingFragment;
import com.shutipro.sdk.fragments.TutorialFragment;
import com.shutipro.sdk.helpers.FragmentHelper;
import com.shutipro.sdk.helpers.IntentHelper;
import com.shutipro.sdk.listeners.NetworkListener;
import com.shutipro.sdk.listeners.VideoListener;
import com.shutipro.sdk.models.ShuftiVerificationRequestModel;
import com.shutipro.sdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.shutipro.sdk.constants.Constants.VERIFICATION_REQUEST_ACCEPTED;
import static com.shutipro.sdk.constants.Constants.VERIFICATION_REQUEST_DECLINED;

public class ShuftiVerifyActivity extends AppCompatActivity implements TutorialFragment.TutorialStateFragment, VideoListener,
        NetworkListener {

    private TutorialFragment tutorialFragment;
    private int currentState = 0;
    public static LinearLayout rlLoadingProgress;
    private HashMap<String, String> responseSet;
    private CameraRecordingFragment cameraFragment;
    private final String TAG = ShuftiVerifyActivity.class.getSimpleName();
    private ShuftiVerificationRequestModel shuftiVerificationRequestModel;
    private JSONObject requestedObject;
    public static boolean requestInProcess = false;
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;
    private AlertDialog alertDialog;
    private boolean docIdCard = false, docPassport = false, docDrivingLicense = false, docCreditCard = false,
            addressIdCard = false, addressUtilityBills = false, addressBankStatement = false;
    private static ShuftiVerifyActivity instance = null;

    private String[] documentSupportedTypes = new String[]{"id_card", "credit_or_debit_card", "passport", "driving_license"};
    private String[] addressSupportedTypes = new String[]{"id_card", "utility_bill", "bank_statement"};

    private boolean documentVerification = true, addressVerification = true;

    private boolean asyncRequest = false;
    private String reference = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_shufti_verify);

        instance = this;

        //Initializing UI elements
        rlLoadingProgress = findViewById(R.id.rl_progress_update);
        responseSet = new HashMap<>();
        requestedObject = new JSONObject();

        if (IntentHelper.getInstance().containsKey(Constants.KEY_DATA_MODEL)) {
            shuftiVerificationRequestModel = (ShuftiVerificationRequestModel) IntentHelper.getInstance().getObject(Constants.KEY_DATA_MODEL);
        }

        //Handling Stack trace on an exception
        //Setting instance to handle the uncaught exceptions

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                String threadName = paramThread.getName();
                String stackTrace = Arrays.toString(paramThread.getStackTrace());
                String message = paramThrowable.getMessage();
                String deviceInformation = Utils.getDeviceInformation();
                String timeStamp = Utils.getCurrentTimeStamp();
                String clientId = "";
                if (shuftiVerificationRequestModel != null) {
                    clientId = shuftiVerificationRequestModel.getClientId();
                }

                HttpConnectionHandler.getInstance(clientId, "", false)
                        .sendStacktraceReport(ShuftiVerifyActivity.this, clientId, threadName, stackTrace, message, deviceInformation, timeStamp);
            }
        });

        //Return callbacks in case of wrong parameters sending
        if (shuftiVerificationRequestModel != null) {
            String clientId = shuftiVerificationRequestModel.getClientId();
            if (clientId == null || clientId.isEmpty()) {
                returnErrorCallback(getString(R.string.empty_client_id));
                return;
            }
            String secretKey = shuftiVerificationRequestModel.getSecretKey();
            if (secretKey == null || secretKey.isEmpty()) {
                returnErrorCallback(getString(R.string.empty_secret_key));
                return;
            }
            String reference = shuftiVerificationRequestModel.getReference();
            if (reference == null || reference.isEmpty()) {
                returnErrorCallback(getString(R.string.empty_reference));
                return;
            }
            String country = shuftiVerificationRequestModel.getCountry();
            if (country == null || country.isEmpty()) {
                returnErrorCallback(getString(R.string.empty_country));
                return;
            }
            String email = shuftiVerificationRequestModel.getEmail();
            if (email == null || email.isEmpty()) {
                returnErrorCallback(getString(R.string.empty_email));
                return;
            }
            String callback_url = shuftiVerificationRequestModel.getCallbackUrl();
            if (callback_url == null || callback_url.isEmpty()) {
                returnErrorCallback(getString(R.string.empty_callback_url));
                return;
            }

            asyncRequest = shuftiVerificationRequestModel.isAsyncRequest();
        }

        try {

            requestedObject.put("reference", shuftiVerificationRequestModel.getReference());
            requestedObject.put("country", shuftiVerificationRequestModel.getCountry());
            requestedObject.put("language", shuftiVerificationRequestModel.getLanguage());
            requestedObject.put("email", shuftiVerificationRequestModel.getEmail());
            requestedObject.put("callback_url", shuftiVerificationRequestModel.getCallbackUrl());
            requestedObject.put("redirect_url", shuftiVerificationRequestModel.getRedirectUrl());
            requestedObject.put("verification_mode", "video_only");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!requestInProcess)
            checkForVerificationRequest();
    }

    public void checkForVerificationRequest() {

        if (shuftiVerificationRequestModel.getFaceVerification() != null && shuftiVerificationRequestModel.getFaceVerification().isFaceVerification()) {
            currentState = Constants.CODE_SELFIE;
            showTutorialFragment();
            shuftiVerificationRequestModel.getFaceVerification().setFaceVerification(false);
            return;
        }
        if (shuftiVerificationRequestModel.getDocumentVerification() != null && documentVerification) {
            currentState = Constants.CODE_DOCUMENT;
            setDocumentTypes();
            showTutorialFragment();
            documentVerification = false;
            return;
        }
        if (shuftiVerificationRequestModel.getAddressVerification() != null && addressVerification) {
            currentState = Constants.CODE_ADDRESS;
            setAddressTypes();
            showTutorialFragment();
            addressVerification = false;
            return;
        }

        if (!requestInProcess) {
            sendRequestToShuftiproServer(requestedObject);
        }
    }

    private void setDocumentTypes() {

        ArrayList<String> supported_types = shuftiVerificationRequestModel.getDocumentVerification().getSupportedTypes();
        if (supported_types != null && !supported_types.isEmpty()) {
            if (supported_types.contains(documentSupportedTypes[0])) {
                docIdCard = true;
            }
            if (supported_types.contains(documentSupportedTypes[1])) {
                docCreditCard = true;
            }
            if (supported_types.contains(documentSupportedTypes[2])) {
                docPassport = true;
            }
            if (supported_types.contains(documentSupportedTypes[3])) {
                docDrivingLicense = true;
            }
        } else {
            returnErrorCallback(getString(R.string.document_type_no_set));
        }
    }

    private void setAddressTypes() {

        ArrayList<String> supported_types = shuftiVerificationRequestModel.getAddressVerification().getSupportedTypes();
        if (supported_types != null && !supported_types.isEmpty()) {
            if (supported_types.contains(addressSupportedTypes[0])) {
                addressIdCard = true;
            }
            if (supported_types.contains(addressSupportedTypes[1])) {
                addressUtilityBills = true;
            }
            if (supported_types.contains(addressSupportedTypes[2])) {
                addressBankStatement = true;
            }
        } else {
            returnErrorCallback(getString(R.string.address_type_no_set));
        }
    }

    public static ShuftiVerifyActivity getInstance() {
        return instance;
    }

    private void sendRequestToShuftiproServer(JSONObject requestedObject) {

        requestInProcess = true;
        String clientId = shuftiVerificationRequestModel.getClientId();
        String secretKey = shuftiVerificationRequestModel.getSecretKey();
        reference = shuftiVerificationRequestModel.getReference();

        responseSet.clear();

        if (!asyncRequest) {
            rlLoadingProgress.setVisibility(View.VISIBLE);

            try {

                boolean isSubmitted = HttpConnectionHandler.getInstance(clientId, secretKey, asyncRequest).executeVerificationRequest(requestedObject,
                        ShuftiVerifyActivity.this, ShuftiVerifyActivity.this);

                if (!isSubmitted) {
                    requestInProcess = false;

                    responseSet.put("reference", reference);
                    responseSet.put("event", "");
                    responseSet.put("error", "No Internet Connection");

                    showDialog("No Internet Connection",
                            "Make sure your device is connected to the internet",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                                        shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
                                    }
                                    if (alertDialog.isShowing()) {
                                        alertDialog.dismiss();
                                    }
                                    ShuftiVerifyActivity.this.finish();
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            rlLoadingProgress.setVisibility(View.GONE);

            try {

                boolean isSubmitted = HttpConnectionHandler.getInstance(clientId, secretKey, asyncRequest).executeVerificationRequest(requestedObject,
                        ShuftiVerifyActivity.this, ShuftiVerifyActivity.this);

                if (!isSubmitted) {
                    requestInProcess = false;

                    responseSet.put("reference", reference);
                    responseSet.put("event", "");

                    if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                        shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
                    }
                    returnErrorCallback("No Internet Connection");

                } else {

                    requestInProcess = false;
                    Log.e(TAG, "Async Request has been sent.");
                    ShuftiVerifyActivity.this.finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showTutorialFragment() {

        tutorialFragment = TutorialFragment.newInstance(currentState, docIdCard, docPassport, docDrivingLicense, docCreditCard, addressIdCard, addressUtilityBills, addressBankStatement);
        tutorialFragment.setTutorialFragmentCB(this);
        rlLoadingProgress.setOnClickListener(null);
        FragmentHelper.addFragment(this, tutorialFragment, R.id.rl_fragment_container, Constants.TAG_TUTORIAL_FRAGMENT);
    }

    public void showPermissionRejectionDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("App won't work without permissions. Please, restart app and give" +
                " access to the permissions.");
        alertDialog.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ShuftiVerifyActivity.this.finish();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public int getTypeofTutorial() {
        return currentState;
    }

    @Override
    public void captureVideo(int state) {
        FragmentHelper.removeFragment(this, Constants.TAG_TUTORIAL_FRAGMENT);
        if (state == Constants.CODE_SELFIE) {
            cameraFragment = CameraRecordingFragment.newInstance(true);
        } else {
            cameraFragment = CameraRecordingFragment.newInstance(false);
        }
        cameraFragment.setVideoStateListener(this);
        FragmentHelper.addFragment(this, cameraFragment, R.id.rl_fragment_container,
                Constants.TAG_CAMERA_FRAGMENT);
    }

    @Override
    public void onVideoRecorded(File recordedFile, String encodedBase64String) {
        FragmentHelper.removeFragment(this, Constants.TAG_CAMERA_FRAGMENT);
        makeJsonObjectOfInput(currentState, encodedBase64String);
        checkForVerificationRequest();
    }

    private void makeJsonObjectOfInput(int currentState, String encodedBase64String) {
        switch (currentState) {
            case Constants.CODE_SELFIE:
                makeFaceObject(encodedBase64String);
                break;

            case Constants.CODE_DOCUMENT:
                makeDocumentObject(encodedBase64String);
                break;

            case Constants.CODE_ADDRESS:
                makeAddressObject(encodedBase64String);
                break;
        }
    }

    private void makeFaceObject(String encodedBase64String) {
        JSONObject faceObject = new JSONObject();
        try {
            faceObject.put("proof", Constants.VIDEO_PREFIX_FOR_SERVER + encodedBase64String);
            requestedObject.put("face", faceObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void makeDocumentObject(String encodedBase64String) {
        JSONObject documentationObject = new JSONObject();
        ArrayList<String> doc_supported_types = shuftiVerificationRequestModel.getDocumentVerification().getSupportedTypes();

        try {
            documentationObject.put("proof", Constants.VIDEO_PREFIX_FOR_SERVER + encodedBase64String);
            documentationObject.put("supported_types", new JSONArray(doc_supported_types));

            //Set parameter in the requested object if OCR is required.
            if (shuftiVerificationRequestModel.getDocumentVerification().isExtractName()) {
                documentationObject.put("name", "");
            }
            if (shuftiVerificationRequestModel.getDocumentVerification().isExtractDob()) {
                documentationObject.put("dob", "");
            }
            if (shuftiVerificationRequestModel.getDocumentVerification().isExtractDocumentNumber()) {
                documentationObject.put("document_number", "");
            }
            if (shuftiVerificationRequestModel.getDocumentVerification().isExtractExpiryDate()) {
                documentationObject.put("expiry_date", "");
            }
            if (shuftiVerificationRequestModel.getDocumentVerification().isExtractIssueDate()) {
                documentationObject.put("issue_date", "");
            }
            requestedObject.put("document", documentationObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeAddressObject(String encodedBase64String) {
        JSONObject addressObject = new JSONObject();
        ArrayList<String> address_supported_types = shuftiVerificationRequestModel.getAddressVerification().getSupportedTypes();

        try {
            addressObject.put("proof", Constants.VIDEO_PREFIX_FOR_SERVER + encodedBase64String);
            if (shuftiVerificationRequestModel.getAddressVerification().isExtractFullAddress()) {
                addressObject.put("full_address", "");
            }
            if (shuftiVerificationRequestModel.getAddressVerification().isExtractName()) {
                addressObject.put("name", "");
            }

            addressObject.put("supported_types", new JSONArray(address_supported_types));
            requestedObject.put("address", addressObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void successResponse(String result) {

        requestInProcess = false;
        responseSet.clear();

        try {

            JSONObject jsonObject = new JSONObject(result);
            String reference = "";
            String event = "";
            String error = "";
            String verification_url = "";
            String verification_result = "";
            String verification_data = "";
            String declined_reason = "";

            if (jsonObject.has("reference")) {
                try {
                    reference = jsonObject.getString("reference");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("event")) {
                try {
                    event = jsonObject.getString("event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("error")) {
                try {
                    error = jsonObject.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_url")) {
                try {
                    verification_url = jsonObject.getString("verification_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_result")) {
                try {
                    verification_result = jsonObject.getString("verification_result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_data")) {
                try {
                    verification_data = jsonObject.getString("verification_data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("declined_reason")) {
                try {
                    declined_reason = jsonObject.getString("declined_reason");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            responseSet.put("reference", reference);
            responseSet.put("event", event);
            responseSet.put("error", error);
            responseSet.put("verification_url", verification_url);
            responseSet.put("verification_result", verification_result);
            responseSet.put("verification_data", verification_data);
            responseSet.put("declined_reason", declined_reason);

            if (!asyncRequest) {

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                            shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
                        }
                        //rlLoadingProgress.setVisibility(View.GONE);
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        ShuftiVerifyActivity.this.finish();
                    }
                };

                if (event.equalsIgnoreCase(VERIFICATION_REQUEST_ACCEPTED)) {
                    showDialog("Success", "Verified", onClickListener);
                } else if (event.equalsIgnoreCase(VERIFICATION_REQUEST_DECLINED)) {
                    showDialog("Failure", "Not Verified", onClickListener);
                } else {
                    if (error.isEmpty()) {
                        error = "Error Occurred";
                    }
                    showDialog("Error", "" + error, onClickListener);
                }
            } else {

                if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                    shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
                }

                ShuftiVerifyActivity.this.finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorResponse(String response) {

        requestInProcess = false;
        responseSet.clear();

        if (response == null) {
            return;
        }

        try {

            JSONObject jsonObject = new JSONObject(response);
            String reference = "";
            String event = "";
            String error = "";
            String verification_url = "";
            String verification_result = "";
            String verification_data = "";
            String declined_reason = "";

            if (jsonObject.has("reference")) {
                try {
                    reference = jsonObject.getString("reference");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("event")) {
                try {
                    event = jsonObject.getString("event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("error")) {
                try {
                    JSONObject errorObject = new JSONObject(jsonObject.getString("error"));
                    error = errorObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_url")) {
                try {
                    verification_url = jsonObject.getString("verification_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_result")) {
                try {
                    verification_result = jsonObject.getString("verification_result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_data")) {
                try {
                    verification_data = jsonObject.getString("verification_data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("declined_reason")) {
                try {
                    declined_reason = jsonObject.getString("declined_reason");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            responseSet.put("reference", reference);
            responseSet.put("event", event);
            responseSet.put("error", error);
            responseSet.put("verification_url", verification_url);
            responseSet.put("verification_result", verification_result);
            responseSet.put("verification_data", verification_data);
            responseSet.put("declined_reason", declined_reason);

            if (!asyncRequest) {
                if (error.isEmpty()) {
                    error = "Error Occurred";
                }
                showDialog("Error", "" + error, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Sending response back to caller

                        if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                            shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
                        }
                        //rlLoadingProgress.setVisibility(View.GONE);
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        ShuftiVerifyActivity.this.finish();
                    }
                });

            } else {

                if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                    shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
                }

                ShuftiVerifyActivity.this.finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Override back pressed method to stop user from quitting accindently
    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertClose = new AlertDialog.Builder(this);
        alertClose.setMessage("Are you sure you want to close verification process ?");
        alertClose.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestInProcess = false;
                ShuftiVerifyActivity.this.finish();
            }
        });

        alertClose.setNegativeButton("No", null);
        alertClose.show();
    }

    private void showDialog(String title, String message, View.OnClickListener clickListener) {

        rlLoadingProgress.setVisibility(View.GONE);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShuftiVerifyActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.response_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        TextView tvMessage = dialogView.findViewById(R.id.tv_message_response);
        ImageView crossIconImageView = dialogView.findViewById(R.id.crossIconImageView);
        ImageView responseImageView = dialogView.findViewById(R.id.responseImageView);

        if (title.equalsIgnoreCase("Success")) {
            responseImageView.setImageResource(R.drawable.success_icon);
            tvMessage.setText("Successfully Verified");
        } else if (title.equalsIgnoreCase("Failure")) {
            responseImageView.setImageResource(R.drawable.failure_icon);
            tvMessage.setText("Verification Unsuccessful");
        } else {
            responseImageView.setImageResource(R.drawable.failure_icon);
            tvMessage.setText(message);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        crossIconImageView.setOnClickListener(clickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        showPermissionRejectionDialog();
                    }
                }
            }
        }
    }

    private void returnErrorCallback(String error) {

        responseSet.put("error", error);

        if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
            shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
        }
        ShuftiVerifyActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;

        try {
            trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        assert dir != null;
        return dir.delete();
    }
}



