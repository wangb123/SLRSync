package com.tumao.sync.ptp.commands;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tumao.sync.ptp.Camera;
import com.tumao.sync.ptp.PtpAction;
import com.tumao.sync.ptp.PtpCamera;
import com.tumao.sync.ptp.PtpConstants;
import com.tumao.sync.ptp.model.ObjectInfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 王冰
 * @date 2018/4/19
 */
public class CopyObjectToFileAction implements PtpAction {


    private SimpleDateFormat formatParser;
    private final PtpCamera camera;
    private final Camera.ImportToFileListener listener;
    private final int objectHandle;
    private final String destPath;

    public CopyObjectToFileAction(PtpCamera camera, Camera.ImportToFileListener listener, int objectHandle, @NonNull String destPath) {
        this.camera = camera;
        this.listener = listener;
        this.objectHandle = objectHandle;
        this.destPath = destPath;

        formatParser = new SimpleDateFormat("yyyyMMdd'T'HHmmss.S");
    }

    @Override
    public void exec(PtpCamera.IO io) {
        GetObjectInfoCommand getInfo = new GetObjectInfoCommand(camera, objectHandle);
        io.handleCommand(getInfo);

        if (getInfo.getResponseCode() != PtpConstants.Response.Ok) {
            return;
        }

        ObjectInfo objectInfo = getInfo.getObjectInfo();
        if (objectInfo == null) {
            return;
        }

        Date date;
        if (TextUtils.isEmpty(objectInfo.captureDate)) {
            date = new Date();
        } else {
            try {
                date = formatParser.parse(objectInfo.captureDate);
            } catch (ParseException e) {
                date = new Date();
            }
        }
        File file = new File(destPath, date.getTime() + objectInfo.filename);

        if(!file.exists()||file.length()!=objectInfo.objectCompressedSize){
            CopyObjectToFileCommand copyObjectToFile = new CopyObjectToFileCommand(camera, objectHandle, file);
            io.handleCommand(copyObjectToFile);

            if (copyObjectToFile.getResponseCode() != PtpConstants.Response.Ok) {
                return;
            }
        }

        if (!file.exists()) {
            return;
        }

        if (listener != null) {
            listener.onImportToFile(objectHandle, objectInfo, file);
        }
    }

    @Override
    public void reset() {

    }
}
