package org.wbing.oss;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.MultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;

import org.wbing.oss.compress.Luban;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploaderService extends Service {
    private static final String ACCESS_KEY = "oqNB0HXSR7aFK86P"; // 测试代码没有考虑AK/SK的安全性
    private static final String SCRECT_KEY = "8lMAnyPDSfPyEsxeZw0z0ZM3Bqet0B";
    private static final String BUCKET_NAME = "tomoyunshequ";
    private static final String BUCKET_PATH = "camera";

    public static void start(Context context) {
        Intent starter = new Intent(context, UploaderService.class);
        context.startService(starter);
    }

    public static void bind(Context context, ServiceConnection conn) {
        Intent starter = new Intent(context, UploaderService.class);
        context.bindService(starter, conn, Context.BIND_AUTO_CREATE);
    }

    private final int MSG_LOOPER_TASK = 2018042001;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOOPER_TASK:
                    removeMessages(MSG_LOOPER_TASK);
                    addTask(looperTask());
                    sendEmptyMessageDelayed(MSG_LOOPER_TASK, 1000);
                    break;
            }
        }
    };
    private OSS oss;
    private transient int taskCount = 0;
    private transient Map<String, OSSAsyncTask> taskMap = new HashMap<>();

    public UploaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(this, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initOss();
        handler.sendEmptyMessageDelayed(MSG_LOOPER_TASK, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initOss() {
        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
        OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil
                // 以下是用本地算法进行的演示
                return OSSUtils.sign(ACCESS_KEY, SCRECT_KEY, content);
            }
        };
        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    private UploadTask looperTask() {
        if (taskCount >= 5) {
            return null;
        }
        return UploaderEngine.instance().pullWaitingTask();
    }

    private void addTask(final UploadTask task) {
        if (task == null) {
            return;
        }
        UploadRes res = task.getRes();
        if (res == null) {
            return;
        }

        if (!TextUtils.isEmpty(task.getUrl())) {
            UploaderEngine.instance().getUploadTaskListener().onComplete(task);
            return;
        }

//        HeadObjectRequest head = new HeadObjectRequest("tomoyunshequ", "ca");


        File file = res.getFile();
        byte[] bytes = res.getByte();
        if (file != null && file.exists()) {
            uploadFile(task);
        } else if (bytes != null && bytes.length > 0) {
            uploadByte(task);
        }
    }


    private void uploadFile(@NonNull final UploadTask task) {
        String path = task.getRes().getFile().getAbsolutePath();
        try {
            path = Luban.with(getBaseContext())
                    .width(1080)
                    .height(1920)
                    .ignoreBy(200)
                    .name(task.getId())
                    .get(path).getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String objectKey = BUCKET_PATH + "/" + task.getId() + checkSuffix(path);
        try {
            UploaderEngine.instance().getUploadTaskListener().onProgress(task, 0, 0);
            if (oss.doesObjectExist(BUCKET_NAME, objectKey)) {
                task.setUrl(objectKey);
                UploaderEngine.instance().getUploadTaskListener().onComplete(task);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            UploaderEngine.instance().getUploadTaskListener().onError(task, new Throwable(e));
        }


        ResumableUploadRequest put = new ResumableUploadRequest(BUCKET_NAME, objectKey, path);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<MultipartUploadRequest>() {
            @Override
            public void onProgress(MultipartUploadRequest request, long currentSize, long totalSize) {
                UploaderEngine.instance().getUploadTaskListener().onProgress(task, currentSize, totalSize);
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask ossAsyncTask = oss.asyncResumableUpload(put, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                taskCount--;
                taskMap.remove(task.getId());
                task.setUrl(objectKey);
                UploaderEngine.instance().getUploadTaskListener().onComplete(task);
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientException, ServiceException serviceException) {
                taskCount--;
                taskMap.remove(task.getId());
                String error = "";
                // 请求异常
                if (clientException != null) {
                    // 本地异常如网络异常等
                    clientException.printStackTrace();
                    error += clientException.getMessage();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());

                    error += serviceException.getMessage();
                }

                UploaderEngine.instance().getUploadTaskListener().onError(task, new Throwable(error));
            }
        });

        taskCount++;
        taskMap.put(task.getId(), ossAsyncTask);
    }

    private void uploadByte(@NonNull UploadTask task) {

    }

    public String checkSuffix(String path) {
        if (TextUtils.isEmpty(path)) {
            return ".jpg";
        }
        return path.substring(path.lastIndexOf("."), path.length());
    }

    public static final class ServiceBinder extends Binder {
        UploaderService service;
        Intent intent;

        public ServiceBinder(UploaderService service, Intent intent) {
            this.service = service;
            this.intent = intent;
        }

        public UploaderService getService() {
            return service;
        }

        public Intent getIntent() {
            return intent;
        }

    }
}
