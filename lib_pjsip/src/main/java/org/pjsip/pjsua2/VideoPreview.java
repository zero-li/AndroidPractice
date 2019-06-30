/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class VideoPreview {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected VideoPreview(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VideoPreview obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_VideoPreview(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VideoPreview(int dev_id) {
    this(pjsua2JNI.new_VideoPreview(dev_id), true);
  }

  public boolean hasNative() {
    return pjsua2JNI.VideoPreview_hasNative(swigCPtr, this);
  }

    public void start(VideoPreviewOpParam param) throws java.lang.Exception {
    pjsua2JNI.VideoPreview_start(swigCPtr, this, VideoPreviewOpParam.getCPtr(param), param);
  }

    public void stop() throws java.lang.Exception {
    pjsua2JNI.VideoPreview_stop(swigCPtr, this);
  }

  public VideoWindow getVideoWindow() {
    return new VideoWindow(pjsua2JNI.VideoPreview_getVideoWindow(swigCPtr, this), true);
  }

    public VideoMedia getVideoMedia() throws java.lang.Exception {
        return new VideoMedia(pjsua2JNI.VideoPreview_getVideoMedia(swigCPtr, this), true);
    }

}
