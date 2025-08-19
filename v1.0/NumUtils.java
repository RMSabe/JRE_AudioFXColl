/*
 * Number Utility Class
 * Version 1.1
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

public class NumUtils
{
	/*
	 * binCast...() methods: binary casting.
	 * Basically cast different types of integers as raw binary (unsigned integers).
	 *
	 * None of these binCast methods would be necessary if Java natively supported unsigned integer types :(
	 */

	public static long binCastN32ToN64(int n32)
	{
		long output = 0L;

		output = (long) n32;
		output &= 0xffffffffL;

		return output;
	}

	public static int binCastN16ToN32(short n16)
	{
		int output = 0;

		output = (int) n16;
		output &= 0xffff;

		return output;
	}

	public static short binCastN8ToN16(byte n8)
	{
		short output = 0;

		output = (short) n8;
		output &= 0xff;

		return output;
	}

	/*
	 * Integer get lower and higher binary half values.
	 */

	public static long n64GetL32(long n64)
	{
		long output = 0L;

		output = (n64 & 0xffffffffL);

		return output;
	}

	public static long n64GetH32(long n64)
	{
		long output = 0L;

		output = (n64 >> 32);

		output &= 0xffffffffL;

		return output;
	}

	public static int n32GetL16(int n32)
	{
		int output = 0;

		output = (n32 & 0xffff);

		return output;
	}

	public static int n32GetH16(int n32)
	{
		int output = 0;

		output = (n32 >> 16);

		output &= 0xffff;

		return output;
	}

	public static short n16GetL8(short n16)
	{
		short output = 0;

		output = (short) (n16 & 0xff);

		return output;
	}

	public static short n16GetH8(short n16)
	{
		short output = 0;

		output = (short) (n16 >> 8);

		output &= 0xff;

		return output;
	}

	/*
	 * Extract unsigned/signed 32bit/24bit/16bit integer values from a byte array, little-endian/big-endian
	 */

	public static long bytesToU32LE(byte[] byteArray, int offset)
	{
		long output = 0L;

		if(offset < 0) return 0L;
		if(byteArray.length < (offset + 4)) return 0L;

		output |= (((long) byteArray[offset + 3]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 2]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 1]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset]) & 0xffL);

		return output;
	}

	public static long bytesToI32LE(byte[] byteArray, int offset)
	{
		long output = 0L;
		long aux = 0L;

		aux = (long) ~(0xffffffffL);
		output = bytesToU32LE(byteArray, offset);

		if((output & 0x80000000L) == 0L) return output;

		return (aux | output);
	}

	public static long bytesToU24LE(byte[] byteArray, int offset)
	{
		long output = 0L;

		if(offset < 0) return 0L;
		if(byteArray.length < (offset + 3)) return 0L;

		output |= (((long) byteArray[offset + 2]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 1]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset]) & 0xffL);

		return output;
	}

	public static long bytesToI24LE(byte[] byteArray, int offset)
	{
		long output = 0L;
		long aux = 0L;

		aux = (long) ~(0xffffffL);
		output = bytesToU24LE(byteArray, offset);

		if((output & 0x800000L) == 0L) return output;

		return (aux | output);
	}

	public static long bytesToU16LE(byte[] byteArray, int offset)
	{
		long output = 0L;

		if(offset < 0) return 0L;
		if(byteArray.length < (offset + 2)) return 0L;

		output |= (((long) byteArray[offset + 1]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset]) & 0xffL);

		return output;
	}

	public static long bytesToI16LE(byte[] byteArray, int offset)
	{
		long output = 0L;
		long aux = 0L;

		aux = (long) ~(0xffffL);
		output = bytesToU16LE(byteArray, offset);

		if((output & 0x8000L) == 0L) return output;

		return (aux | output);
	}

	public static long bytesToU32BE(byte[] byteArray, int offset)
	{
		long output = 0L;

		if(offset < 0) return 0L;
		if(byteArray.length < (offset + 4)) return 0L;

		output |= (((long) byteArray[offset]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 1]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 2]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 3]) & 0xffL);

		return output;
	}

	public static long bytesToI32BE(byte[] byteArray, int offset)
	{
		long output = 0L;
		long aux = 0L;

		aux = (long) ~(0xffffffffL);
		output = bytesToU32BE(byteArray, offset);

		if((output & 0x80000000L) == 0L) return output;

		return (aux | output);
	}

	public static long bytesToU24BE(byte[] byteArray, int offset)
	{
		long output = 0L;

		if(offset < 0) return 0L;
		if(byteArray.length < (offset + 3)) return 0L;

		output |= (((long) byteArray[offset]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 1]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 2]) & 0xffL);

		return output;
	}

	public static long bytesToI24BE(byte[] byteArray, int offset)
	{
		long output = 0L;
		long aux = 0L;

		aux = (long) ~(0xffffffL);
		output = bytesToU24BE(byteArray, offset);

		if((output & 0x800000L) == 0L) return output;

		return (aux | output);
	}

	public static long bytesToU16BE(byte[] byteArray, int offset)
	{
		long output = 0L;

		if(offset < 0) return 0L;
		if(byteArray.length < (offset + 2)) return 0L;

		output |= (((long) byteArray[offset]) & 0xffL);
		output = (output << 8);
		output |= (((long) byteArray[offset + 1]) & 0xffL);

		return output;
	}

	public static long bytesToI16BE(byte[] byteArray, int offset)
	{
		long output = 0L;
		long aux = 0L;

		aux = (long) ~(0xffffL);
		output = bytesToU16BE(byteArray, offset);

		if((output & 0x8000L) == 0L) return output;

		return (aux | output);
	}

	/*
	 * Writes a 32bit/24bit/16bit value into a byte array, little-endian or big-endian
	 */

	public static boolean n32ToBytesLE(int n32, byte[] byteArray, int offset)
	{
		if(offset < 0) return false;
		if(byteArray.length < (offset + 4)) return false;

		byteArray[offset] = (byte) (n32 & 0xff);
		byteArray[offset + 1] = (byte) ((n32 >> 8) & 0xff);
		byteArray[offset + 2] = (byte) ((n32 >> 16) & 0xff);
		byteArray[offset + 3] = (byte) (n32 >> 24);

		return true;
	}

	public static boolean n24ToBytesLE(int n24, byte[] byteArray, int offset)
	{
		if(offset < 0) return false;
		if(byteArray.length < (offset + 3)) return false;

		byteArray[offset] = (byte) (n24 & 0xff);
		byteArray[offset + 1] = (byte) ((n24 >> 8) & 0xff);
		byteArray[offset + 2] = (byte) ((n24 >> 16) & 0xff);

		return true;
	}

	public static boolean n16ToBytesLE(short n16, byte[] byteArray, int offset)
	{
		if(offset < 0) return false;
		if(byteArray.length < (offset + 2)) return false;

		byteArray[offset] = (byte) (n16 & 0xff);
		byteArray[offset + 1] = (byte) (n16 >> 8);

		return true;
	}

	public static boolean n32ToBytesBE(int n32, byte[] byteArray, int offset)
	{
		if(offset < 0) return false;
		if(byteArray.length < (offset + 4)) return false;

		byteArray[offset] = (byte) (n32 >> 24);
		byteArray[offset + 1] = (byte) ((n32 >> 16) & 0xff);
		byteArray[offset + 2] = (byte) ((n32 >> 8) & 0xff);
		byteArray[offset + 3] = (byte) (n32 & 0xff);

		return true;
	}

	public static boolean n24ToBytesBE(int n24, byte[] byteArray, int offset)
	{
		if(offset < 0) return false;
		if(byteArray.length < (offset + 3)) return false;

		byteArray[offset] = (byte) ((n24 >> 16) & 0xff);
		byteArray[offset + 1] = (byte) ((n24 >> 8) & 0xff);
		byteArray[offset + 2] = (byte) (n24 & 0xff);

		return true;
	}

	public static boolean n16ToBytesBE(short n16, byte[] byteArray, int offset)
	{
		if(offset < 0) return false;
		if(byteArray.length < (offset + 2)) return false;

		byteArray[offset] = (byte) (n16 >> 8);
		byteArray[offset + 1] = (byte) (n16 & 0xff);

		return true;
	}
}

