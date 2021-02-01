package com.icsfl.rfsmart.honeywell;

import android.content.Context;
import android.util.Log;

import com.honeywell.aidc.*;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class HoneywellScannerPlugin extends CordovaPlugin implements BarcodeReader.BarcodeListener {
    private static final String TAG = "HoneywellScanner";
    private static BarcodeReader barcodeReader;
    private AidcManager manager;
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {

        super.initialize(cordova, webView);

        Context context = cordova.getActivity().getApplicationContext();
        AidcManager.create(context, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = aidcManager.createBarcodeReader();
                barcodeReader.addBarcodeListener(HoneywellScannerPlugin.this);
                try {
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                            BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER, false);
                } catch (UnsupportedPropertyException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext)
    throws JSONException {
        this.callbackContext = callbackContext;
        if ("claim".equals(action)) {
            claimBarcodeReader();
        } else if ("release".equals(action)) {
            releaseBarcodeReader();
            callbackContext.success();
        }
        return true;
    }

    private void claimBarcodeReader() {
        try {
            barcodeReader.claim();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            NotifyError("Unable to claim barcode, no scanner available. Error: " + e.getMessage());
        }
    }

    private void releaseBarcodeReader(){
        barcodeReader.release();
    }

    private void destroyBarcodeReader(){
        barcodeReader.removeBarcodeListener(this);
        barcodeReader.close();
        manager.close();
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        this.callbackContext.success(barcodeReadEvent.getBarcodeData());
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        NotifyError("Scan has failed");
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        claimBarcodeReader();
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        releaseBarcodeReader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseBarcodeReader();
        destroyBarcodeReader();
    }

    private void NotifyError(String error) {
        this.callbackContext.error(error);
    }
}