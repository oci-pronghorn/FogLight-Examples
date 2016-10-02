package com.ociweb.iot.gasPumpSimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ociweb.iot.maker.CommandChannel;
import com.ociweb.iot.maker.DeviceRuntime;
import com.ociweb.iot.maker.DigitalListener;
import com.ociweb.iot.maker.PayloadReader;
import com.ociweb.iot.maker.PayloadWriter;
import com.ociweb.iot.maker.Port;
import com.ociweb.iot.maker.PubSubListener;
import com.ociweb.iot.maker.StateChangeListener;

public class PumpSimulator implements DigitalListener, StateChangeListener<PumpState> {

	private Logger logger = LoggerFactory.getLogger(PumpSimulator.class);
	
	private final CommandChannel channel;	
	private final String fuelName;
	private final int centsPerGallon;
	
	private final String pumpTopic;
	private final String totalTopic;
	private boolean isActive;
			
	private int units;
	private int totalUnits;
	
	public PumpSimulator(DeviceRuntime runtime, String pumpTopic, String totalTopic, String fuelName, int centsPerGallon) {

   	  this.channel = runtime.newCommandChannel();
      this.pumpTopic = pumpTopic;   	  
      this.totalTopic = totalTopic;
   	  this.fuelName = fuelName;
   	  this.centsPerGallon = centsPerGallon;
   	  
	}

	long last = 0;
	
	@Override
	public void digitalEvent(Port port, long time, long durationMillis, int value) {
	
		
		if (isActive) {
	
			long gap = time-last;
			System.out.println("gap "+gap+" "+value);
			last = time;
			
			//pump 1/100 gallon or nothing
			units += value;				
			
			PayloadWriter payload = channel.openTopic(pumpTopic);
						
			payload.writeLong(time);
			payload.writeUTF(fuelName);
			payload.writeInt(centsPerGallon);
			payload.writeInt(units);
						
			payload.publish();
		
		}
	
	}
	

	@Override
	public void stateChange(PumpState oldState, PumpState newState) {	

		//exit pump mode
		if (oldState == PumpState.Pump) {
			isActive = false;
			
			if (units>0) {
				totalUnits += units;
				units = 0;
			
				PayloadWriter payload = channel.openTopic(totalTopic);
				
				payload.writeUTF(fuelName);
				payload.writeInt(centsPerGallon);
				payload.writeInt(totalUnits);
				
				payload.publish();
			}
		}
		
		//enter pump mode
		if (newState == PumpState.Pump) {
			isActive = true;
			
		}
		
	}



}
