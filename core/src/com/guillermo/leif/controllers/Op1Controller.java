package com.guillermo.leif.controllers;

import com.guillermo.leif.screens.MidiDropsGameScreen;

import static com.guillermo.leif.utility.Constants.MAX_BUCKET_SPEED;
import static com.guillermo.leif.utility.Constants.MIN_BUCKET_SPEED;

public class Op1Controller {
    private final int MAX_ENCODER_VALUE = 127;
    int lastBlueEncoderValue = -1;
    int lastOrangeEncoderValue = MAX_ENCODER_VALUE / 2;
    private MidiDropsGameScreen midiDropsGameScreen;

    public Op1Controller(MidiDropsGameScreen midiDropsGameScreen) {
        this.midiDropsGameScreen = midiDropsGameScreen;
    }

    public void midiRecieved(byte[] msg, long timestamp) {
        int encoderValue = msg[1];
        int messageValue = msg[2];

        System.out.println("encoderValue: " + encoderValue);
        switch (encoderValue) {
            // Encoders turned:
            case 1:
                handleBlueEncoderTurned(messageValue);
                break;
            case 2:
                System.out.println("Green encoder");
                break;
            case 3:
                System.out.println("White encoder");
                break;
            case 4: // Orange encoder controls speed
                handleOrangeEncoderTurned(messageValue);
                break;
            // Encoders Pressed.
            // Blue encoder pressed.
            case 64: break;
            case 65: // Green encoder pressed.
                System.out.println("pause!" + messageValue);

                if(messageValue == 127) {
                    midiDropsGameScreen.togglePause();
                }
                break;
            // White encoder pressed
            case 66: break;
            // Orange encoder pressed.
            case 67: break;

        }
    }

    private void handleBlueEncoderTurned(int messageValue) {
        if (lastBlueEncoderValue == 0 || lastBlueEncoderValue > messageValue) { // move left
            midiDropsGameScreen.setMoveBucketDirection(-1);
        } else if (lastBlueEncoderValue == MAX_ENCODER_VALUE || (lastBlueEncoderValue < messageValue && (-1 != lastBlueEncoderValue))) { // move right
            midiDropsGameScreen.setMoveBucketDirection(1);
        } else {
            midiDropsGameScreen.setMoveBucketDirection(0); // don't move
        }

        lastBlueEncoderValue = messageValue;
    }

    private void handleOrangeEncoderTurned(int messageValue) {
        int bucketSpeed = midiDropsGameScreen.getBucketSpeed();

        if (lastOrangeEncoderValue == 0 || lastOrangeEncoderValue > messageValue) { //
            bucketSpeed--;
        } else if (lastOrangeEncoderValue == 127 || lastOrangeEncoderValue < messageValue) {
            bucketSpeed++;
        }

        if (bucketSpeed > MAX_BUCKET_SPEED) {
            bucketSpeed = MAX_BUCKET_SPEED;
        } else if (bucketSpeed < MIN_BUCKET_SPEED) {
            bucketSpeed = MIN_BUCKET_SPEED;
        }

        lastOrangeEncoderValue = messageValue;
        midiDropsGameScreen.setBucketSpeed(bucketSpeed);
    }


}
