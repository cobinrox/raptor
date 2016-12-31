package com.cobinrox.io;

public class EgorConstants {

        // mover control GPIO pins
        public static int FWD_GPIO_PIN   = 7;  // physical pin 4
        public static int BACK_GPIO_PIN  = 0;  // 6
        public static int LEFT_GPIO_PIN  = 2;  // 7
        public static int RIGHT_GPIO_PIN = 3;  // 8

	// default UDP large-data chunk buffer size
	public static final int BUFFER_SIZE = 1024*32;
	public static final int FILE_PORT = 9666;
	public static final int CMD_PORT = 8666;
	
	

	/* deefault time between image re-reads by client */
	public static final int CLIENT_REREAD_TIME = 2000;

	/* how long to let a GPIO pin remain running */
	public static final int CMD_RUN_TIME = 2000;

	/* length of udp datagram pkt used for sending various short cmds */
	public static final int CMD_LENGTH = 100;

	public static final String IMG_FILE_NAME = "/tmp/mjpg/test.jpg";
	//public static final String BUF_FILE_NAME = "/tmp/mjpg/buf.jpg";
	//public static final String ERR_FILE_NAME = "/tmp/mjpg/err.jpg";

//	public static final String IMG_FILE_NAME = "C:/aaa/tle/test.jpg";
	//public static final String BUF_FILE_NAME = "C:/aaawk/Egor/buf.jpg";
	//public static final String ERR_FILE_NAME = "C:/aaawk/Egor/err.jpg";

	/* save image file to hard drive for help in debugging problems */
	public static final boolean SAVE_SANITY_COPY = false;

	/* time to wait between camera snapshots */
	public static final int CAMERA_REFRESH_TIME = 5000;

	public static final int CMD_IDX       = 0;
        public static final int LEN_IDX       = 1;
	public static final int CLI_CNT_IDX   = 2;
	public static final int EGOR_CNT_IDX   = 3;
	public static final int LAST_SIZE_IDX = 4;
	public static final int BUF_SIZE_IDX  = 5;
	public static final int TIMEOUT_IDX   = 6;
	public static final int DSCP_IDX      = 7;
	public static final int PROTO_IDX     = 8;


}
