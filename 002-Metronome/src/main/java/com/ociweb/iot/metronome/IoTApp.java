package com.ociweb.iot.metronome;


import static com.ociweb.iot.grove.GroveTwig.Buzzer;

import static com.ociweb.iot.grove.GroveTwig.Button;
import static com.ociweb.iot.grove.GroveTwig.AngleSensor;

import com.ociweb.iot.hardware.Hardware;
import com.ociweb.iot.maker.DeviceRuntime;
import com.ociweb.iot.maker.IoTSetup;

public class IoTApp implements IoTSetup {
    
    public static final int ROTARY_ANGLE_CONNECTION = 1;
    public static final int BUZZER_CONNECTION = 2;    
    
    public static void main( String[] args ) {
        DeviceRuntime.run(new IoTApp());
    }
    
    
    @Override
    public void declareConnections(Hardware c) {
        c.useConnectD(Buzzer, BUZZER_CONNECTION); //could use relay or LED instead of buzzer if desired
        c.useConnectA(AngleSensor, ROTARY_ANGLE_CONNECTION);
        c.useI2C();
        c.useTriggerRate(40);
    }


    @Override
    public void declareBehavior(DeviceRuntime runtime) {
        runtime.registerListener(new MetronomeBehavior(runtime));
    }        
  
}