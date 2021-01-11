

package dev.entao.serial;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";

    private FileDescriptor fd;
    private FileInputStream fis;
    private FileOutputStream fos;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        Log.e("Open Serial", device.getAbsolutePath());

        if (!device.canRead() || !device.canWrite()) {
            try {
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        fd = open(device.getAbsolutePath(), baudrate, flags);
        if (fd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        fis = new FileInputStream(fd);
        fos = new FileOutputStream(fd);
    }


    public InputStream getInputStream() {
        return fis;
    }

    public OutputStream getOutputStream() {
        return fos;
    }

    public void closeSerial() {
        try {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
            fos = null;
            fis = null;
            if (fd != null) {
                close(fd);
            }
            fd = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private native static FileDescriptor open(String path, int baudrate, int flags);

    private native static void close(FileDescriptor fileDesc);

    static {
        System.loadLibrary("serial_port");
    }
}
