package com.tumao.sync.bean;

import android.media.ExifInterface;
import android.support.annotation.NonNull;

import com.tumao.sync.App;

/**
 * capture
 * @author 王冰
 * @date 2018/4/12
 */
public class SLRExifInfo {

    public static SLRExifInfo createByJson(String paramString) {
        SLRExifInfo info = App.getApp().getGson().fromJson(paramString, SLRExifInfo.class);
        info.resultJson = paramString;
        return info;
    }

    public static SLRExifInfo createByExif(@NonNull ExifInterface exifInterface) {
        SLRExifInfo info = new SLRExifInfo();
        info.orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        info.dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        info.make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        info.model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        info.flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        info.imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        info.imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        info.latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        info.longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        info.latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        info.longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        info.exposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        info.aperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
        info.isoSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
        info.dateTimeDigitized = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
        info.subSecTime = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME);
        info.subSecTimeOrig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIG);
        info.subSecTimeDig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_DIG);
        info.altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
        info.altitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
        info.gpsTimeStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
        info.gpsDateStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
        info.whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
        info.focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        info.processingMethod = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
        info.resultJson = App.getApp().getGson().toJson(info);
        return info;
    }


    private String resultJson;
    private String orientation;//角度中心
    private String dateTime;//时间
    private String make;//
    private String model;
    private String flash;
    private String imageLength;
    private String imageWidth;
    private String latitude;
    private String longitude;
    private String latitudeRef;
    private String longitudeRef;
    private String exposureTime;
    private String aperture;
    private String isoSpeedRatings;
    private String dateTimeDigitized;
    private String subSecTime;
    private String subSecTimeOrig;
    private String subSecTimeDig;
    private String altitude;
    private String altitudeRef;
    private String gpsTimeStamp;
    private String gpsDateStamp;
    private String whiteBalance;
    private String focalLength;
    private String processingMethod;

    public SLRExifInfo() {
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public String getImageLength() {
        return imageLength;
    }

    public void setImageLength(String imageLength) {
        this.imageLength = imageLength;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitudeRef() {
        return latitudeRef;
    }

    public void setLatitudeRef(String latitudeRef) {
        this.latitudeRef = latitudeRef;
    }

    public String getLongitudeRef() {
        return longitudeRef;
    }

    public void setLongitudeRef(String longitudeRef) {
        this.longitudeRef = longitudeRef;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getIsoSpeedRatings() {
        return isoSpeedRatings;
    }

    public void setIsoSpeedRatings(String isoSpeedRatings) {
        this.isoSpeedRatings = isoSpeedRatings;
    }

    public String getDateTimeDigitized() {
        return dateTimeDigitized;
    }

    public void setDateTimeDigitized(String dateTimeDigitized) {
        this.dateTimeDigitized = dateTimeDigitized;
    }

    public String getSubSecTime() {
        return subSecTime;
    }

    public void setSubSecTime(String subSecTime) {
        this.subSecTime = subSecTime;
    }

    public String getSubSecTimeOrig() {
        return subSecTimeOrig;
    }

    public void setSubSecTimeOrig(String subSecTimeOrig) {
        this.subSecTimeOrig = subSecTimeOrig;
    }

    public String getSubSecTimeDig() {
        return subSecTimeDig;
    }

    public void setSubSecTimeDig(String subSecTimeDig) {
        this.subSecTimeDig = subSecTimeDig;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAltitudeRef() {
        return altitudeRef;
    }

    public void setAltitudeRef(String altitudeRef) {
        this.altitudeRef = altitudeRef;
    }

    public String getGpsTimeStamp() {
        return gpsTimeStamp;
    }

    public void setGpsTimeStamp(String gpsTimeStamp) {
        this.gpsTimeStamp = gpsTimeStamp;
    }

    public String getGpsDateStamp() {
        return gpsDateStamp;
    }

    public void setGpsDateStamp(String gpsDateStamp) {
        this.gpsDateStamp = gpsDateStamp;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getProcessingMethod() {
        return processingMethod;
    }

    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }

    @Override
    public String toString() {
        return "SLRExifInfo{" +
                "resultJson='" + resultJson + '\'' +
                ", orientation='" + orientation + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", flash='" + flash + '\'' +
                ", imageLength='" + imageLength + '\'' +
                ", imageWidth='" + imageWidth + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitudeRef='" + latitudeRef + '\'' +
                ", longitudeRef='" + longitudeRef + '\'' +
                ", exposureTime='" + exposureTime + '\'' +
                ", aperture='" + aperture + '\'' +
                ", isoSpeedRatings='" + isoSpeedRatings + '\'' +
                ", dateTimeDigitized='" + dateTimeDigitized + '\'' +
                ", subSecTime='" + subSecTime + '\'' +
                ", subSecTimeOrig='" + subSecTimeOrig + '\'' +
                ", subSecTimeDig='" + subSecTimeDig + '\'' +
                ", altitude='" + altitude + '\'' +
                ", altitudeRef='" + altitudeRef + '\'' +
                ", gpsTimeStamp='" + gpsTimeStamp + '\'' +
                ", gpsDateStamp='" + gpsDateStamp + '\'' +
                ", whiteBalance='" + whiteBalance + '\'' +
                ", focalLength='" + focalLength + '\'' +
                ", processingMethod='" + processingMethod + '\'' +
                '}';
    }
}
