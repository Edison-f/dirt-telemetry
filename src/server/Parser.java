package server;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Time:                    0-4
 * Current Lap Time:        4-8
 * Current Lap Distance:    8-12
 * Distance:                12-16
 * Location:                16-20, 20-24, 24-28
 * Speed:                   28-32
 * Velocity:                32-36, 36-40, 40-44
 * Roll Vector:             44-48, 48-52, 52-56
 * Pitch Vector:            56-60, 60-64, 64-68
 * Suspension Positions:
 * Rear Left:          68-72
 * Rear Right:         72-76
 * Front Left:         76-80
 * Front Right:        80-84
 * Suspension Velocity:
 * Rear Left:          84-88
 * Rear Right:         88-92
 * Front Left:         92-96
 * Front Right:        96-100
 * Wheel Velocity:
 * Rear Left:          100-104
 * Rear Right:         104-108
 * Front Left:         108-112
 * Front Right:        112-116
 * Throttle:                116-120
 * Steer:                   120-124
 * Brake:                   124-128
 * Clutch:                  128-132
 * Gear:                    132-136
 * Lateral G-force:         136-140
 * Longitudinal G-force:    140-144
 * Current Lap:             144-148
 * RPM:                     148-152
 * ?:                       152-156 // For some reason this is 1.0 always
 * Position:                156-160
 * Junk:                    160-164
 * Junk:                    164-168, 168-172, 172-176 // Also copilot generated
 * Junk:                    176-180 // Also copilot generated
 * Junk:                    180-184 // Also copilot generated
 * Junk:                    184-188 // Also copilot generated
 * Junk:                    188-192 // Also copilot generated
 * Junk:                    192-196
 * Junk:                    196-200
 * Junk:                    200-204
 * Brake Temperature:
 * Rear Left:          204-208
 * Rear Right:         208-212
 * Front Left:         212-216
 * Front Right:        216-220
 * Junk:                    220-224
 * Junk:                    224-228
 * Junk:                    228-232
 * Junk:                    232-236
 * Junk:                    236-240
 * Total Laps:              240-244
 * Track Length:            244-248
 * Last Lap Time:           248-252
 * Max RPM:                 252-256
 */

public class Parser {
    public ArrayList<ArrayList<String>> parseAll(byte[] data) {
        ArrayList<ArrayList<String>> parsedData = new ArrayList<>();
        parsedData.add(parseRPM(data));
        parsedData.add(parseSpeed(data));
        parsedData.add(parseGear(data));
        parsedData.add(parseVelocity(data));
        parsedData.add(parseInputs(data));
        parsedData.add(parsePosition(data));
        parsedData.add(parseBrakeTemp(data));
        parsedData.add(parseLapInfo(data));
        parsedData.add(parseGForces(data));
        parsedData.add(parseTrackInfo(data));
        parsedData.add(parseTimingInfo(data));
        parsedData.add(parseRoll(data));
        parsedData.add(parsePitch(data));
        parsedData.add(parseSuspensionPosition(data));
        parsedData.add(parseSuspensionVelocity(data));
        parsedData.add(parseWheelVelocity(data));
        parsedData.add(parseJunk(data));
        parsedData.add(parseNonZeroJunk(data));
        return parsedData;
    }

    public byte[] getBytes(int start, byte[] data) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = data[start + i];
        }
        return result;
    }

    public ArrayList<String> parseRPM(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("RPM");
        parsedData.add(ByteBuffer.wrap(getBytes(148, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(252, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseSpeed(byte[] data) { // Data is in some weird format, about 1/3 of what it should be
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Speed");
        parsedData.add(ByteBuffer.wrap(getBytes(28, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseGear(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Gear");
        parsedData.add(ByteBuffer.wrap(getBytes(132, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseVelocity(byte[] data) { // X, Y, Z
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Velocity");
        parsedData.add(ByteBuffer.wrap(getBytes(32, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(36, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(40, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseInputs(byte[] data) { // Throttle, Steer, Brake, Clutch
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Inputs");
        parsedData.add(ByteBuffer.wrap(getBytes(116, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(120, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(124, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(128, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parsePosition(byte[] data) { // Sector 1, Sector 2, Sector 3
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Position");
        parsedData.add(ByteBuffer.wrap(getBytes(156, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseBrakeTemp(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Brake Temperature");
        parsedData.add(ByteBuffer.wrap(getBytes(204, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(208, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(212, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(216, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseLapInfo(byte[] data) { // Current Lap, Total Laps, Track Length, Last Lap Time
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Lap Info");
        parsedData.add(ByteBuffer.wrap(getBytes(144, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(240, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(248, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseTrackInfo(byte[] data) { // Track Length, Current Lap Distance, Distance, Location(3)
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Track Info");
        parsedData.add(ByteBuffer.wrap(getBytes(244, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(8, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(12, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(16, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(20, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(24, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseGForces(byte[] data) { // Lateral, Longitudinal
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("G Forces");
        parsedData.add(ByteBuffer.wrap(getBytes(136, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(140, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseTimingInfo(byte[] data) { // Time, Current Lap Time
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Timing Info");
        parsedData.add(ByteBuffer.wrap(getBytes(0, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(4, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseRoll(byte[] data) { // Roll, Pitch, Yaw
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Roll");
        parsedData.add(ByteBuffer.wrap(getBytes(44, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(48, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(52, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parsePitch(byte[] data) { // Roll, Pitch, Yaw
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Pitch");
        parsedData.add(ByteBuffer.wrap(getBytes(56, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(60, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(64, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseSuspensionPosition(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Suspension Position");
        parsedData.add(ByteBuffer.wrap(getBytes(68, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(72, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(76, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(80, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseSuspensionVelocity(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Suspension Velocity");
        parsedData.add(ByteBuffer.wrap(getBytes(84, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(88, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(92, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(96, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseWheelVelocity(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Wheel Velocity");
        parsedData.add(ByteBuffer.wrap(getBytes(100, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(104, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(108, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(112, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseJunk(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("ZeroJunk");
        parsedData.add(ByteBuffer.wrap(getBytes(160, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(164, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(168, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(172, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(176, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(180, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(184, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(188, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(192, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(196, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(200, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(220, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(224, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(228, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(232, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        parsedData.add(ByteBuffer.wrap(getBytes(236, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }

    public ArrayList<String> parseNonZeroJunk(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("NonZeroJunk");
        parsedData.add(ByteBuffer.wrap(getBytes(152, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat() + "");
        return parsedData;
    }
}
