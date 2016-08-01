package com.perplelab.adbrix;

import com.igaworks.IgawCommon;
import com.igaworks.adbrix.IgawAdbrix;
import com.igaworks.adbrix.interfaces.ADBrixInterface.CohortVariable;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PerpleAdbrix {
    private static final String LOG_TAG = "PerpleSDK Adbrix";

    private static Activity sMainActivity;
    private boolean mIsInit;
    private Handler mAppHandler;

    public PerpleAdbrix(Activity activity) {
        sMainActivity = activity;
    }

    public void init() {
        Log.d(LOG_TAG, "Initializing Adbrix.");

        // 필수 Permissions
        // INTERNET
        // ACCESS_NETWORK_STATE
        // 옵션 Permissions (Common 4.2.0 이상에서는 필수로 요구하지 않음)
        // READ_EXTERNAL_STORAGE
        // WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(sMainActivity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "Permission error - INTERNET permission is not granted.");
            return;
        }

        if (ContextCompat.checkSelfPermission(sMainActivity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "Permission error - ACCESS_NETWORK_STATE permission is not granted.");
            return;
        }

        IgawCommon.startApplication(sMainActivity);

        mAppHandler = new Handler();
        mIsInit = true;
    }

    public void onResume() {
        if (mIsInit) {
            IgawCommon.startSession(sMainActivity);
        }
    }

    public void onPause() {
        if (mIsInit) {
            IgawCommon.endSession();
        }
    }

    public void StartSession() {
        if (mIsInit) {
            IgawCommon.startSession(sMainActivity);
        }
    }

    public void EndSession() {
        if (mIsInit) {
            IgawCommon.endSession();
        }
    }

    public void setEvent(final String id, final String arg0, final String arg1) {
        if (!mIsInit) {
            Log.e(LOG_TAG, "Adbrix is not initialized.");
            return;
        }

        mAppHandler.post(new Runnable() {
            @Override
            public void run() {
                if (id.equals("userId")) {
                    IgawAdbrix.setUserId(arg0);
                } else if (id.equals("age")) {
                    IgawAdbrix.setAge(Integer.parseInt(arg0));
                } else if (id.equals("gender")) {
                    if (arg0.equals("MALE")) {
                        IgawAdbrix.setGender(IgawCommon.Gender.MALE);
                    } else if (arg0.equals("FEMALE")) {
                        IgawAdbrix.setGender(IgawCommon.Gender.FEMALE);
                    }
                } else if (id.equals("firstTimeExperience")) {
                    if (arg1.equals("")) {
                        // arg0 : 유저활동 이름(영어와 숫자로 된 공백없는 문자열)
                        IgawAdbrix.firstTimeExperience(arg0);
                    } else {
                        // arg0 : 유저활동 이름(영어와 숫자로 된 공백없는 문자열)
                        // arg1 : 부가 파라미터
                        IgawAdbrix.firstTimeExperience(arg0, arg1);
                    }
                } else if (id.equals("retention")) {
                    if (arg1.equals("")) {
                        // arg0 : 유저활동 이름(영어와 숫자로 된 공백없는 문자열)
                        IgawAdbrix.retention(arg0);
                    } else {
                        // arg0 : 유저활동 이름(영어와 숫자로 된 공백없는 문자열)
                        // arg1 : 부가 파라미터
                        IgawAdbrix.retention(arg0, arg1);
                    }
                } else if (id.equals("buy")) {
                    if (arg1.equals("")) {
                        // arg0 : 구매아이템 이름(영어와 숫자로 된 공백없는 문자열)
                        IgawAdbrix.buy(arg0);
                    } else {
                        // arg0 : 구매아이템 이름(영어와 숫자로 된 공백없는 문자열)
                        // arg1 : 부가 파라미터(ex, 아이템 가격)
                        IgawAdbrix.buy(arg0, arg1);
                    }
                } else if (id.equals("customCohort")) {
                    if (arg0.equals("COHORT_1")) {
                        // arg1 : in app activiy
                        IgawAdbrix.setCustomCohort(CohortVariable.COHORT_1, arg1);
                    } else if (arg0.equals("COHORT_2")) {
                        // arg1 : in app activiy
                        IgawAdbrix.setCustomCohort(CohortVariable.COHORT_2, arg1);
                    } else if (arg0.equals("COHORT_3")) {
                        // arg1 : in app activiy
                        IgawAdbrix.setCustomCohort(CohortVariable.COHORT_3, arg1);
                    }
                }
            }
        });
    }
}
