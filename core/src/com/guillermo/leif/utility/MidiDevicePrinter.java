package com.guillermo.leif.utility;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.List;

public class MidiDevicePrinter {
    public static void listMidiDevices() throws MidiUnavailableException {
        MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        System.out.println(Arrays.toString(midiDeviceInfos));
        for (MidiDevice.Info info : midiDeviceInfos) {
            System.out.println("    Name: " + info.toString() +
                    ", Decription: " +
                    info.getDescription() +
                    ", Vendor: " +
                    info.getVendor());
            MidiDevice device = MidiSystem.getMidiDevice(info);
            if (!device.isOpen()) {
                device.open();
            }
            if (device instanceof Sequencer) {
                System.out.println("        Device is a sequencer");
            }
            if (device instanceof Synthesizer) {
                System.out.println("        Device is a synthesizer");
            }
            System.out.println("        Open receivers:");
            List<Receiver> receivers = device.getReceivers();
            for (Receiver r : receivers) {
                System.out.println("            " + r.toString());
            }
            try {
                System.out.println("\n        Default receiver: " +
                        device.getReceiver().toString());

                System.out.println("\n        Open receivers now:");
                receivers = device.getReceivers();
                for (Receiver r : receivers) {
                    System.out.println("            " + r.toString());
                }
            } catch (MidiUnavailableException e) {
                System.out.println("        No default receiver");
            }

            System.out.println("\n        Open transmitters:");
            List<Transmitter> transmitters = device.getTransmitters();
            for (Transmitter t : transmitters) {
                System.out.println("            " + t.toString());
            }
            try {
                System.out.println("\n        Default transmitter: " +
                        device.getTransmitter().toString());

                System.out.println("\n        Open transmitters now:");
                transmitters = device.getTransmitters();
                for (Transmitter t : transmitters) {
                    System.out.println("            " + t.toString());
                }
            } catch (MidiUnavailableException e) {
                System.out.println("        No default transmitter");
            }
            device.close();
        }
    }
}
