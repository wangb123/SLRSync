package org.wbing.oss.impl;

import org.wbing.oss.UploadRes;
import org.wbing.oss.UploadTask;

import java.io.File;

/**
 * @author 王冰
 * @date 2018/4/9
 */
public class FileUploadTask extends UploadTask<FileUploadTask.FileUploadRes> {

    public FileUploadTask(File file){
        super(new FileUploadRes(file));
    }


    public static class FileUploadRes implements UploadRes {
        File file;

        public FileUploadRes(File file) {
            this.file = file;
        }

        @Override
        public File getFile() {
            return file;
        }

        @Override
        public Byte[] getByte() {
            return new Byte[0];
        }
    }
}
