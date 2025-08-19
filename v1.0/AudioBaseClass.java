/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public abstract class AudioBaseClass
{
	protected static enum Formats {
		UNSUPPORTED,
		NULL,
		I16,
		I24
	};

	protected static enum Status {
		ERROR_BROKENHEADER,
		ERROR_FORMATNOTSUPPORTED,
		ERROR_FILENOTSUPPORTED,
		ERROR_NOFILE,
		ERROR_GENERIC,
		UNINITIALIZED,
		INITIALIZED
	};

	protected static final String FILETEMP_DIR = "temp.raw";
	protected static final String FILEOUT_DIR_DEFAULT = "output.wav";
	protected static final int BUFFER_SIZE_FRAMES_DEFAULT = 512;

	protected String fileInDir = "";
	protected String fileOutDir = "";

	protected int bufferSizeFrames = 0;
	protected int bufferSizeSamples = 0;
	protected int bufferSizeBytes = 0;

	protected Formats format = Formats.NULL;
	protected Status status = Status.UNINITIALIZED;

	protected String errMsg = "";

	protected int sampleRate = 0;
	protected int bitDepth = 0;
	protected int nChannels = 0;

	protected long audioDataBegin = 0L;
	protected long audioDataEnd = 0L;

	protected RandomAccessFile fileIn = null;
	protected RandomAccessFile fileOut = null;
	protected RandomAccessFile fileTemp = null;

	protected long fileInSize = 0L;
	protected long fileInPos = 0L;

	protected long fileOutPos = 0L;

	protected long fileTempSize = 0L;
	protected long fileTempPos = 0L;

	public AudioBaseClass(String fileInDir)
	{
		this.fileInDir = fileInDir;
		this.fileOutDir = FILEOUT_DIR_DEFAULT;
		this.bufferSizeFrames = BUFFER_SIZE_FRAMES_DEFAULT;
	}

	public AudioBaseClass(String fileInDir, String fileOutDir)
	{
		this(fileInDir);
		this.fileOutDir = fileOutDir;
	}

	public boolean initialize()
	{
		if(this.status == Status.INITIALIZED) return true;

		this.status = Status.UNINITIALIZED;

		if(!this.fileExtCheck(this.fileInDir)) return false;

		if(!this.fileInOpen())
		{
			this.status = Status.ERROR_NOFILE;
			return false;
		}

		if(!this.fileInGetParams())
		{
			this.fileInClose();
			return false;
		}

		this.bufferSizeSamples = this.bufferSizeFrames*this.nChannels;
		this.bufferSizeBytes = this.bufferSizeSamples*this.bitDepth/8;

		this.status = Status.INITIALIZED;
		return true;
	}

	public void deinitialize()
	{
		this.status = Status.UNINITIALIZED;
		this.fileInClose();
		this.fileOutClose();
		this.fileTempClose();
	}

	public abstract boolean runDSP();

	public String getLastErrorMessage()
	{
		switch(this.status)
		{
			case ERROR_BROKENHEADER:
				return "File header is missing information (probably corrupted).";

			case ERROR_FORMATNOTSUPPORTED:
				return "Audio format or encoding format is not supported.";

			case ERROR_FILENOTSUPPORTED:
				return "File format is not supported.";

			case ERROR_NOFILE:
				return "File does not exist, or cannot be accessed.";

			case ERROR_GENERIC:
				return "Something went wrong\nExtended error message: " + this.errMsg;

			case UNINITIALIZED:
				return "Audio object has not been initialized.";
		}

		return this.errMsg;
	}

	public int getSampleRate()
	{
		return this.sampleRate;
	}

	public int getBitDepth()
	{
		return this.bitDepth;
	}

	public int getNumberChannels()
	{
		return this.nChannels;
	}

	protected boolean fileExtCheck(String fileDir)
	{
		Scanner stdin = null;
		String userCmd = "";
		String auxdir = "";
		int dirlen = 0;

		auxdir = fileDir.toLowerCase();
		dirlen = auxdir.length();

		if(dirlen >= 5)
			if(compareSignature(".wav".toCharArray(), auxdir.toCharArray(), (dirlen - 4)))
				return true;

		stdin = new Scanner(System.in);

		while(true)
		{
			System.out.print("WARNING: File does not have a \".wav\" extension. Might be incompatible with this application.\nDo you wish to continue? (yes/no): ");

			userCmd = stdin.nextLine();
			userCmd = userCmd.toLowerCase();

			if(userCmd.equals("no"))
			{
				stdin.close();
				this.status = Status.ERROR_FILENOTSUPPORTED;
				return false;
			}
			
			if(userCmd.equals("yes"))
			{
				stdin.close();
				return true;
			}

			System.out.println("Error: invalid command entered.");
		}
	}

	protected boolean fileInOpen()
	{
		this.fileInClose();

		try
		{
			this.fileIn = new RandomAccessFile(this.fileInDir, "r");
			this.fileInSize = this.fileIn.length();
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}

	protected boolean fileInClose()
	{
		if(this.fileIn == null) return false;

		try
		{
			this.fileIn.close();
		}
		catch(Exception e)
		{
			return false;
		}

		this.fileIn = null;
		this.fileInSize = 0L;

		return true;
	}

	protected boolean fileOutCreate()
	{
		this.fileOutClose();

		if(!FileManager.fileCreate(this.fileOutDir)) return false;

		try
		{
			this.fileOut = new RandomAccessFile(this.fileOutDir, "rw");
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}

	protected boolean fileOutClose()
	{
		if(this.fileOut == null) return false;

		try
		{
			this.fileOut.close();
		}
		catch(Exception e)
		{
			return false;
		}

		this.fileOut = null;
		return true;
	}

	protected boolean fileTempCreate()
	{
		this.fileTempClose();

		if(!FileManager.fileCreate(FILETEMP_DIR)) return false;

		try
		{
			this.fileTemp = new RandomAccessFile(FILETEMP_DIR, "rw");
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}

	protected boolean fileTempOpen()
	{
		this.fileTempClose();

		try
		{
			this.fileTemp = new RandomAccessFile(FILETEMP_DIR, "r");
			this.fileTempSize = this.fileTemp.length();
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}

	protected boolean fileTempClose()
	{
		if(this.fileTemp == null) return false;

		try
		{
			this.fileTemp.close();
		}
		catch(Exception e)
		{
			return false;
		}

		this.fileTemp = null;
		this.fileTempSize = 0L;

		return true;
	}

	protected boolean fileInGetParams()
	{
		final int BUFFER_SIZE = 4096;
		int bytepos = 0;

		byte[] headerInfo = new byte[BUFFER_SIZE];
		long num = 0L;

		try
		{
			this.fileIn.seek(0L);
			this.fileIn.readFully(headerInfo);
		}
		catch(EOFException eof_e)
		{
			/*IGNORE*/
		}
		catch(Exception e)
		{
			this.status = Status.ERROR_GENERIC;
			this.errMsg = "AudioBaseClass.fileInGetParams: Error: RandomAccessFile.readFully failed.";
			return false;
		}

		if(!compareSignature("RIFF".toCharArray(), headerInfo, 0))
		{
			this.status = Status.ERROR_FILENOTSUPPORTED;
			return false;
		}

		if(!compareSignature("WAVE".toCharArray(), headerInfo, 8))
		{
			this.status = Status.ERROR_FILENOTSUPPORTED;
			return false;
		}

		bytepos = 12;

		while(true)
		{
			if(bytepos > (BUFFER_SIZE - 8))
			{
				this.status = Status.ERROR_BROKENHEADER;
				return false;
			}

			if(compareSignature("fmt ".toCharArray(), headerInfo, bytepos)) break;

			num = NumUtils.bytesToU32LE(headerInfo, (bytepos + 4));
			bytepos += (int) (num + 8L);
		}

		num = NumUtils.bytesToU16LE(headerInfo, (bytepos + 8));
		if(num != 1)
		{
			this.status = Status.ERROR_FORMATNOTSUPPORTED;
			return false;
		}

		this.nChannels = (int) NumUtils.bytesToU16LE(headerInfo, (bytepos + 10));
		this.sampleRate = (int) NumUtils.bytesToU32LE(headerInfo, (bytepos + 12));
		this.bitDepth = (int) NumUtils.bytesToU16LE(headerInfo, (bytepos + 22));

		num = NumUtils.bytesToU32LE(headerInfo, (bytepos + 4));
		bytepos += (int) (num + 8L);

		while(true)
		{
			if(bytepos > (BUFFER_SIZE - 8))
			{
				this.status = Status.ERROR_BROKENHEADER;
				return false;
			}

			if(compareSignature("data".toCharArray(), headerInfo, bytepos)) break;

			num = NumUtils.bytesToU32LE(headerInfo, (bytepos + 4));
			bytepos += (int) (num + 8L);
		}

		num = NumUtils.bytesToU32LE(headerInfo, (bytepos + 4));

		this.audioDataBegin = (long) (bytepos + 8);
		this.audioDataEnd = this.audioDataBegin + num;

		switch(this.bitDepth)
		{
			case 16:
				this.format = Formats.I16;
				return true;

			case 24:
				this.format = Formats.I24;
				return true;
		}

		this.format = Formats.UNSUPPORTED;
		this.status = Status.ERROR_FORMATNOTSUPPORTED;

		return false;
	}

	protected boolean fileOutWriteHeader()
	{
		byte[] headerInfo = new byte[44];

		headerInfo[0] = (byte) 'R';
		headerInfo[1] = (byte) 'I';
		headerInfo[2] = (byte) 'F';
		headerInfo[3] = (byte) 'F';

		NumUtils.n32ToBytesLE((int) (this.fileTempSize + 36L), headerInfo, 4);

		headerInfo[8] = (byte) 'W';
		headerInfo[9] = (byte) 'A';
		headerInfo[10] = (byte) 'V';
		headerInfo[11] = (byte) 'E';

		headerInfo[12] = (byte) 'f';
		headerInfo[13] = (byte) 'm';
		headerInfo[14] = (byte) 't';
		headerInfo[15] = (byte) ' ';

		NumUtils.n32ToBytesLE(16, headerInfo, 16);

		NumUtils.n16ToBytesLE((short) 1, headerInfo, 20);

		NumUtils.n16ToBytesLE((short) this.nChannels, headerInfo, 22);

		NumUtils.n32ToBytesLE(this.sampleRate, headerInfo, 24);

		NumUtils.n32ToBytesLE((this.sampleRate*this.nChannels*this.bitDepth/8), headerInfo, 28);

		NumUtils.n16ToBytesLE((short) (this.nChannels*this.bitDepth/8), headerInfo, 32);

		NumUtils.n16ToBytesLE((short) this.bitDepth, headerInfo, 34);

		headerInfo[36] = (byte) 'd';
		headerInfo[37] = (byte) 'a';
		headerInfo[38] = (byte) 't';
		headerInfo[39] = (byte) 'a';

		NumUtils.n32ToBytesLE((int) this.fileTempSize, headerInfo, 40);

		try
		{
			this.fileOut.seek(0L);
			this.fileOut.write(headerInfo);
		}
		catch(Exception e)
		{
			this.status = Status.ERROR_GENERIC;
			this.errMsg = "AudioBaseClass.fileOutWriteHeader: Error: RandomAccessFile.write failed.";
			return false;
		}

		this.fileOutPos = 44L;

		return true;
	}

	protected boolean compareSignature(char[] auth, byte[] buf, int offset)
	{
		if(auth == null) return false;
		if(buf == null) return false;

		if(offset < 0) return false;
		if(auth.length < 4) return false;
		if(buf.length < (offset + 4)) return false;

		if(auth[0] != buf[offset]) return false;
		if(auth[1] != buf[offset + 1]) return false;
		if(auth[2] != buf[offset + 2]) return false;
		if(auth[3] != buf[offset + 3]) return false;

		return true;
	}

	protected boolean compareSignature(char[] auth, char[] buf, int offset)
	{
		if(auth == null) return false;
		if(buf == null) return false;

		if(offset < 0) return false;
		if(auth.length < 4) return false;
		if(buf.length < (offset + 4)) return false;

		if(auth[0] != buf[offset]) return false;
		if(auth[1] != buf[offset + 1]) return false;
		if(auth[2] != buf[offset + 2]) return false;
		if(auth[3] != buf[offset + 3]) return false;

		return true;
	}

	protected boolean rawToWavProc()
	{
		if(this.status != Status.INITIALIZED) return false;

		if(!this.fileTempOpen())
		{
			this.errMsg = "AudioBaseClass.rawToWavProc: Error: failed to open temporary DSP file.";
			return false;
		}

		if(!this.fileOutCreate())
		{
			this.fileTempClose();
			this.errMsg = "AudioBaseClass.rawToWavProc: Error: failed to create output file.";
			return false;
		}

		if(!this.fileOutWriteHeader())
		{
			this.fileTempClose();
			this.fileOutClose();

			return false;
		}

		this.fileTempPos = 0L;

		if(!this.rawToWavProcLoop())
		{
			this.fileTempClose();
			this.fileOutClose();

			return false;
		}

		this.fileTempClose();
		this.fileOutClose();

		return true;
	}

	protected boolean rawToWavProcLoop()
	{
		byte[] buffer = new byte[this.bufferSizeBytes];

		while(this.fileTempPos < this.fileTempSize)
		{
			Arrays.fill(buffer, (byte) 0);

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.readFully(buffer);
			}
			catch(EOFException eof_e)
			{
				/*IGNORE*/
			}
			catch(Exception e)
			{
				this.errMsg = "AudioBaseClass.rawToWavProcLoop: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;

			try
			{
				this.fileOut.seek(this.fileOutPos);
				this.fileOut.write(buffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioBaseClass.rawToWavProcLoop: Error: RandomAccessFile.write failed.";
			}

			this.fileOutPos += (long) this.bufferSizeBytes;
		}

		return true;
	}
}

