package com.ociweb.iot.stopwatch;

import static com.ociweb.iot.grove.GroveTwig.Button;

import com.ociweb.iot.maker.DeviceRuntime;
import com.ociweb.iot.maker.Hardware;
import com.ociweb.iot.maker.IoTSetup;
import com.ociweb.iot.maker.Port;

import static com.ociweb.iot.maker.Port.*;

public class IoTApp implements IoTSetup {

    public static final Port BUTTON_CONNECTION = D3; //long press clear, short press start/top

    public static void main(String[] args) {
        DeviceRuntime.run(new IoTApp());
    }

    @Override
    public void declareConnections(Hardware c) {
        c.connect(Button, BUTTON_CONNECTION);
        c.useI2C();
        c.setTriggerRate(50);
    }

    @Override
    public void declareBehavior(DeviceRuntime runtime) {
        runtime.registerListener(new StopwatchBehavior(runtime));
    }
}
