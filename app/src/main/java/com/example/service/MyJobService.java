package com.example.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.zj.MainActivity;
import com.example.zj.R;

import java.util.List;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    public MyJobService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG","服务启动"+isRunningApp(this,"com.example.zj"));
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyJobService.class.getName()));
        builder.setPeriodic(100);
        builder.setPersisted(true);
//        builder.setRequiredNetworkType(NETWORK_TYPE_ANY);
//        builder.setRequiresCharging(false);
//        builder.setRequiresDeviceIdle(false);

        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
//        boolean isLocalServiceWork = isServiceWork(this, "com.example.service.LiveService");
        boolean isrunningapp=isRunningApp(this,"com.example.zj");
        if (isrunningapp){
            Log.d("TAG4","on");
        }else{
            Log.d("TAG4","off");
           startActivity(new Intent(this,MainActivity.class));
//         startService(new Intent(this,MyService.class));
        }
//        Toast.makeText(this,"ok", Toast.LENGTH_SHORT).show();

//        if (isrunningapp){
//            Log.d("TAG5","on");
//        }else{
//            Log.d("TAG5","off");
//            startActivity(new Intent(this,MainActivity.class));
//        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("TAG6","onStopJob检测");
        return false;
    }
    // 判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            Log.d("TAG11",mName);
            if (mName.equals(serviceName)) {
                isWork = true;

            }
        }
        return isWork;
    }
    //判断进程是否正在运行
    public static boolean isRunningApp(Context context, String packageName) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        return isAppRunning;
    }
    private void startForeground() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("Running")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        startForeground(9999,notification);
    }

}

