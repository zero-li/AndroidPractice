/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class MediaEventData {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected MediaEventData(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MediaEventData obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_MediaEventData(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setFmtChanged(MediaFmtChangedEvent value) {
    pjsua2JNI.MediaEventData_fmtChanged_set(swigCPtr, this, MediaFmtChangedEvent.getCPtr(value), value);
  }

  public MediaFmtChangedEvent getFmtChanged() {
    long cPtr = pjsua2JNI.MediaEventData_fmtChanged_get(swigCPtr, this);
    return (cPtr == 0) ? null : new MediaFmtChangedEvent(cPtr, false);
  }

    public void setAudDevError(AudDevErrorEvent value) {
        pjsua2JNI.MediaEventData_audDevError_set(swigCPtr, this, AudDevErrorEvent.getCPtr(value), value);
    }

    public AudDevErrorEvent getAudDevError() {
        long cPtr = pjsua2JNI.MediaEventData_audDevError_get(swigCPtr, this);
        return (cPtr == 0) ? null : new AudDevErrorEvent(cPtr, false);
    }

  public void setPtr(SWIGTYPE_p_void value) {
    pjsua2JNI.MediaEventData_ptr_set(swigCPtr, this, SWIGTYPE_p_void.getCPtr(value));
  }

  public SWIGTYPE_p_void getPtr() {
    long cPtr = pjsua2JNI.MediaEventData_ptr_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_void(cPtr, false);
  }

  public MediaEventData() {
    this(pjsua2JNI.new_MediaEventData(), true);
  }

}
