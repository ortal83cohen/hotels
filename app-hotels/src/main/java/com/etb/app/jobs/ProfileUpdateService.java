package com.etb.app.jobs;

import android.content.Context;

import com.etb.app.App;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.member.MemberService;
import com.etb.app.member.model.User;
import com.etb.app.utils.AppLog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.squareup.okhttp.ResponseBody;

import java.util.concurrent.CountDownLatch;

import retrofit.Response;

/**
 * @author alex
 * @date 2015-08-18
 */
public class ProfileUpdateService extends GcmTaskService {
    public static final String TAG = "ProfileUpdateService";

    public static void schedule(Context context) {
        OneoffTask task = new OneoffTask.Builder()
                .setService(ProfileUpdateService.class)
                .setTag(TAG)
                .setExecutionWindow(10, 130) // up to 2 minutes
                .setUpdateCurrent(true) // this task should override any preexisting tasks with the same tag
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .build();

        AppLog.d(task.toString());
        GcmNetworkManager.getInstance(context).schedule(task);
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        AppLog.d(taskParams.toString());
        User user = App.provide(getApplicationContext()).memberStorage().loadUser();

        MemberService memberService = App.provide(getApplicationContext()).memberService();

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] resultSuccess = {false};
        memberService.profile(user.profile).enqueue(new RetrofitCallback<Void>() {
            @Override
            protected void failure(ResponseBody response, boolean isOffline) {
                latch.countDown();
            }

            @Override
            protected void success(Void body, Response<Void> response) {
                resultSuccess[0] = true;
                latch.countDown();
            }

        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            AppLog.e(e);
            return GcmNetworkManager.RESULT_FAILURE;
        }

        if (resultSuccess[0]) {
            return GcmNetworkManager.RESULT_SUCCESS;
        }

        return GcmNetworkManager.RESULT_FAILURE;
    }
}
