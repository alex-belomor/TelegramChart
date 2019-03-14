package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GraphComponent extends GLSurfaceView {

    GraphRenderer renderer;

    public GraphComponent(Context context) {
        super(context);
    }

    public GraphComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setZOrderOnTop(true); //necessary
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        renderer = new GraphRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
