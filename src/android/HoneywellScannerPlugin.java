package com.icsfl.rfsmart.honeywell;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.ScannerNotClaimedException;

public class HoneywellScannerPlugin extends CordovaPlugin implements BarcodeReader.BarcodeListener {
    private static final String TAG = "HoneywellScanner";
    private static BarcodeReader barcodeReader;
    private AidcManager manager;
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {

        super.initialize(cordova, webView);

        Context context = cordova.getActivity().getApplicationContext();
        AidcManager.create(context, new CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
                barcodeReader.addBarcodeListener(HoneywellScannerPlugin.this);
                try {
                    barcodeReader.claim();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext)
    throws JSONException {
        switch (action) {
            case "test":
                testPlugin(args, callbackContext);
            case "softwareTriggerStart":
                if (barcodeReader != null) {
                    try {
                        barcodeReader.softwareTrigger(true);
                    } catch (ScannerNotClaimedException e) {
                        e.printStackTrace();
                        NotifyError("ScannerNotClaimedException");
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        NotifyError("ScannerUnavailableException");
                    }
                }
                break;
            case "softwareTriggerStop":
                if (barcodeReader != null) {
                    try {
                        barcodeReader.softwareTrigger(false);
                    } catch (ScannerNotClaimedException e) {
                        e.printStackTrace();
                        NotifyError("ScannerNotClaimedException");
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        NotifyError("ScannerUnavailableException");
                    }
                }
                break;
            case "listen":
                this.callbackContext = callbackContext;
                PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
                if (barcodeReader != null) {
                    try {
                        barcodeReader.softwareTrigger(false);
                    } catch (ScannerNotClaimedException e) {
                        e.printStackTrace();
                        NotifyError("ScannerNotClaimedException2");
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        NotifyError("ScannerUnavailableException2");
                    }
                }
                break;
            case "claim":
                if (barcodeReader != null) {
                    try {
                        barcodeReader.claim();
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        NotifyError("Scanner unavailable");
                    }
                }
                if (barcodeReader != null) {
                    try {
                        barcodeReader.softwareTrigger(false);
                    } catch (ScannerNotClaimedException e) {
                        e.printStackTrace();
                        NotifyError("ScannerNotClaimedException2");
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        NotifyError("ScannerUnavailableException2");
                    }
                }
                break;
            case "release":
                if (barcodeReader != null) {
                    barcodeReader.release();
                }
                if (barcodeReader != null) {
                    try {
                        barcodeReader.softwareTrigger(false);
                    } catch (ScannerNotClaimedException e) {
                        e.printStackTrace();
                        NotifyError("ScannerNotClaimedException2");
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                        NotifyError("ScannerUnavailableException2");
                    }
                }
                break;
        }
        return true;
    }

    private void testPlugin(JSONArray args, CallbackContext callbackContext) {
        String testStr = args.getJSONObject(0).getString("testStr");
        callbackContext.success("Tests are ok - param str: " + testStr);
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        if (this.callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, barcodeReadEvent.getBarcodeData());
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        NotifyError("Scan has failed");
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                NotifyError("The scanner is unavailable");
            }
        }
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if (barcodeReader != null) {
            barcodeReader.release();
        }
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            manager.close();
        }
    }

    private void NotifyError(String error) {
        if (this.callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, error);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }
}