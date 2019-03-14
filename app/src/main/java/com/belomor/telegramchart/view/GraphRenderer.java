package com.belomor.telegramchart.view;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.ArrayMap;

import com.belomor.telegramchart.data.TestChartData;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GraphRenderer implements GLSurfaceView.Renderer {

    GraphLine graphLine;

    ArrayMap<Integer, TestChartData> chartArray;

    int width, height;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        chartArray = new ArrayMap<>();

        Random random = new Random();

        long currentTime = System.currentTimeMillis();

        for (int i=0; i < 50; i++) {
            TestChartData data = new TestChartData();
            data.setDate(currentTime + 86400000 * i);
            data.setFollowers(random.nextInt(350));
            chartArray.put(i, data);
        }

        graphLine = new GraphLine(chartArray);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;

        if(height == 0) {                       //Prevent A Divide By Zero By
            height = 1;                         //Making Height Equal One
        }
        gl.glViewport(0, 0, width, height);     //Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);    //Select The Projection Matrix
        gl.glLoadIdentity();                    //Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        //Log.d("Chart Ratio2 "," width " +width + " H " + height);
        GLU.gluPerspective(gl, 45.0f, (float) height * 2.0f/(float)width, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);     //Select The Modelview Matrix
        gl.glLoadIdentity();                    //Reset The Modelview Matrix
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Reset the Modelview Matrix
        gl.glLoadIdentity();
        // Drawing
        //Log.d("Chart Ratio1 "," width " +width + " H " + height);
        gl.glTranslatef(-50.0f, -100.0f, -600f);     // move 5 units INTO the screen
        // is the same as moving the camera 5 units away
        graphLine.setResolution(width, height);
//        graphLine.setChartData(chartData);
        graphLine.draw(gl);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}


