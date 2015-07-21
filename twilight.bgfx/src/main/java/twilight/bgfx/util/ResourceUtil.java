package twilight.bgfx.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import twilight.bgfx.BGFX;
import twilight.bgfx.BGFXException;

/**
 * <p>
 * ResourceUtil provides various utility functions for using BGFX to render
 * graphics.
 * </p>
 * 
 * @author tmccrary
 *
 */
public class ResourceUtil {

    /**
     * 
     * @param bgfx
     * @param path
     * @return
     */
    public static int loadTexture(BGFX bgfx, String path) {
        DataInputStream dataStream = null;
        try {
            File file = new File(path);

            byte[] imgData = new byte[(int) file.length()];
            dataStream = new DataInputStream(new FileInputStream(file));
            dataStream.readFully(imgData);

            ByteBuffer buffer = ByteBuffer.allocateDirect(imgData.length);
            buffer.put(imgData);
            buffer.rewind();

            return bgfx.createTexture(buffer, BGFX.BGFX_TEXTURE_NONE, 0, null);
        } catch (FileNotFoundException e) {
            throw new BGFXException("Error opening input file: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new BGFXException("Error reading data stream: " + e.getMessage(), e);
        } finally {
            try {
                dataStream.close();
            } catch (IOException e) {
                throw new BGFXException("Error closing data stream: " + e.getMessage(), e);
            }
        }
    }

    /**
     * <p>
     * Creates a shader from the data stored in the specified
     * <class>File</class>.
     * </p>
     * 
     * @param bgfx
     *            the bgfx context on the current Thread
     * @param file
     *            the File to load shader source from
     * @return
     */
    public static int loadShader(BGFX bgfx, File file) {
        try {
            return loadShader(bgfx, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new BGFXException("Error loading shader: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Creates a shader from an <class>InputStream</class>.
     * </p>
     * 
     * @param bgfx
     *            the bgfx context on the current Thread
     * @param stream
     *            the InputStream to read source from when creating the shader
     * @return
     */
    public static int loadShader(BGFX bgfx, InputStream stream) {
        try {
            byte[] shaderBytes = inputStreamToByteArray(stream);

            ByteBuffer buffer = ByteBuffer.allocateDirect(shaderBytes.length + 1).order(ByteOrder.nativeOrder());
            buffer.put(shaderBytes);
            buffer.put(buffer.capacity() - 1, (byte) 0x0A);
            buffer.rewind();

            return bgfx.createShader(buffer);
        } catch (Exception e) {
            throw new BGFXException("Error loading shader: " + e.getMessage(), e);
        }
    }

    /***
     * 
     * @param stream
     * @return
     */
    public static byte[] inputStreamToByteArray(InputStream stream) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
        } catch (IOException e) {
            throw new BGFXException("Error reading InputStream: " + e.getMessage(), e);
        } finally {
            try {
                buffer.close();
            } catch (IOException e) {
                throw new BGFXException("Error closing OutputStream: " + e.getMessage(), e);
            }
        }

        return buffer.toByteArray();
    }

    /**
     * 
     * @param clazz
     * @param path
     * @return
     */
    public static InputStream findStream(Class<?> clazz, String path) {
        InputStream resourceAsStream = clazz.getResourceAsStream(path);

        if (resourceAsStream == null) {
            resourceAsStream = clazz.getResourceAsStream("/" + path);
        }

        return resourceAsStream;
    }

    /**
     * 
     * @param clazz
     * @param path
     * @return
     */
    public static InputStream findStream(String path) {
        return findStream(BGFX.class, path);
    }

}
