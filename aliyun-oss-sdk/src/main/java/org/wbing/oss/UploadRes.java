package org.wbing.oss;

import java.io.File;

/**
 * @author 王冰
 * @date 2018/4/10
 */
public interface UploadRes {
    File getFile();

    byte[] getByte();
}
