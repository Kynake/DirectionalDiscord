package kynake.audio;

// Java
import javax.annotation.Nonnull;

public class Utils {
  @Nonnull public static byte[] shortToByteArray(@Nonnull short[] array, boolean isBigEndian) {
    byte[] res = new byte[array.length * Short.BYTES];
    if(isBigEndian) {
      for(int i = 0; i < array.length; i++) {
        res[i*2] = (byte) (array[i] >> Byte.SIZE);
        res[i*2 + 1] = (byte) array[i];
      }
    } else {
      for(int i = 0; i < array.length; i++) {
        res[i*2] = (byte) array[i];
        res[i*2 + 1] = (byte) (array[i] >> Byte.SIZE);
      }
    }

    return res;
  }

  @Nonnull public static short[] byteToShortArray(@Nonnull byte[] array, boolean isBigEndian) {
    short[] res = new short[array.length / Short.BYTES];
    if(isBigEndian) {
      for(int i = 0; i < res.length; i++) {
        res[i] = (short) (array[i * 2] << 8 | array[i * 2 + 1] & 0xff);
      }
    } else {
      for(int i = 0; i < res.length; i++) {
        res[i] = (short) (array[i * 2 + 1] << 8 | array[i * 2] & 0xff);
      }
    }

    return res;
  }
}
