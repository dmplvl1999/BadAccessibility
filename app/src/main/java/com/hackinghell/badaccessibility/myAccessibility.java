package com.hackinghell.badaccessibility;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class myAccessibility extends AccessibilityService {

    private static myAccessibility instance;
    private Handler handler = new Handler();


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("tags", "event accesseddddd with button" + event);
    }

    public static myAccessibility getInstance() {return instance;}

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
            Log.i("tag", "connected services");
            goToHomePage();
            onStartHandlerforApp();
    }

    private void goToHomePage() {
        Log.i("nodes", "Inside go to home screen");
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    private void onStartHandlerforApp() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Malware accessbilityMalware = new Malware();
                AccessibilityNodeInfo pageNode = getRootInActiveWindow();
                if (pageNode!=null) {
                    //Goes to method to find and open chrome
                    onFindandOpenChrome(pageNode);
                }
            }
        }, 1000); // For adjusting Delay
    }

    private void onFindandOpenChrome(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) return;
        List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByText("Chrome");
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            if (nodeInfo != null && nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        }
    }
}
