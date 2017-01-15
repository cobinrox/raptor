/*
 * Copyright (C) 2014 Klaus Reimer <k@ailis.de>
 * See LICENSE.txt for licensing information.
 */

package com.cobinrox.io.impl.usb;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

/**
 * Dumps the descriptors of all available devices.
 * 
 * @author Klaus Reimer <k@ailis.de>
 */
public class DumpDevices
{
    /**
     * Dumps all configuration descriptors of the specified device. Because
     * libusb descriptors are connected to each other (Configuration descriptor
     * references interface descriptors which reference endpoint descriptors)
     * dumping a configuration descriptor also dumps all interface and endpoint
     * descriptors in this configuration.
     * 
     * @param device
     *            The USB device.
     * @param numConfigurations
     *            The number of configurations to dump (Read from the device
     *            descriptor)
     */
    public static void dumpConfigurationDescriptors(final Device device,
        final int numConfigurations)
    {
        for (byte i = 0; i < numConfigurations; i += 1)
        {
            final ConfigDescriptor descriptor = new ConfigDescriptor();
            final int result = LibUsb.getConfigDescriptor(device, i, descriptor);
            if (result < 0)
            {
                throw new LibUsbException("Unable to read config descriptor",
                    result);
            }
            try
            {
                System.out.println(descriptor.dump().replaceAll("(?m)^",
                    "  "));
            }
            finally
            {
                // Ensure that the config descriptor is freed
                LibUsb.freeConfigDescriptor(descriptor);
            }
        }
    }

    /**
     * Dumps the specified device to stdout.
     * 
     * @param device
     *            The device to dump.
     */
    public static void dumpDevice(final Device device)
    {
        // Dump device address and bus number
        final int address = LibUsb.getDeviceAddress(device);
        final int busNumber = LibUsb.getBusNumber(device);
        System.out.println(String
            .format("Device %03d/%03d", busNumber, address));

        // Dump port number if available
        final int portNumber = LibUsb.getPortNumber(device);
        if (portNumber != 0)
            System.out.println("Connected to port: " + portNumber);

        // Dump parent device if available
        final Device parent = LibUsb.getParent(device);
        if (parent != null)
        {
            final int parentAddress = LibUsb.getDeviceAddress(parent);
            final int parentBusNumber = LibUsb.getBusNumber(parent);
            System.out.println(String.format("Parent: %03d/%03d",
                parentBusNumber, parentAddress));
        }

        // Dump the device speed
        System.out.println("Speed: "
            + DescriptorUtils.getSpeedName(LibUsb.getDeviceSpeed(device)));

        // Read the device descriptor
        final DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result < 0)
        {
            throw new LibUsbException("Unable to read device descriptor",
                result);
        }

        // Try to open the device. This may fail because user has no
        // permission to communicate with the device. This is not
        // important for the dumps, we are just not able to resolve string
        // descriptor numbers to strings in the descriptor dumps.
        DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(device, handle);
        if (result < 0)
        {
            System.out.println(String.format("Unable to open device: %s. "
                + "Continuing without device handle.",
                LibUsb.strError(result)));
            handle = null;
        }

        // Dump the device descriptor
        System.out.print(descriptor.dump(handle));

        // Dump all configuration descriptors
        dumpConfigurationDescriptors(device, descriptor.bNumConfigurations());

        // Close the device if it was opened
        if (handle != null)
        {
            LibUsb.close(handle);
        }
    }

    /**
     * Main method.
     * 
     * @param args
     *            Command-line arguments (Ignored)
     */
    public static void main(final String[] args)
    {
        // Create the libusb context
        final Context context = new Context();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Read the USB device list
        final DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        if (result < 0)
        {
            throw new LibUsbException("Unable to get device list", result);
        }

        try
        {
            // Iterate over all devices and dump them
            for (Device device: list)
            {
                dumpDevice(device);
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }
        
        // Deinitialize the libusb context
        LibUsb.exit(context);
    }


    // Read data from device after claiming it
    public static String read(DeviceHandle handle, int size, long timeout, int readType) {
        byte endpointId = (byte) 0x81;
        StringBuilder stringBuilder = new StringBuilder();
        boolean started = false;
        while(true)
        {
            ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(ByteOrder.LITTLE_ENDIAN);
            IntBuffer transferred = BufferUtils.allocateIntBuffer();
            int result;
            switch(readType)
            {
                case 0:
                    result = LibUsb.interruptTransfer(handle, endpointId, buffer, transferred, timeout);
                    break;
                case 1:
                    result = LibUsb.bulkTransfer(handle, endpointId, buffer, transferred, timeout);
                    break;
                default:
                    result = LibUsb.interruptTransfer(handle, endpointId, buffer, transferred, timeout);
                    break;
            }
            if (result != LibUsb.SUCCESS)
            {
                return "Failure reading data with"
                        + " buffer size:"
                        + size + " bytes timeout:"
                        + timeout + " ms error code:"
                        + result;
            }else{
                CharBuffer charBuffer = buffer.asCharBuffer();
                while(charBuffer.hasRemaining())
                {
                    char nextChar = charBuffer.get();
                    if(nextChar == '\n') started = true; //Start Character
                    else if(nextChar == '\r' && started) return stringBuilder.toString(); //End Character
                    else if(started) stringBuilder.append(nextChar);
                }
            }
        }
    }
}
