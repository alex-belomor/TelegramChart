package com.belomor.telegramchart.view;

import android.opengl.GLES20;
import android.util.ArrayMap;

import com.belomor.telegramchart.data.TestChartData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class GraphLine {

    private FloatBuffer vertexBuffer;

    private final int mProgram;

    int width, height;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float coords[];

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public GraphLine(ArrayMap<Integer, TestChartData> dataArrayMap) {
        int vertexShader = GraphRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GraphRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

//        coords = new float[dataArrayMap.size() * 2];
//        float x = 0;
//        for (int i = 0; i < dataArrayMap.size(); i++) {
//            if (i == 0) {
//                coords[0] = -1f;
//                coords[1] = -1f;
//            } else {
//                coords[i * 2] = x;
//                coords[i * 2 + 1] = dataArrayMap.get(i).getFollowers() / 100;
//                x = x + 4f;
//            }
//        }

        coords = new float[] {
                -1f, -1f,
                0f, 1f
        };

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(coords.length * 4);

        vertexByteBuffer.order(ByteOrder.nativeOrder());
        // allocates the memory from the byte buffer
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        // fill the vertexBuffer with the vertices
        vertexBuffer.put(coords);
        // set the cursor position to the beginning of the buffer
        vertexBuffer.position(0);
    }

    public void setResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }


    private int positionHandle;
    private int colorHandle;

    public void draw(GL10 gl) {
//        gl.glViewport(0, 0, width, height);
//        // bind the previously generated texture
//        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//        // set the color for the triangle
//        gl.glColor4f(0.1214124f, 0.2523532f, 0f, 1f);
//        // Point to our vertex buffer
//        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
//        // Line width
//        gl.glLineWidth(30.0f);
//        // Draw the vertices as triangle strip
//        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, coords.length/3);
//        //Disable the client state before leaving
//        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);


        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, true,
                0, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        gl.glLineWidth(10f);


        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, coords.length);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
