package com.perplelab;

import com.igaworks.IgawCommon;
import com.igaworks.adbrix.IgawAdbrix;
import com.igaworks.adbrix.interfaces.ADBrixInterface.CohortVariable;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

public class PerpleAdbrix {
    private static final String LOG_TAG = "PerpleSDK";

    private static Activity sMainActivity;
    private boolean mIsInit;

    private Handler mAppHandler;

    public PerpleAdbrix(Activity activity) {
        sMainActivity = activity;
        mIsInit = false;
    }

    public void init() {
        mAppHandler = new Handler();

        IgawCommon.startApplication(sMainActivity);
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
                    if (arg0.equals("male")) {
                        IgawAdbrix.setGender(IgawCommon.Gender.MALE);
                    } else if (arg0.equals("female")) {
                        IgawAdbrix.setGender(IgawCommon.Gender.FEMALE);
                    }
                } else if (id.equals("firstTimeExperience")) {
                    if (arg1.equals("")) {
                        IgawAdbrix.firstTimeExperience(arg0);
                    } else {
                        IgawAdbrix.firstTimeExperience(arg0, arg1);
                    }
                } else if (id.equals("retention")) {
                    if (arg1.equals("")) {
                        IgawAdbrix.retention(arg0);
                    } else {
                        IgawAdbrix.retention(arg0, arg1);
                    }
                } else if (id.equals("buy")) {
                    if (arg1.equals("")) {
                        IgawAdbrix.buy(arg0);
                    } else {
                        IgawAdbrix.buy(arg0, arg1);
                    }
                } else if (id.equals("customCohort")) {
                    if (arg0.equals("COHORT_1")) {
                        IgawAdbrix.setCustomCohort(CohortVariable.COHORT_1, arg1);
                    } else if (arg0.equals("COHORT_2")) {
                        IgawAdbrix.setCustomCohort(CohortVariable.COHORT_2, arg1);
                    } else if (arg0.equals("COHORT_3")) {
                        IgawAdbrix.setCustomCohort(CohortVariable.COHORT_3, arg1);
                    }
                }
            }
        });
    }
}
