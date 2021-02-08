package com.example.coolerthanyou.bluetooth

/**
 * Exception class for when Bluetooth is not supported by the hardware.
 *
 * @property msg    The message describing the event
 */
class BluetoothUnsupportedException(msg: String) : Exception(msg)