package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Google官方代码
 * 此类的作用为，JNI的调用，用来加载.so文件的
 * 获取串口输入输出流
 */

public class SerialPort {

    private static final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used                                                                                                                                                                            by native method
     * close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int flags)
            throws SecurityException, IOException {

        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        System.out.println(device.getAbsolutePath()
                + "==============================");
//开启串口，传入物理地址、波特率、flags值
        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }
    //获取串口的输入流
    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }
    //获取串口的输出流
    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }
//    path：为串口的物理地址，一般硬件工程师都会告诉你的例如ttyS0、ttyS1等，或者通过SerialPortFinder类去寻找得到可用的串口地址。
//    baudrate：波特率，与外接设备一致
//    flags：设置为0，原因较复杂，见文章最底下。
    // JNI调用，开启串口
    // JNI
    private native static FileDescriptor open(String path, int baudrate,
                                              int flags);
    //关闭串口
    public native void close();

    static {
        System.out.println("==============================");
        System.loadLibrary("serial_port");
        System.out.println("********************************");
    }
}
