package Models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexDump {

    // TODO: test case funktioniert noch nicht => perfomanter, wartekreis, oder zeile für zeile laden
    // C:\Users\hecke\Documents\Berufsschule\2FA131\DE\Angebotsanfrage.docx

    private final int bytesPerLine = 16;
    private final int hexFrontLength = 8;
    private final String separationString = " ";
    private final char uknownCharReplacement = '.';
    private final Charset encoding = StandardCharsets.UTF_8;

    private String formattedHexDump;
    private byte[] data;


    public HexDump(String file) throws IOException {
        Path path = Paths.get(file);
        byte[] data = Files.readAllBytes(path);
        this.data = data;
        this.formattedHexDump = formatHexDumpByLines(data);
    }

    /**
     * creates hex dump for byte array line by line
     *
     * @param data
     * @return
     */
    private String formatHexDumpByLines(byte[] data) {
        String hexDump = "";
        for (int i = 0; i < data.length; i = i + bytesPerLine) {
            // letzte Zeile
            if (i > data.length - bytesPerLine) {
                hexDump += formatHexDumpLine(i, Arrays.copyOfRange(data, i, data.length), true);
            } else {
                hexDump += formatHexDumpLine(i, Arrays.copyOfRange(data, i, i + bytesPerLine), false);
            }
            hexDump += "\n";
        }
        return hexDump;
    }

    /**
     * creates a single line of hex dump
     *
     * @param lineIndex
     * @param data
     * @return
     */
    private String formatHexDumpLine(int lineIndex, byte[] data, boolean isLastLine) {
        String idk = intToHex(lineIndex, hexFrontLength);

        String hexData = formatHexData(bytesToHex(data), data.length < bytesPerLine);

        char[] translation = new String(data, this.encoding).toCharArray();

        //translation = new String(data, this.encoding);
        //.replace("\n",uknownCharReplacement)
        //.replace("\r", uknownCharReplacement)
        //.replace("\t",uknownCharReplacement);
        for (int i = 0; i < translation.length; i++) {
            char character = translation[i];
            if (Character.isAlphabetic(character) || Character.isDigit(character)) {

            } else {
                translation[i] = uknownCharReplacement;
            }
        }

        // last line (quick and dirty)
        Pattern trailing00Pattern = Pattern.compile("((00\\s*)+$)");

        String lastLine = "";
        if (isLastLine) {
            int amountOfEmptyBytes = bytesPerLine;
            for (int i = 0; i < data.length; i++) {
                amountOfEmptyBytes--;

            }
            String trailing00Data = "";
            Matcher matcher = trailing00Pattern.matcher(hexData);
            if (matcher.find()) {
                trailing00Data = matcher.group(1);
            }

            StringBuilder replaceString =
                    new StringBuilder(trailing00Data.length());

            for (int i = 0; i < trailing00Data.length(); i++) {
                replaceString.append(" ");
            }

            hexData = hexData.replaceAll(trailing00Pattern.pattern(), replaceString.toString());

            lastLine = "\n" + intToHex(lineIndex + bytesPerLine - amountOfEmptyBytes + 1, hexFrontLength);
        }

        return idk + separationString + hexData + separationString + new String(translation) + lastLine;
    }

    /**
     * add whitespaces to create a nice readable hex data string
     *
     * @param hexData
     * @return
     */
    private String formatHexData(String hexData, boolean isLastLine) {
        //hexData = hexData.replaceAll("00+$", "  ");
        String formattedHex = "";
        if (isLastLine) {
            StringBuilder trailingSpaces = new StringBuilder();
            for (int i = 0; i < (bytesPerLine * 2) - hexData.length(); i++) {
                trailingSpaces.append(" ");
            }
            hexData += trailingSpaces.toString();
        }

        for (int i = 0; i < hexData.length(); i++) {
            formattedHex += hexData.charAt(i);
            if ((i + 1) % 2 == 0) {
                formattedHex += " ";
            }
            if (i + 1 == hexData.length() / 2) {
                formattedHex += " ";
            }
        }


        return formattedHex;//.replace("00","  "); // only for last line, überdenken!! quick and dirty
    }

    /**
     * converts an integer to a hex string with the specified length
     *
     * @param lineIndex
     * @param hexLength
     * @return
     */
    private String intToHex(int lineIndex, int hexLength) {
        String hexString = "";
        String hex = String.format("%X", lineIndex);
        int missingCharsNumber = hexLength - hex.length();
        for (int i = 0; i < missingCharsNumber; i++) {
            hexString += 0;
        }
        hexString += hex;

        return hexString;
    }

    // help methods

    /**
     * converts a byte array to hexadecimal string
     *
     * @param bytes
     * @return
     */
    private String bytesToHex(byte[] bytes) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    // getter and setter
    public String getFormattedHexDump() {
        return formattedHexDump;
    }
}
