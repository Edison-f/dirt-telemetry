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
 * Shift Light Support:     152-156
 * Position:                156-160
 * Regen Brake Level:       160-164
 * Regen Brake Max Level:   164-168
 * Drag Reduction System:   168-172
 * Traction Control:        172-176
 * Anti-Lock Brakes:        176-180
 * Fuel in Tank:            180-184
 * Fuel Capacity:           184-188
 * In Pit:                  188-192
 * Split Number:            192-196 //TODO
 * Split 1 Time:            196-200
 * Split 2 Time:            200-204
 * Brake Temperature:
 *      Rear Left:          204-208
 *      Rear Right:         208-212
 *      Front Left:         212-216
 *      Front Right:        216-220
 * Junk:                    220-224 // Next 4 supposedly tire pressure
 * Junk:                    224-228
 * Junk:                    228-232
 * Junk:                    232-236
 * Laps Completed:          236-240 //TODO
 * Total Laps:              240-244
 * Track Length:            244-248
 * Last Lap Time:           248-252
 * Max RPM:                 252-256
 * ?:                       256-260
 * Max Gears:               260-264
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
        return parsedData;
    }

    public byte[] getBytes(int start, byte[] data) {
        byte[] result = new byte[4];
        System.arraycopy(data, start, result, 0, 4);
        return result;
    }

    public ArrayList<String> parseRPM(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("RPM");
        parsedData.add(getFloat(148, data) + "");
        parsedData.add(getFloat(252, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseSpeed(byte[] data) { // Data is in some weird format, about 1/3 of what it should be
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Speed");
        parsedData.add(getFloat(28, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseGear(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Gear");
        parsedData.add(getFloat(132, data) + "");
        parsedData.add(getFloat(260, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseVelocity(byte[] data) { // X, Y, Z
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Velocity");
        parsedData.add(getFloat(32, data) + "");
        parsedData.add(getFloat(36, data) + "");
        parsedData.add(getFloat(40, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseInputs(byte[] data) { // Throttle, Steer, Brake, Clutch
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Inputs");
        parsedData.add(getFloat(116, data) + "");
        parsedData.add(getFloat(120, data) + "");
        parsedData.add(getFloat(124, data) + "");
        parsedData.add(getFloat(128, data) + "");
        return parsedData;
    }

    public ArrayList<String> parsePosition(byte[] data) { // Sector 1, Sector 2, Sector 3
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Position");
        parsedData.add(getFloat(156, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseBrakeTemp(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Brake Temperature");
        parsedData.add(getFloat(204, data) + "");
        parsedData.add(getFloat(208, data) + "");
        parsedData.add(getFloat(212, data) + "");
        parsedData.add(getFloat(216, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseLapInfo(byte[] data) { // Current Lap, Total Laps, Track Length, Last Lap Time
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Lap Info");
        parsedData.add(getFloat(144, data) + "");
        parsedData.add(getFloat(236, data) + ""); // turns to 1 on end
        parsedData.add(getFloat(240, data) + "");
        parsedData.add(getFloat(248, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseTrackInfo(byte[] data) { // Track Length, Current Lap Distance, Distance, Location(3)
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Track Info");
        parsedData.add(getFloat(244, data) + "");
        parsedData.add(getFloat(8, data) + "");
        parsedData.add(getFloat(12, data) + "");
        parsedData.add(getFloat(16, data) + "");
        parsedData.add(getFloat(20, data) + "");
        parsedData.add(getFloat(24, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseGForces(byte[] data) { // Lateral, Longitudinal
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("G Forces");
        parsedData.add(getFloat(136, data) + "");
        parsedData.add(getFloat(140, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseTimingInfo(byte[] data) { // Time, Current Lap Time
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Timing Info");
        parsedData.add(getFloat(0, data) + "");
        parsedData.add(getFloat(4, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseRoll(byte[] data) { // Roll, Pitch, Yaw
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Roll");
        parsedData.add(getFloat(44, data) + "");
        parsedData.add(getFloat(48, data) + "");
        parsedData.add(getFloat(52, data) + "");
        return parsedData;
    }

    public ArrayList<String> parsePitch(byte[] data) { // Roll, Pitch, Yaw
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Pitch");
        parsedData.add(getFloat(56, data) + "");
        parsedData.add(getFloat(60, data) + "");
        parsedData.add(getFloat(64, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseSuspensionPosition(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Suspension Position");
        parsedData.add(getFloat(68, data) + "");
        parsedData.add(getFloat(72, data) + "");
        parsedData.add(getFloat(76, data) + "");
        parsedData.add(getFloat(80, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseSuspensionVelocity(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Suspension Velocity");
        parsedData.add(getFloat(84, data) + "");
        parsedData.add(getFloat(88, data) + "");
        parsedData.add(getFloat(92, data) + "");
        parsedData.add(getFloat(96, data) + "");
        return parsedData;
    }

    public ArrayList<String> parseWheelVelocity(byte[] data) { // Rear Left, Rear Right, Front Left, Front Right
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Wheel Velocity");
        parsedData.add(getFloat(100, data) + "");
        parsedData.add(getFloat(104, data) + "");
        parsedData.add(getFloat(108, data) + "");
        parsedData.add(getFloat(112, data) + "");
        return parsedData;
    }

    private float getFloat(int start, byte[] data) {
        return ByteBuffer.wrap(getBytes(start, data)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public ArrayList<String> parseFrom(byte[] data, int start, int finish) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("From " + start + " to " + (finish + 4));
        for(int i = start; i < data.length && i <= finish; i += 4) {
            parsedData.add(getFloat(i, data) + "");
        }
        return parsedData;
    }

    public ArrayList<String> parseWheelSlip(byte[] data) {
        ArrayList<String> parsedData = new ArrayList<>();
        parsedData.add("Wheel Slip");
        float totalVelocity = Math.abs(getFloat(32, data))
                + Math.abs(getFloat(36, data))
                + Math.abs(getFloat(40, data));
        parsedData.add(Math.abs(getFloat(100, data) - totalVelocity) + "");
        parsedData.add(Math.abs(getFloat(104, data) - totalVelocity) + "");
        parsedData.add(Math.abs(getFloat(108, data) - totalVelocity) + "");
        parsedData.add(Math.abs(getFloat(112, data) - totalVelocity) + "");
        return parsedData;
    }
}
