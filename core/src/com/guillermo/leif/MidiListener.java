package com.guillermo.leif;

import com.guillermo.leif.utility.MidiDevicePrinter;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.List;

public class MidiListener implements MidiDeviceReceiver {
    public static final boolean listDevices = false;
    private static final String OP1_DEVICE_NAME = "OP-1 Midi Device";
    private MidiDevice op1Device;

    private MidiDropsGameScreen applicationAdapter;

    public MidiListener(MidiDropsGameScreen applicationAdapter) {
        this.applicationAdapter = applicationAdapter;
    }

    public void beginListening() throws MidiUnavailableException {
        /*
        Example:
        [Gervill, Real Time Sequencer, Microsoft MIDI Mapper, Microsoft GS Wavetable Synth, OP-1 Midi Device, OP-1 Midi Device]
        */
        if (listDevices) {
            MidiDevicePrinter.listMidiDevices();
        }
        setUpMidi();
        MidiSystem.getTransmitter();
    }

    public void setUpMidi() throws MidiUnavailableException {
        MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info : midiDeviceInfos) {
            if (info.getName().equals(OP1_DEVICE_NAME)) {
                op1Device = MidiSystem.getMidiDevice(info);
                List<Transmitter> transmitters =
                        getOp1DeviceFromDeviceInfo(op1Device);
                if (null != transmitters && !transmitters.isEmpty()) {
                    transmitters.get(0).setReceiver(this);
                    System.out.println("OP-1 Receiver is now set set");
                    return;
                }
            }
        }

        throw new MidiUnavailableException("Couldn't find the OP-1 " +
                "transmitter.");
    }

    private List<Transmitter> getOp1DeviceFromDeviceInfo(MidiDevice device) {
        try {
            System.out.println("        Default transmitter: " +
                    device.getTransmitter().toString());

            System.out.println("        Open transmitters now:");
            List<Transmitter> transmitters = device.getTransmitters();
            for (Transmitter t : transmitters) {
                System.out.println("        !!!!!!!     " + t.toString());
            }
            return transmitters;
        } catch (MidiUnavailableException e) {
            System.out.println("        No default transmitter");
        }
        return null;
    }

    @Override
    public MidiDevice getMidiDevice() {
        return op1Device;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        byte[] msg = message.getMessage();

        System.out.println(Arrays.toString(msg));

        applicationAdapter.midiReceived(msg, timeStamp);

    }

    @Override
    public void close() {
        this.op1Device.close();
    }

    public String getName() {
        return op1Device.getDeviceInfo().getName();
    }

}
