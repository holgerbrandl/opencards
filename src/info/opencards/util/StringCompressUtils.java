package info.opencards.util;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


/**
 * Some static utility function to  inflate/deflate strings.
 *
 * @author Holger Brandl
 */
public class StringCompressUtils {


    private static final char[] hexChar = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    };


    private static String compress2(String s) {
        Deflater defl = new Deflater(Deflater.BEST_COMPRESSION);
        defl.setInput(s.getBytes());
        defl.finish();
        boolean done = false;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (!done) {
            byte[] buf = new byte[256];
            int bufnum = defl.deflate(buf);
            bos.write(buf, 0, bufnum);
            if (bufnum < buf.length)
                done = true;
        }
        try {
            bos.flush();
            bos.close();
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        }
        return toHexString(bos.toByteArray());
    }


    private static String uncompress2(String compressedHex) {
        byte[] b = toBinArray(compressedHex);

        Inflater infl = new Inflater();
        infl.setInput(b);

        StringBuilder retval = new StringBuilder();
        boolean done = false;
        while (!done) {
            byte[] buf = new byte[256];
            try {
                int bufnum = infl.inflate(buf);
                retval.append(new String(buf, 0, bufnum));
                if (bufnum < buf.length)
                    done = true;
            } catch (DataFormatException dfe) {
                done = true;
                System.err.println(dfe.toString());
            }
        }

        return (retval.toString());
    }


    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            // look up high nibble char
            sb.append(hexChar[(aB & 0xf0) >>> 4]); // fill left with zero bits

            // look up low nibble char
            sb.append(hexChar[aB & 0x0f]);
        }
        return sb.toString();
    }


    private static byte[] toBinArray(String hexStr) {
        byte bArray[] = new byte[hexStr.length() / 2];
        for (int i = 0; i < (hexStr.length() / 2); i++) {
            byte firstNibble = Byte.parseByte(hexStr.substring(2 * i, 2 * i + 1), 16); // [x,y)
            byte secondNibble = Byte.parseByte(hexStr.substring(2 * i + 1, 2 * i + 2), 16);
            int finalByte = (secondNibble) | (firstNibble << 4); // bit-operations only with numbers, not bytes.
            bArray[i] = (byte) finalByte;
        }
        return bArray;
    }


    @Test
    public void testTextCompression() {
        String input = "hallo world";
        String compressedHex = compress2(input);

        String undoCompressed = uncompress2(compressedHex);
        System.out.println("before=" + input);
        System.out.println("after=" + undoCompressed);
    }
}
