package com.exogee.meetingroomtablet;

import android.util.Log;

import java.io.DataOutputStream;

class LEDCommand {
    // Commands
    public static final int BRIGHTNESS_UP = 0;
    public static final int BRIGHTNESS_DOWN = 1;
    public static final int OFF = 2;
    public static final int ON = 3;

    // Modes
    public static final int MODE_FLASH = 11;
    public static final int MODE_STROBE = 15;
    public static final int MODE_FADE = 19;
    public static final int MODE_SMOOTH = 23;

    // Colors
    public static final int SHOW_RED = 4;
    public static final int SHOW_GREEN = 5;
    public static final int SHOW_BLUE = 6;
    public static final int SHOW_WHITE = 7;
    public static final int SHOW_ORANGE_RED = 8;
    public static final int SHOW_CYAN_GREEN = 9;
    public static final int SHOW_BLUE_PURPLE = 10;
    public static final int SHOW_ORANGE = 12;
    public static final int SHOW_CYAN = 13;
    public static final int SHOW_PURPLE = 14;
    public static final int SHOW_YELLOW_ORANGE = 16;
    public static final int SHOW_TEAL_CYAN = 17;
    public static final int SHOW_PINK_PURPLE = 18;
    public static final int SHOW_YELLOW = 20;
    public static final int SHOW_TEAL = 21;
    public static final int SHOW_PINK = 22;

    public static final void execute(int command) {
        String commandString = String.format("echo w 0x%02X > /sys/devices/platform/led_con_h/zigbee_reset\nexit\n", command);

        try {
            Process process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(commandString);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("ledCommand", "Error in ledCommand", e);
        }
    }
}

