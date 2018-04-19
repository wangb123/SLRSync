package com.tumao.sync.ptp.commands;

import com.tumao.sync.ptp.PtpCamera;
import com.tumao.sync.ptp.PtpConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * @author 王冰
 * @date 2018/4/19
 */
public class CopyObjectToFileCommand extends Command {

    private final String TAG = GetObjectInfoCommand.class.getSimpleName();
    private final int objectHandle;

    private File file;
    private boolean outOfMemoryError;

    public CopyObjectToFileCommand(PtpCamera camera, int objectHandle, File file) {
        super(camera);
        this.objectHandle = objectHandle;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean isOutOfMemoryError() {
        return outOfMemoryError;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        super.reset();
        file = null;
        outOfMemoryError = false;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.GetObject, objectHandle);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b.array(), 12, length - 12);
            fos.flush();
            fos.close();
            // 12 == offset of data header
//            Log.e("ByteBuffer123", b.getLong());
//            b.array()
//            inBitmap = BitmapFactory.decodeByteArray(b.array(), 12, length - 12, options);
        } catch (OutOfMemoryError e) {
            System.gc();
            outOfMemoryError = true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}
