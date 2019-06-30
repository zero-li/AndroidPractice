/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class VideoMedia extends Media {
    private transient long swigCPtr;

    protected VideoMedia(long cPtr, boolean cMemoryOwn) {
        super(pjsua2JNI.VideoMedia_SWIGUpcast(cPtr), cMemoryOwn);
        swigCPtr = cPtr;
    }

    protected static long getCPtr(VideoMedia obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                pjsua2JNI.delete_VideoMedia(swigCPtr);
            }
            swigCPtr = 0;
        }
        super.delete();
    }

    public VidConfPortInfo getPortInfo() throws java.lang.Exception {
        return new VidConfPortInfo(pjsua2JNI.VideoMedia_getPortInfo(swigCPtr, this), true);
    }

    public int getPortId() {
        return pjsua2JNI.VideoMedia_getPortId(swigCPtr, this);
    }

    public static VidConfPortInfo getPortInfoFromId(int port_id) throws java.lang.Exception {
        return new VidConfPortInfo(pjsua2JNI.VideoMedia_getPortInfoFromId(port_id), true);
    }

    public void startTransmit(VideoMedia sink, VideoMediaTransmitParam param) throws java.lang.Exception {
        pjsua2JNI.VideoMedia_startTransmit(swigCPtr, this, VideoMedia.getCPtr(sink), sink, VideoMediaTransmitParam.getCPtr(param), param);
    }

    public void stopTransmit(VideoMedia sink) throws java.lang.Exception {
        pjsua2JNI.VideoMedia_stopTransmit(swigCPtr, this, VideoMedia.getCPtr(sink), sink);
    }

    public VideoMedia() {
        this(pjsua2JNI.new_VideoMedia(), true);
    }

}
