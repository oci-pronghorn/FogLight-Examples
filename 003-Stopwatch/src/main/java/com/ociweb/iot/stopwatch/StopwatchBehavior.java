package com.ociweb.iot.stopwatch;

import com.ociweb.iot.grove.Grove_LCD_RGB;
import com.ociweb.iot.maker.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class StopwatchBehavior implements DigitalListener, TimeListener {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private boolean running;
    private boolean startOnUp;
    private long startTime;
    private long stopTime;

    private final ZoneId zone = ZoneOffset.UTC;

    private final CommandChannel lcdTextChannel;

    public StopwatchBehavior(DeviceRuntime runtime) {
        lcdTextChannel = runtime.newCommandChannel();
    }

    @Override
    public void digitalEvent(Port connection, long time, long durationMillis, int value) {
        if (0 == value) {
            //how long was it pressed before this change to up?
            if (durationMillis > 1000) {
                startTime = 0;
                stopTime = 0;
                running = false;
            } else {
                running = startOnUp;
                startOnUp = false;
            }
        } else {
            //user button down
            //toggle clock start or stop
            if (!running) {
                if (0 == startTime) {
                    startTime = System.currentTimeMillis();
                } else {
                    startTime = System.currentTimeMillis() - (stopTime - startTime);
                    stopTime = 0;
                }
                startOnUp = true;
            } else {
                stopTime = System.currentTimeMillis();
                running = false;
            }

        }
    }

    @Override
    public void timeEvent(long time) {
        long duration = 0 == startTime ? 0 : stopTime == 0 ? time - startTime : stopTime - startTime;

        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(duration), zone);

        String text = date.format(formatter);

        Grove_LCD_RGB.commandForText(lcdTextChannel, text);
    }
}
