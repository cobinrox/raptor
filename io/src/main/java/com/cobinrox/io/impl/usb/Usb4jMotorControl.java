package com.cobinrox.io.impl.usb;

import com.cobinrox.io.impl.IMotor;
import com.cobinrox.io.impl.MotorProps;
import org.apache.log4j.Logger;
import org.usb4java.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by name on 12/22/2016.
 */
public class Usb4jMotorControl implements IMotor {

    static final Logger logger = Logger.getLogger(Usb4jMotorControl.class);
    DecimalFormat df = new DecimalFormat("###.#");
    String alias;
    static public boolean ebrake;
    String g_debugStatusString;
    Thread g_debugStatusThread;
    public int numFinishedCmds;
    int numCmdsToExpect;

    MotorProps mp;
    public Usb4jMotorControl(String alias) {
        this.alias = alias;
    }

    @Override
    public void init(MotorProps mp) throws Throwable {
        this.mp = mp;
        hwInit();
    }

    @Override
    public void setExpectedNumCmds(int numCmds) {
        numCmdsToExpect = numCmds;
        numFinishedCmds = 0;
    }

    @Override
    public int getNumFinishedCommands() {
        return numFinishedCmds;
    }


    /**
     * Creates a new thread on which to send motor commands, then returns.
     * New thread then runs autonomously, until it sends all the sub commands (if running in spurt mode),
     * an error is encountered, or the ebrake flag is set.  If running in continuous mode, the thread
     * continues to run until the ebrake flag is set.
     * @param subCmds
     * @param cmd_run_time_ms
     * @param duty_cycle_hi_ms
     * @param duty_cycle_lo_ms
     * @return
     */
    public String nonBlockingPulse(final List<String> subCmds, final int cmd_run_time_ms, final int duty_cycle_hi_ms, final int duty_cycle_lo_ms) {
        Thread thrd = new Thread() {
            public void run() {
                next_subcmd:
                //for(int subCmdIdx = 0; subCmdIdx < subCmds.size(); subCmdIdx++) {
                for(String subCmd: subCmds) { // = 0; subCmdIdx < subCmds.size(); subCmdIdx++) {
                    if(ebrake)
                    {
                        logger.error("---------------------EBRAKE!-------------------------");
                        brakeAll();
                        numFinishedCmds = numCmdsToExpect;
                        return;
                    }
                    if( subCmd.startsWith("'"))
                    {
                        try {
                            lowLevelSend(subCmd.substring(1,subCmd.length()-1));
                        }catch(Throwable t){logger.error("Sending command",t);}
                        continue next_subcmd;
                    }
                    else {
                        //String[]thisCmd = subCmds.get(subCmdIdx).split(",");
                        String[] thisCmd = subCmd.split(",");
                        String plusOrMinusDirChar = thisCmd[0];

                        float t = Float.parseFloat(thisCmd[1]);
                        int numMsHi = (int) (duty_cycle_hi_ms * t);//utyCycleHi;//dutyCycleHi;//(int)((float)numMsToRunCmdFor * (float)((float)dutyCycleHi/(float)100));
                        int numMsLo = (int) (duty_cycle_lo_ms);
                        long outerStart = System.currentTimeMillis();
                        setName(alias + plusOrMinusDirChar);
                        try {
                            int numPeriods = (int) ((float) cmd_run_time_ms / ((float) numMsHi + (float) numMsLo));
                            //logger.info("     Pulsing [" + plusOrMinusDirChar +"], need [" + numPeriods + "] pulses across ["
                            //        + cmd_run_time_ms + "] ms ...");
                            logger.info("     Pulsing [" + plusOrMinusDirChar + "], pulses across ["
                                    + cmd_run_time_ms + "] ms ...");
                            long elapsedTime = cmd_run_time_ms;
                            //for (int i = 0; i < numPeriods; i++)
                            while (elapsedTime > 0) {
                                long startPulseAt = System.currentTimeMillis();
                                if (ebrake) {
                                    logger.error("---------------------EBRAKE!-------------------------");
                                    brakeAll();
                                    numFinishedCmds = numCmdsToExpect;
                                    return;
                                }
                                logger.info("     turning on for [" + numMsHi + "]ms...");
                                //lowLevelPulse(plusOrMinusDirChar, t);
                                lowLevelPulse(plusOrMinusDirChar, mp.usb_USB_VOLT_STRENGTH);
                                Thread.sleep(numMsHi);

                                if (numMsLo > 0) {
                                    logger.info("     turning off for [" + numMsLo + "]ms...");
                                    lowLevelStop(plusOrMinusDirChar, mp.usb_USB_VOLT_STRENGTH);
                                    Thread.sleep(numMsLo);
                                }
                                long stopPulseAt = System.currentTimeMillis();
                                elapsedTime = elapsedTime - (stopPulseAt - startPulseAt);
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                            System.err.println("Error pulsing");
                        } finally {
                            long outerStop = System.currentTimeMillis();
                            logger.debug("     ...end pulsing; total time sending cmd: "
                                    + (outerStop - outerStart) + " ms");
                        }

                        logger.debug("     (bumping numFinishedCmds from " + numFinishedCmds + ")");
                        numFinishedCmds++;
                    }
                } // end for subcommand
            } // end run
        }; // end thread declaration
        thrd.start();
        return(alias + " nonBlockingPulse started");
    }

    @Override
    public void brakeAll() {
        try {
            lowLevelPulse("+", 0);
        }
        catch(Throwable t)
        {
            logger.error("Unable to break",t);
        }
    }

    @Override
    public void setEbrake(boolean brake) {
        brakeAll();
    }
    String gCmd;
    public void lowLevelEnableDisable(final String d) throws Throwable{
        logger.debug("          low level enable/disable request [" + alias +  " E" + d + "]"  );

    }
    private void lowLevelPulse(final String plusOrMinusDirChar, final int speed) throws Throwable {
        gCmd = alias + ":" +
                (plusOrMinusDirChar.equals("+") ?
                        "" + speed
                        :
                        "-" + speed) +
                "\r\n";
        g_debugStatusString = alias + plusOrMinusDirChar;
        lowLevelSend(gCmd);
    }


    private void lowLevelSend(final String cmd) throws Throwable {
        logger.debug("Sending cmd [" + cmd + "]");
        if (!mp.simulate_pi) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(cmd.length());
            buffer.put(cmd.getBytes());
            buffer.rewind();

            Transfer transfer = LibUsb.allocTransfer();
            byte OUT_ENDPOINT = 2;
            IntBuffer transferred = BufferUtils.allocateIntBuffer();
            int result = LibUsb.bulkTransfer(myHandle, OUT_ENDPOINT, buffer,
                    transferred, 5000);
            if (result != LibUsb.SUCCESS)
            {
                //LibUsb.strError(result);
                throw new LibUsbException("Unable to submit transfer", result);
            }
        }
    }

    private void lowLevelStop(final String plusOrMinusDirChar, final float notUsed) throws Throwable {
        lowLevelPulse(plusOrMinusDirChar, 0);
    }
    /*
        logger.debug("          low level stop request [" +alias + plusOrMinusDirChar + "]"  );
        Process p = null;
        gCmd = null;
        gCmd = alias+":0";

        g_debugStatusString = alias.toLowerCase() + plusOrMinusDirChar;
        //if (g_debugStatusThread == null ){ runDebugStatusThread(); if(g_debugStatusThread != null) g_debugStatusThread.start();}

        String pulseOffCmd = gCmd;
        logger.debug("          Sending cmd [" + pulseOffCmd + "]");
        if (!mp.simulate_pi) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(pulseOffCmd.length());
            buffer.put(pulseOffCmd.getBytes());
            buffer.rewind();
            Transfer transfer = LibUsb.allocTransfer();
            byte OUT_ENDPOINT = 2;
            LibUsb.fillBulkTransfer(transfer, myHandle, OUT_ENDPOINT, buffer,
                    null, null, 1000);
            //System.out.println("Sending " + data.length + " bytes to device");
            int result = LibUsb.submitTransfer(transfer);
            if (result != LibUsb.SUCCESS)
            {
                throw new LibUsbException("Unable to submit transfer", result);
            }
            //Transfer t = new Transfer(buffer);
            //LibUsb.controlTransferGetData()
            //int transfered = LibUsb.controlTransfer(myHandle,
            //        (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
            //        (byte) 0x6d, (byte) 0x31, (byte) 0x3a, buffer, 1000);

        }
    }
    */
    static DeviceHandle myHandle;
    public void hwInit() throws Throwable{
        if(myHandle != null)
        {
            System.out.println("Handle already gotten");
            return;
        }
        Context usbContext = new Context();
        DeviceHandle handle = null;
        // init libusb ctx
        int result = LibUsb.init(usbContext);
        if( result < 0)
        {
            throw new LibUsbException("Unable to initialize libusb",result);
        }

        DeviceList usbDeviceList = new DeviceList();
        result = LibUsb.getDeviceList(usbContext,usbDeviceList);
        if( result < 0)
        {
            throw new LibUsbException("Unable to get device list",result);
        }

        for( Device device:usbDeviceList) {
            handle = dumpDevice(device);
            if(handle != null)
            {
                myHandle = handle;
                break;
            }
        }
        if( myHandle == null)
        {
            throw new IOException("Could not find USB Motor Driver with Product/Vendor ID [" + mp.usb_ID_PRODUCT + "/" + mp.usb_ID_VENDOR);
        }
        else
        {
            //got haanle
            System.out.println("GOT HANDLE");
        }

    }

    /**
     * Dumps the specified device to stdout.
     *
     * @param device
     *            The device to dump.
     */
    private DeviceHandle dumpDevice(final Device device)
    {
        DeviceHandle handle = null;
        try {
            // Dump device address and bus number
            final int address = LibUsb.getDeviceAddress(device);
            final int busNumber = LibUsb.getBusNumber(device);
            System.out.println("***");
            System.out.println(String
                    .format("***Checking Device %03d/%03d ***", busNumber, address));
            System.out.println("***");
            // Dump port number if available
            final int portNumber = LibUsb.getPortNumber(device);
            if (portNumber != 0)
                System.out.println("Connected to port: " + portNumber);

            // Dump parent device if available
            final Device parent = LibUsb.getParent(device);
            if (parent != null) {
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
                System.out.println("(no descriptor for device)");
                return null;
            }
            short idProduct = descriptor.idProduct();
            short idVendor = descriptor.idVendor();
            if (idProduct != (mp.usb_ID_PRODUCT) ||
                    idVendor != (mp.usb_ID_VENDOR)) {
                // not right descriptor yet
                System.out.println("product/vendor: " + idProduct + "/" + idVendor + "(" + mp.usb_ID_PRODUCT + "/" + mp.usb_ID_VENDOR+")");
                return null;
            }
            // Try to open the device. This may fail because user has no
            // permission to communicate with the device.
            handle = new DeviceHandle();
            result = LibUsb.open(device, handle);
            if (result < 0) {
                logger.error(String.format("Unable to open device: %s. ",
                        LibUsb.strError(result)));
                if(LibUsb.strError(result).toLowerCase().contains("permission"))
                {
                    throw new LibUsbException("Unable to open device, you may have to run the executable with elevated permissions",result);
                }
                return null;
            }

            logger.info("**************** FOUND USB MOTOR DRIVER HOO HAH *********************");

            // Dump the device descriptor
            System.out.print(descriptor.dump(handle));
            System.out.println("   ===========================================");

            // Dump all configuration descriptors
            dumpConfigurationDescriptors(device, descriptor.bNumConfigurations());

            // Check if kernel driver is attached to the interface
            int attached = LibUsb.kernelDriverActive(handle, 1);
            //if (attached < 0)
            //{
            //    throw new LibUsbException(
            //            "Unable to check kernel driver active", attached);
            //}

            // Detach kernel driver from interface 0 and 1. This can fail if
            // kernel is not attached to the device or operating system
            // doesn't support this operation. These cases are ignored here.
            result = LibUsb.detachKernelDriver(handle, 0);
            if (result != LibUsb.SUCCESS &&
                    result != LibUsb.ERROR_NOT_SUPPORTED &&
                    result != LibUsb.ERROR_NOT_FOUND)
            {
                throw new LibUsbException("Unable to detach kernel driver",
                        result);
            }
            result = LibUsb.detachKernelDriver(handle, 1);
            if (result != LibUsb.SUCCESS &&
                    result != LibUsb.ERROR_NOT_SUPPORTED &&
                    result != LibUsb.ERROR_NOT_FOUND)
            {
                throw new LibUsbException("Unable to detach kernel driver",
                        result);
            }

            // Claim interface
            result = LibUsb.claimInterface(handle, 1);
            if (result != LibUsb.SUCCESS)
            {
                throw new LibUsbException("Unable to claim interface", result);
            }

            return handle;
        }
        finally
        {
            // Close the device if it was opened
            //if (handle != null) {
            //    LibUsb.close(handle);
            //}
        }
    }
    private void dumpConfigurationDescriptors(final Device device,
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
                //LibUsb.get
                //descriptor.
            }
            finally
            {
                // Ensure that the config descriptor is freed
                LibUsb.freeConfigDescriptor(descriptor);
            }
        }
    }

}
