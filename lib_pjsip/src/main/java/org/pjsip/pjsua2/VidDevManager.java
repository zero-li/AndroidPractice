/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class VidDevManager {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected VidDevManager(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VidDevManager obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        throw new UnsupportedOperationException("C++ destructor does not have public access");
      }
      swigCPtr = 0;
    }
  }

    public void refreshDevs() throws java.lang.Exception {
    pjsua2JNI.VidDevManager_refreshDevs(swigCPtr, this);
  }

  public long getDevCount() {
    return pjsua2JNI.VidDevManager_getDevCount(swigCPtr, this);
  }

    public VideoDevInfo getDevInfo(int dev_id) throws java.lang.Exception {
    return new VideoDevInfo(pjsua2JNI.VidDevManager_getDevInfo(swigCPtr, this, dev_id), true);
  }

    public VideoDevInfoVector enumDev() throws java.lang.Exception {
    return new VideoDevInfoVector(pjsua2JNI.VidDevManager_enumDev(swigCPtr, this), false);
  }

    public VideoDevInfoVector2 enumDev2() throws java.lang.Exception {
        return new VideoDevInfoVector2(pjsua2JNI.VidDevManager_enumDev2(swigCPtr, this), true);
    }

    public int lookupDev(String drv_name, String dev_name) throws java.lang.Exception {
    return pjsua2JNI.VidDevManager_lookupDev(swigCPtr, this, drv_name, dev_name);
  }

  public String capName(pjmedia_vid_dev_cap cap) {
    return pjsua2JNI.VidDevManager_capName(swigCPtr, this, cap.swigValue());
  }

    public void setFormat(int dev_id, MediaFormatVideo format, boolean keep) throws java.lang.Exception {
    pjsua2JNI.VidDevManager_setFormat(swigCPtr, this, dev_id, MediaFormatVideo.getCPtr(format), format, keep);
  }

    public MediaFormatVideo getFormat(int dev_id) throws java.lang.Exception {
    return new MediaFormatVideo(pjsua2JNI.VidDevManager_getFormat(swigCPtr, this, dev_id), true);
  }

    public void setInputScale(int dev_id, MediaSize scale, boolean keep) throws java.lang.Exception {
    pjsua2JNI.VidDevManager_setInputScale(swigCPtr, this, dev_id, MediaSize.getCPtr(scale), scale, keep);
  }

    public MediaSize getInputScale(int dev_id) throws java.lang.Exception {
    return new MediaSize(pjsua2JNI.VidDevManager_getInputScale(swigCPtr, this, dev_id), true);
  }

    public void setOutputWindowFlags(int dev_id, int flags, boolean keep) throws java.lang.Exception {
    pjsua2JNI.VidDevManager_setOutputWindowFlags(swigCPtr, this, dev_id, flags, keep);
  }

    public int getOutputWindowFlags(int dev_id) throws java.lang.Exception {
    return pjsua2JNI.VidDevManager_getOutputWindowFlags(swigCPtr, this, dev_id);
  }

    public void switchDev(int dev_id, VideoSwitchParam param) throws java.lang.Exception {
    pjsua2JNI.VidDevManager_switchDev(swigCPtr, this, dev_id, VideoSwitchParam.getCPtr(param), param);
  }

  public boolean isCaptureActive(int dev_id) {
    return pjsua2JNI.VidDevManager_isCaptureActive(swigCPtr, this, dev_id);
  }

    public void setCaptureOrient(int dev_id, pjmedia_orient orient, boolean keep) throws java.lang.Exception {
    pjsua2JNI.VidDevManager_setCaptureOrient__SWIG_0(swigCPtr, this, dev_id, orient.swigValue(), keep);
  }

    public void setCaptureOrient(int dev_id, pjmedia_orient orient) throws java.lang.Exception {
    pjsua2JNI.VidDevManager_setCaptureOrient__SWIG_1(swigCPtr, this, dev_id, orient.swigValue());
  }

}
