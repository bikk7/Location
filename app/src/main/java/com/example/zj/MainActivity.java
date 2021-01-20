package com.example.zj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.example.file.FileUtils;
import com.example.service.LiveService;
import com.example.service.MyJobService;
import com.example.service.ScreenBroadcastListener;
import com.example.service.ScreenManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //定位客户端
    public LocationClient mLocationClient = null;

    //监听函数
    public BDLocationListener myListener = new MyLocationListener();



    //界面显示的字符
    private TextView positionText;

    FileUtils fileUtils = new FileUtils();

    ///////
    static Date fileDate=new Date(System.currentTimeMillis());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //启动MyService
      //  Intent startIntent = new Intent(this, MyService.class);
       // startService(startIntent);

////     //启动LiveService
//        Intent startIntent1 = new Intent(this, LiveService.class);
//        startService(startIntent1);

//        Intent intent = new Intent();
//        intent.setClass(this,LiveService.class);
//        this.startService(intent);
//       //启动MyJobService
//       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            Intent startIntent2 = new Intent(this, MyJobService.class);
//            startService(startIntent2);
//        }
        //获取应用程序上下文
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);


        setContentView(R.layout.activity_main);

//        final ScreenManager screenManager = ScreenManager.getInstance(MainActivity.this);
//        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
//        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
//            @Override
//            public void onScreenOn() {
//                screenManager.finishActivity();
//            }
//
//            @Override
//            public void onScreenOff() {
//                screenManager.startActivity();
//            }
//        });



        positionText = (TextView) findViewById(R.id.position_text_view);


        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

       /* final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                requestLocation();
                handler.postDelayed(this, 4000);
            }
        });*/
    }
    public String getPhoneNum(){           //////////////////////////////
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try {
            in=openFileInput("phonenumber");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null)
            {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(reader !=null)
            {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String d=content.toString();
        return d;
    }





    //初始化location设置
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        // LocationMode.Hight_Accuracy：高精度；
        // LocationMode. Battery_Saving：低功耗；
        // LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("gcj02");
        //可选，设置返回经纬度坐标类型，默认gcj02
        // gcj02：国测局坐标；
        // bd09ll：百度经纬度坐标；
        // bd09：百度墨卡托坐标；
        // 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标


        //每4s重发一次请求
        option.setScanSpan(3000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        // 如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true
        option.disableCache(true);
        option.setOpenAutoNotifyMode();
        option.setIsNeedLocationPoiList(true);
//可选，是否需要周边POI信息，默认为不需要，即参数为false
//如果开发者需要获得周边POI信息，此处必须为true

        option.setIsNeedLocationDescribe(true);
//可选，是否需要位置描述信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的位置信息，此处必须为true

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        // 需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        // 更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    String s;
    public void save(String a) {
        /////////
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss
//        System.out.println("创建文件时的日期"+simpleDateFormat.format(fileDate));
        FileOutputStream out = null;
        BufferedWriter writer = null;


        try {
//            if(!fileUtils.isFileExist("糖猪猪")){
//                fileUtils.createSDDir("糖猪猪");
//            }
//            String s="糖猪猪/"+getPhoneNum()+"-"+simpleDateFormat.format(fileDate)+".txt";
//           if(!fileUtils.isFileExist(s)&&fileUtils.getFileNum("糖猪猪")==0){
//                fileUtils.createSDFile(s);
//            }
            out = new FileOutputStream(fileUtils.getSDPATH()+s,true);
//            File saveFile = new File(fileUtils.getSDPATH()+"糖猪猪/"+fileUtils.getFileName("糖猪猪/"));
//            System.out.println(saveFile.length());
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(a);
            writer.flush();

//            //如如果文件大于200字节则进行传送，字节大小可以修改
//            if(saveFile.length()>4025){
//                FileTransferClient fileTransferClient = new FileTransferClient(); // 启动客户端连接
//                fileTransferClient.start(); // 传输文件
//                return;
//            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestLocation() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss
        System.out.println("创建文件时的日期"+simpleDateFormat.format(fileDate));

        if(!fileUtils.isFileExist("糖猪猪")){
            fileUtils.createSDDir("糖猪猪");
        }
        s="糖猪猪/"+getPhoneNum()+"-"+simpleDateFormat.format(fileDate)+".txt";
        if(!fileUtils.isFileExist(s)){
            try {
                fileUtils.createSDFile(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        initLocation();
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            StringBuilder timePosition = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
            //String s=simpleDateFormat.format(new Date());
            String s=location.getTime();
            String date=s.substring(0,10);String time=s.substring(11,19);
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经度：").append(location.getLongitude()).append("\n");
//            currentPosition.append("速度: ").append(location.getSpeed()).append("\n");
            currentPosition.append("日期：").append(date).append("\n");
            currentPosition.append("时间：").append(time).append("\n");

            timePosition.append(date + ",");
            timePosition.append(time + ",");
            timePosition.append(location.getLongitude() + ",");
            timePosition.append(location.getLatitude());
            currentPosition.append("详细地址：").append(location.getAddrStr()).append("\n");
//            currentPosition.append("位置语义化信息：").append(location.getLocationDescribe()).append("\n");
//            List<Poi> poiList = location.getPoiList();
//            if(poiList!=null)
//            {
//                currentPosition.append("周围POI数量:").append(poiList.size()).append("\n");
//                for(Poi p:poiList)
//                {
//                    currentPosition.append("POI ID:").append(p.getId()).append("  POI 名称:").append(p.getName()).append("  POI 概率值：").append(p.getRank()).append("\n");
//                }
//            }
//            else
//                currentPosition.append("周围没有POI").append("\n");

            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS\n");
                currentPosition.append("卫星数量:").append(location.getSatelliteNumber()).append("\n");
                currentPosition.append("信号质量:").append(location.getGpsAccuracyStatus()).append("\n");
                timePosition.append(",GPS,");
                timePosition.append("卫星数量:").append(location.getSatelliteNumber()).append(",");
                timePosition.append("信号质量:").append(location.getGpsAccuracyStatus()).append("\r\n");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络\n");
                timePosition.append(",网络").append("\r\n");
            }
            else
            {
                timePosition.append("\r\n");
            }
            positionText.setText(currentPosition);

          /*  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
            Date date = new Date(System.currentTimeMillis());*/

            save(timePosition.toString());

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    /*文件传输部分
    上传到服务器
     */

    public class FileTransferClient implements Runnable {

      private static final String SERVER_IP = "134.175.148.17"; // 服务端IP
      // private static final String SERVER_IP = "192.168.1.103"; // 服务端IP
        private static final int SERVER_PORT = 8896; // 服务端端口

        private Socket client;
        private Thread t;
        private FileInputStream fis;

        private DataOutputStream dos;

        /**
         * 构造函数<br/>
         * 与服务器建立连接
         *
         * @throws Exception
         */


        /**
         * 向服务端传输文件
         *
         * @throws Exception
         */
        public void sendFile() throws Exception {
            try {
                File file = new File(fileUtils.getSDPATH()+"糖猪猪/"+fileUtils.getFileName("糖猪猪/"));
                if (!file.exists()) {
                    System.out.println("文件不存在！");
                } else {
                    fis = new FileInputStream(file);
                    dos = new DataOutputStream(client.getOutputStream());


                    // 文件名和长度
                    dos.writeUTF(file.getName());
                    dos.flush();
                    dos.writeLong(file.length());
                    dos.flush();

                    // 开始传输文件
                    System.out.println("======== 开始传输文件 ========");
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    long progress = 0;
                    while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                        dos.write(bytes, 0, length);
                        dos.flush();
                        progress += length;
                        System.out.print("| " + (100 * progress / file.length()) + "% |");
                    }
                    System.out.println();
                    System.out.println("======== 文件传输成功 ========");
                    //////////
                    file.delete();

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    fis.close();
                if (dos != null)
                    dos.close();
                client.close();
            }
        }

        @Override
        public void run() {
            try {
                this.client = new Socket(SERVER_IP, SERVER_PORT);
                System.out.println("Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");
                this.sendFile();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
        }
    }
}
