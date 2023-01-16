package com.plusdt.phonecallstate;


import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.os.RemoteException;
import androidx.core.content.ContextCompat;
import android.content.Context;

import android.app.Activity;
import io.flutter.app.FlutterActivity;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

// TODO: Implement NEW_OUTGOING_CALL Intent broadcast receiver
// TODO: Implement event direction

/**
 * PhonecallstatePlugin
 */
public class PhonecallstatePlugin implements MethodCallHandler {
  private final MethodChannel channel;
  private Activity activity;
  private static final String TAG = "KORDON";//MyClass.class.getSimpleName();
  private boolean callStateListenerRegistered = false;
  TelephonyManager tm;

  //private PhoneStateListener mPhoneListener;

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.plusdt.phonecallstate");
    channel.setMethodCallHandler(new PhonecallstatePlugin(registrar.activity(), channel));
  }



  PhonecallstatePlugin(Activity activity, MethodChannel channel) {
    this.activity = activity;
    this.channel = channel;
    this.channel.setMethodCallHandler(this);

     if (!callStateListenerRegistered) {
      TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
          telephonyManager.registerTelephonyCallback(getMainExecutor(), callStateListener);
          callStateListenerRegistered = true;
        }
      } else {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        callStateListenerRegistered = true;
      }
    }

  }


  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result response) {
    if (call.method.equals("phoneTest.PhoneIncoming")) {
        Log.i(TAG,"phoneIncoming Test implementation");
      // TODO: test mode with seconds to wait as parameter
    }
    else {
      response.notImplemented();
    }

  }


  private PhoneStateListener mPhoneListener = new PhoneStateListener() {
    public void onCallStateChanged(int state, String incomingNumber) {
      try {
        switch (state) {
          case TelephonyManager.CALL_STATE_IDLE:
            channel.invokeMethod("phone.disconnected", true);
            break;
          case TelephonyManager.CALL_STATE_RINGING:
            channel.invokeMethod("phone.incoming", true);
            break;
          case TelephonyManager.CALL_STATE_OFFHOOK:
            channel.invokeMethod("phone.connected", true);
            break;

          default:
            Log.d(TAG, "Unknown phone state=" + String.valueOf(state));
        }
      } catch (Exception e) {
        Log.e("TAG","Exception");
      }
    }
  };


}
