package com.tumao.sync;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.mtp.MtpConstants;
import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tumao.sync.ptp.PtpConstants;
import com.tumao.sync.ptp.PtpUsbConnection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author 王冰
 * @date 2018/4/10
 */
public class SLRDevice {

    private static final String TAG = SLRDevice.class.getSimpleName();


    private File root;
    private List<File> files;

    private UsbDeviceConnection deviceConnection;

    private final UsbManager usbManager;
    public final UsbDevice usbDevice;
    private final UsbInterface usbInterface;
    private UsbEndpoint outEndpoint = null;
    private UsbEndpoint inEndpoint = null;

    private boolean inited = false;

    private SLRDevice(UsbManager usbManager, UsbDevice usbDevice,
                      UsbInterface usbInterface, UsbEndpoint inEndpoint, UsbEndpoint outEndpoint) {
        this.usbManager = usbManager;
        this.usbDevice = usbDevice;
        this.usbInterface = usbInterface;
        this.inEndpoint = inEndpoint;
        this.outEndpoint = outEndpoint;

        this.files = new ArrayList<>();

        String name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            name = usbDevice.getProductName();
        } else {
            name = "SLRDevice";
        }
        if (TextUtils.isEmpty(name)) {
            name = "unknown";
        }
        root = new File(App.getApp().getExternalSLRThumbDir(), name.trim());
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public File getRoot() {
        return root;
    }

    public List<File> getFiles() {
        return files;
    }

    public static SLRDevice[] getSLRDevices(Context context) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        assert manager != null;
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Collection<UsbDevice> values = deviceList.values();
        List<SLRDevice> usbDevices = new ArrayList<>();

        for (UsbDevice device : values) {
            int interfaceCount = device.getInterfaceCount();
            for (int i = 0; i < interfaceCount; i++) {
                UsbInterface usbInterface = device.getInterface(i);

                Log.i(TAG, "InterfaceClass:" + usbInterface.getInterfaceClass());
                if (usbInterface.getInterfaceClass() != UsbConstants.USB_CLASS_STILL_IMAGE) {
                    Log.i(TAG, "device interface not suitable!");
                    continue;
                }
                int endpointCount = usbInterface.getEndpointCount();
                if (endpointCount != 3) {
                    continue;
                }

                UsbEndpoint outEndpoint = null;
                UsbEndpoint inEndpoint = null;
                for (int j = 0; j < endpointCount; j++) {
                    UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                    Log.i(TAG, "found usb endpoint: " + endpoint);
                    if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                            outEndpoint = endpoint;
                            Log.e(TAG, "out:" + endpoint);
                        } else if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                            inEndpoint = endpoint;
                            Log.e(TAG, "in:" + endpoint);
                        }
                    }
                }
                if (outEndpoint == null || inEndpoint == null) {
                    Log.e(TAG, "Not all needed endpoints found!");
                    continue;
                }
                usbDevices.add(new SLRDevice(manager, device, usbInterface, inEndpoint, outEndpoint));
            }
        }
        return usbDevices.toArray(new SLRDevice[0]);
    }

    public void init() throws IOException {
        files.clear();
        if (usbManager.hasPermission(usbDevice))
            setupDevice();
        else
            throw new IllegalStateException("Missing permission to access usb device: " + usbDevice);
        inited = true;
    }


    private void setupDevice() throws IOException {
        Log.d(TAG, "setup device");

        PtpUsbConnection connection = new PtpUsbConnection(usbManager.openDevice(usbDevice), inEndpoint, outEndpoint,
                usbDevice.getVendorId(), usbDevice.getProductId());
        if (usbDevice.getVendorId() == PtpConstants.CanonVendorId) {

        } else if (usbDevice.getVendorId() == PtpConstants.NikonVendorId) {

        }

//        deviceConnection = usbManager.openDevice(usbDevice);
//        if (deviceConnection == null) {
//            throw new IOException("deviceConnection is null!");
//        }
//
//        boolean claim = deviceConnection.claimInterface(usbInterface, true);
//        if (!claim) {
//            throw new IOException("could not claim interface!");
//        }

//        MtpDevice mtpDevice = new MtpDevice(usbDevice);
//        if (!mtpDevice.open(deviceConnection)) {
//            throw new IOException("could not open mtpDevice!");
//        }
//        int[] storageIds = mtpDevice.getStorageIds();
//        if (storageIds == null) {
//            return;
//        }
//        for (int storageId : storageIds) {
//            scanObjectsInStorage(mtpDevice, storageId, 0, 0);
//        }
//        mtpDevice.close();
    }

    public void close() {
        Log.d(TAG, "close device");
        if (deviceConnection == null) return;
        deviceConnection.close();
        boolean release = deviceConnection.releaseInterface(usbInterface);
        if (!release) {
            Log.e(TAG, "could not release interface!");
        }
        inited = false;
    }


    private void scanObjectsInStorage(MtpDevice mtpDevice, int storageId, int format, int parent) {
        int[] objectHandles = mtpDevice.getObjectHandles(storageId, format, parent);

        if (objectHandles == null) {
            return;
        }
        for (int objectHandle : objectHandles) {
            MtpObjectInfo mtpObjectInfo = mtpDevice.getObjectInfo(objectHandle);
            if (mtpObjectInfo == null) {
                return;
            }
            int parentOfObject = mtpObjectInfo.getParent();
            if (parentOfObject != parent) {
                continue;
            }
            int associationType = mtpObjectInfo.getAssociationType();
            if (associationType == MtpConstants.ASSOCIATION_TYPE_GENERIC_FOLDER) {
                Log.e(TAG, mtpObjectInfo.getName());
                scanObjectsInStorage(mtpDevice, storageId, format, objectHandle);
            } else if (mtpObjectInfo.getFormat() == MtpConstants.FORMAT_EXIF_JPEG && mtpObjectInfo.getProtectionStatus() != MtpConstants.PROTECTION_STATUS_NON_TRANSFERABLE_DATA) {
//                list.add(mtpObjectInfo);
                Log.e(TAG, mtpObjectInfo.getName());

                File file = new File(root, mtpObjectInfo.getDateCreated() + mtpObjectInfo.getName());
                if (!file.exists() || file.length() != mtpObjectInfo.getCompressedSize()) {
                    mtpDevice.importFile(mtpObjectInfo.getObjectHandle(), file.getAbsolutePath());
                }
                files.add(file);
            }
        }
    }
}

