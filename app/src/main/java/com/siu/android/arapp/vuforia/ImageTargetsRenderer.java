/*==============================================================================
Copyright (c) 2010-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.

@file
    ImageTargetsRenderer.java

@brief
    Sample for ImageTargets

==============================================================================*/


package com.siu.android.arapp.vuforia;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.qualcomm.QCAR.QCAR;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/** The renderer class for the ImageTargets sample. */
public class ImageTargetsRenderer implements GLSurfaceView.Renderer
{
    public boolean mIsActive = false;

    /** Reference to main activity **/
    public ImageTargets mActivity;

    /** Native function for initializing the renderer. */
    public native void initRendering();

    /** Native function to update the renderer. */
    public native void updateRendering(int width, int height);


    /** Called when the surface is created or recreated. */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        DebugLog.LOGD("GLRenderer::onSurfaceCreated");

        // Call native function to initialize rendering:
        initRendering();

        // Call QCAR function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        QCAR.onSurfaceCreated();
    }


    /** Called when the surface changed size. */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        DebugLog.LOGD("GLRenderer::onSurfaceChanged");

        // Call native function to update rendering when render surface
        // parameters have changed:
        updateRendering(width, height);

        // Call QCAR function to handle render surface size changes:
        QCAR.onSurfaceChanged(width, height);
    }


    /** The native render function. */
    public native void renderFrame();


    /** Called to draw the current frame. */
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;

        // Update render view (projection matrix and viewport) if needed:
        mActivity.updateRenderView();

        // Call our native function to render content
        renderFrame();
    }

    public void objectDetected(String name, float distance) {
        Log.d(getClass().getName(), "DISTANCE " + distance);
        Log.d(getClass().getName(), "NAME " + name);
    }
}
