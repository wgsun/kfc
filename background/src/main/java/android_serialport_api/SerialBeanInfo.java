package android_serialport_api;

public interface SerialBeanInfo {
    int SERIAL_PORT_RECEIVE_DATA = 50;
    int SERIAL_PORT_CONFIG_ERROR = 51;
    int SERIAL_PORT_SECURITY_ERROR = 52;
    int SERIAL_PORT_UNKNOWN_ERROR = 53;

    int SERIAL_PORT_WRITE_DATA_STR = 57;
    int SERIAL_PORT_WRITE_DATA_BYTES = 59;
    int WRITE_DATA_TIME = 300;
}
