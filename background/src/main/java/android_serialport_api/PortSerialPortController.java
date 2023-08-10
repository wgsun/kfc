package android_serialport_api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.serialport.SerialPort;
import android.text.TextUtils;
import android.util.Log;

import com.common.base.utils.XssUtility;
import com.common.logger.MuhuaLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import static android_serialport_api.SerialBeanInfo.SERIAL_PORT_CONFIG_ERROR;
import static android_serialport_api.SerialBeanInfo.SERIAL_PORT_RECEIVE_DATA;
import static android_serialport_api.SerialBeanInfo.SERIAL_PORT_SECURITY_ERROR;
import static android_serialport_api.SerialBeanInfo.SERIAL_PORT_WRITE_DATA_BYTES;
import static android_serialport_api.SerialBeanInfo.WRITE_DATA_TIME;


public class PortSerialPortController {
    public final String TAG = "PortSerial";

    private PortSerialPortController m_Instance = null;


    private volatile int m_iTimeCount = 0;
    private SerialPort m_SerialPort = null;
    private SerialPortFinder m_SerialPortFinder = null;
    private InputStream m_InputStream = null;
    private OutputStream m_OutputStream = null;
    private ReadThread m_ReadThread = null;

    private Context mContext = null;
    private String portData;


    public PortSerialPortController() {
        m_SerialPortFinder = new SerialPortFinder();
    }

    public void init(Context context) {
        mContext = context;
    }

    public void clean() {
        portData = "";

    }

    private long lastClickTime;

    public long getTimeBetween() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD;
    }

    private long lastClickTimeNew;

    public long getTimeBetweenNew() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTimeNew;
        lastClickTimeNew = time;
        return timeD;
    }


    /**
     * 打开并监视串口
     */

    /**
     * 打开并监视串口
     */
    public void openSerialPort(String dev, int baudrate) {
        openSerialPort(dev, baudrate, 0, 8, 1);
    }

    public void openSerialPort(String dev, int baudrate, int parity, int dataBits, int stopBits) {

        try {
            logx(TAG, "openSerialPort:  " + dev + "  " + baudrate);
            m_SerialPort = SerialPort //
                    .newBuilder(dev, baudrate) // 串口地址地址，波特率
                    .parity(parity) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
                    .dataBits(dataBits) // 数据位,默认8；可选值为5~8
                    .stopBits(stopBits) // 停止位，默认1；1:1位停止位；2:2位停止位

                    .build();
            //m_SerialPort = getSerialPort("/dev/ttyS1", 19200);//（写死串口）
            m_OutputStream = m_SerialPort.getOutputStream();
            m_InputStream = m_SerialPort.getInputStream();

            /* Create a receiving thread */
            if (null != m_ReadThread) {
                m_ReadThread.interrupt();
                m_ReadThread = null;
            }
            m_ReadThread = new ReadThread();
            m_ReadThread.setName("ReadThread");
            m_ReadThread.setRun(true);
            m_ReadThread.setPriority(Thread.NORM_PRIORITY + 3);
            m_ReadThread.start();
        } catch (SecurityException e) {
            logx(TAG, "openSerialPort  打开串口写数据错误  e：" + e.toString() + " str: " + e.getMessage());

            if (m_ReceiveHandler != null) {
                m_ReceiveHandler.sendEmptyMessage(SERIAL_PORT_SECURITY_ERROR);
            }
        } catch (IOException e) {
            if (m_ReceiveHandler != null) {
                m_ReceiveHandler.sendEmptyMessage(SerialBeanInfo.SERIAL_PORT_UNKNOWN_ERROR);
            }
        } catch (InvalidParameterException e) {
            if (m_ReceiveHandler != null) {
                m_ReceiveHandler.sendEmptyMessage(SERIAL_PORT_CONFIG_ERROR);
            }
        }
    }


    //    /**
//     * 关闭串口
//     */
    public void closeSerialPort() {
        if (m_OutputStream != null) {
            try {
                m_OutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            m_OutputStream = null;
        }

        if (m_InputStream != null) {
            try {
                m_InputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            m_InputStream = null;
        }

        if (m_SerialPort != null) {
            m_SerialPort.tryClose();
            m_SerialPort = null;
        }
    }
//
//    /**
//     * 关闭串口
//     */
//    public void closeSerialPortNew() {
//        if (m_OutputStreamNew != null) {
//            try {
//                m_OutputStreamNew.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            m_OutputStreamNew = null;
//        }
//
//        if (m_InputStreamNew != null) {
//            try {
//                m_InputStreamNew.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            m_InputStreamNew = null;
//        }
//        if (m_SerialPortNew != null) {
//            m_SerialPortNew.close();
//            m_SerialPortNew.closeStream();
//            m_SerialPortNew = null;
//        }
//    }

    public void writeData(String str) {
        if (null == str) {
            logx(TAG, "writeData str is null");
            return;
        }
        try {
            if (null != m_OutputStream) {
                if ((getTimeBetween() >= WRITE_DATA_TIME) || (m_iTimeCount > 1)) {
                    m_iTimeCount = 0;
                    m_OutputStream.write(str.getBytes());
                    m_OutputStream.flush();
                    logx(TAG, "writeData 向串口写数据  str: " + str);
                } else {
                    if (m_ReceiveHandler != null) {
                        m_iTimeCount++;
                        Message message = m_ReceiveHandler.obtainMessage();
                        message.what = SerialBeanInfo.SERIAL_PORT_WRITE_DATA_STR;
                        message.obj = str;
                        m_ReceiveHandler.sendMessageDelayed(message, WRITE_DATA_TIME * m_iTimeCount);
                    }
                }

            }
        } catch (IOException e) {
            logx(TAG, "writeData  向串口写数据错误  e：" + e.toString() + " str: " + e.getMessage());

        }
    }

    public void writeDataImmediately(String data) {
        if ((null == m_OutputStream) || (null == data)) {
            return;
        }
        try {
            m_OutputStream.write(data.getBytes());
            m_OutputStream.flush();
        } catch (IOException e) {
            logx(TAG, "writeDataImmediately  向串口写数据错误  e：" + e.toString() + " str: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void writeDataImmediately(byte[] bytes) {
        if ((null == m_OutputStream) || (null == bytes)) {
            return;
        }
//       logx(TAG, "writeDataImmediately bytes：" + TcnUtility.bytesToHexString(bytes));
        try {
            m_OutputStream.write(bytes);
            m_OutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logx(TAG, "writeDataImmediately   e：" + e.toString() + " str: " + e.getMessage());
        }
    }

    public void writeData(byte[] bytes) {
        if (null == bytes) {
            logx(TAG, "writeData bytes is null");
            return;
        }
        try {
            if (null != m_OutputStream) {
                if ((getTimeBetween() >= WRITE_DATA_TIME) || (m_iTimeCount > 1)) {
                    m_iTimeCount = 0;
                    m_OutputStream.write(bytes);
                    m_OutputStream.flush();
                } else {
                    if (m_ReceiveHandler != null) {
                        m_iTimeCount++;
                        Message message = m_ReceiveHandler.obtainMessage();
                        message.what = SERIAL_PORT_WRITE_DATA_BYTES;
                        message.obj = bytes;
                        m_ReceiveHandler.sendMessageDelayed(message, WRITE_DATA_TIME * m_iTimeCount);
                    }
                }

            }
        } catch (IOException e) {
            logx(TAG, "writeData 向串口写数据错误 e：" + e.toString() + " str: " + e.getMessage());

        }
    }

    private Handler m_ReceiveHandler = null;

    public void setHandler(Handler receiveHandler) {
        m_ReceiveHandler = receiveHandler;
    }


    /**
     * 监视串口得到串口发送的数据
     *
     * @author Administrator
     */
    private class ReadThread extends Thread {

        private boolean bIsRun;

        public boolean getRun() {
            return bIsRun;
        }

        public void setRun(boolean bRun) {
            this.bIsRun = bRun;
        }

        @Override
        public void run() {
            super.run();

            if (null == m_InputStream) {
                logx(TAG, "readthread m_InputStream is null");
                return;
            }
            byte[] buffer = null;
            // String read = "";
            int byteCount = 0;
            while (bIsRun) {

                try {
                    buffer = new byte[512];
                    byteCount = m_InputStream.read(buffer);
                    /*if (byteCount > 0) {
                       // read = new String(buffer, 0, byteCount);
                        m_bIsSendData = true;
                    } else {
               //         read = "";
                    }*/

                    //Log.d(TAG, "read: " + TcnUtility.bytesToHexString(buffer,byteCount));
                    if (byteCount > 0) {
                        if (m_ReceiveHandler != null) {
                            if (TextUtils.isEmpty(portData) || portData.equalsIgnoreCase("null")) {
                                portData = OnAnalyseProtocolData(byteCount, buffer);

                            } else {
                                portData += OnAnalyseProtocolData(byteCount, buffer);

                            }
                            Message message = m_ReceiveHandler.obtainMessage();
                            message.what = SERIAL_PORT_RECEIVE_DATA;
                            message.obj = portData;
                            m_ReceiveHandler.removeMessages(SERIAL_PORT_RECEIVE_DATA);
                            m_ReceiveHandler.sendMessageDelayed(message, 150);
                        } else {
                            logx(TAG, "m_ReceiveHandler is null");
                        }
                    }

                } catch (IOException e) {
                    logx(TAG, "ReadThread IOException e: " + e);
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public void logx(String fun, String msg) {
        MuhuaLog.getInstance().LoggerDebug("", TAG, "openSerialPort", msg);

    }

    /*收到数据*/
    private String OnAnalyseProtocolData(int bytesCount, byte[] bytesData) {
        if ((null == bytesData) || (bytesData.length < 1)) {
            return "";
        }
        return XssUtility.bytesToHexString(bytesData, bytesCount);
    }
}
