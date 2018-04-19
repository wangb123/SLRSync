package com.tumao.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.mtp.MtpConstants;
import android.mtp.MtpDevice;
import android.mtp.MtpObjectInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tumao.sync.ptp.Camera;
import com.tumao.sync.ptp.EosCamera;
import com.tumao.sync.ptp.NikonCamera;
import com.tumao.sync.ptp.PtpCamera;
import com.tumao.sync.ptp.PtpConstants;
import com.tumao.sync.ptp.PtpUsbConnection;
import com.tumao.sync.ptp.WorkerNotifier;
import com.tumao.sync.ptp.model.LiveViewData;
import com.tumao.sync.ptp.model.ObjectInfo;

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

    private final OnSLRDeviceFileScanListener slrDeviceFileScanListener = new OnSLRDeviceFileScanListener() {
        @Override
        public void onScanStart() {
            if (deviceFileScanListener != null) {
                deviceFileScanListener.onScanStart();
            }
        }

        @Override
        public void onFileAdd(File file) {
            if (deviceFileScanListener != null) {
                deviceFileScanListener.onFileAdd(file);
            }
        }

        @Override
        public void onScanEnd(List<File> files) {
            if (deviceFileScanListener != null) {
                deviceFileScanListener.onScanEnd(files);
            }
        }
    };
    private OnSLRDeviceFileScanListener deviceFileScanListener;

    private File root;
    private List<File> files;
    private PtpCamera camera;
    private Camera.CameraListener listener = new Camera.CameraListener() {
        @Override
        public void onCameraStarted(Camera camera) {
            Log.i(TAG, "onCameraStarted");
        }

        @Override
        public void onCameraStopped(Camera camera) {
            Log.i(TAG, "onCameraStopped");
        }

        @Override
        public void onNoCameraFound() {
            Log.i(TAG, "onNoCameraFound");
        }

        @Override
        public void onError(String message) {
            Log.i(TAG, "onError");
        }

        @Override
        public void onPropertyChanged(int property, int value) {
            Log.i(TAG, "onPropertyChanged");
        }

        @Override
        public void onPropertyStateChanged(int property, boolean enabled) {
            Log.i(TAG, "onPropertyStateChanged");
        }

        @Override
        public void onPropertyDescChanged(int property, int[] values) {
            Log.i(TAG, "onPropertyDescChanged");
        }

        @Override
        public void onLiveViewStarted() {
            Log.i(TAG, "onLiveViewStarted");
        }

        @Override
        public void onLiveViewData(LiveViewData data) {
            Log.i(TAG, "onLiveViewData");
        }

        @Override
        public void onLiveViewStopped() {
            Log.i(TAG, "onLiveViewStopped");
        }

        @Override
        public void onCapturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap) {
            Log.i(TAG, "onCapturedPictureReceived");
        }

        @Override
        public void onBulbStarted() {
            Log.i(TAG, "onBulbStarted");
        }

        @Override
        public void onBulbExposureTime(int seconds) {
            Log.i(TAG, "onBulbExposureTime");
        }

        @Override
        public void onBulbStopped() {
            Log.i(TAG, "onCameraStarted");
        }

        @Override
        public void onFocusStarted() {
            Log.i(TAG, "onFocusStarted");
        }

        @Override
        public void onFocusEnded(boolean hasFocused) {
            Log.i(TAG, "onFocusEnded");
        }

        @Override
        public void onFocusPointsChanged() {
            Log.i(TAG, "onFocusPointsChanged");
        }

        @Override
        public void onObjectAdded(int handle, int format) {
            Log.i(TAG, "onObjectAdded");
            if (camera == null) {
                return;
            }
            if (format != PtpConstants.ObjectFormat.EXIF_JPEG) {
                return;
            }
            importImageToFile(handle);
        }
    };

    private final UsbManager usbManager;
    public final UsbDevice usbDevice;
    private final UsbInterface usbInterface;
    private UsbEndpoint outEndpoint = null;
    private UsbEndpoint inEndpoint = null;

    private PtpUsbConnection connection;
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


    public void setDeviceFileScanListener(OnSLRDeviceFileScanListener deviceFileScanListener) {
        this.deviceFileScanListener = deviceFileScanListener;
    }

    public void init(OnSLRDeviceFileScanListener deviceFileScanListener) throws IOException {
        files.clear();
        this.deviceFileScanListener = deviceFileScanListener;
        if (usbManager.hasPermission(usbDevice))
            setupDevice();
        else
            throw new IllegalStateException("Missing permission to access usb device: " + usbDevice);
        inited = true;
    }


    private void setupDevice() throws IOException {
        Log.d(TAG, "setup device");
        if (camera != null) {
            camera.shutdown();
            camera = null;
        }
        connection = new PtpUsbConnection(usbManager.openDevice(usbDevice), inEndpoint, outEndpoint,
                usbDevice.getVendorId(), usbDevice.getProductId());
        if (usbDevice.getVendorId() == PtpConstants.CanonVendorId) {
            camera = new EosCamera(connection, listener, new WorkerNotifier(App.getApp()));
        } else if (usbDevice.getVendorId() == PtpConstants.NikonVendorId) {
            camera = new NikonCamera(connection, listener, new WorkerNotifier(App.getApp()));
        }
        if (camera != null) {
            camera.retrieveStorages(new Camera.StorageInfoListener() {
                List<Integer> handleList = new ArrayList<>();

                @Override
                public void onStorageFound(int handle, String label) {
                    handleList.add(handle);
                }

                @Override
                public void onAllStoragesFound() {
                    files.clear();
                    slrDeviceFileScanListener.onScanStart();
                    for (Integer handle : handleList) {
                        if (camera == null) {
                            return;
                        }
                        camera.retrieveImageHandles(this,
                                handle,
                                PtpConstants.ObjectFormat.EXIF_JPEG);
                    }
                }

                @Override
                public void onImageHandlesRetrieved(int[] handles) {
                    if (handles == null || handles.length == 0) {
                        return;
                    }
                    if (camera == null) {
                        return;
                    }
                    for (int handle : handles) {
                        importImageToFile(handle);
                    }
                    slrDeviceFileScanListener.onScanEnd(files);
                }
            });
        }
//        int[] storageIds = usbDevice.getStorageIds();
//        if (storageIds == null) {
//            return;
//        }
//        for (int storageId : storageIds) {
//            scanObjectsInStorage(mtpDevice, storageId, 0, 0);
//        }
//        camera.retrieveImageHandles();

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
        if (connection == null) return;
        connection.close();
        inited = false;
    }

    private void importImageToFile(int handle) {
        camera.importImageToFile(handle, new Camera.ImportToFileListener() {
            @Override
            public void onImportToFile(int objectHandle, ObjectInfo objectInfo, File file) {
                files.add(file);
                slrDeviceFileScanListener.onFileAdd(file);
            }
        }, root.getAbsolutePath());
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


    public interface OnSLRDeviceFileScanListener {

        void onScanStart();

        void onFileAdd(File file);

        void onScanEnd(List<File> files);
    }

}

