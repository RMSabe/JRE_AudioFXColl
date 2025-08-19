/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.io.*;
import java.util.Arrays;

public class AudioBitCrush extends AudioBaseClass
{
	private int cutoff = 0;

	public AudioBitCrush(String fileInDir)
	{
		super(fileInDir);
	}

	public AudioBitCrush(String fileInDir, String fileOutDir)
	{
		super(fileInDir, fileOutDir);
	}

	public boolean setCutoff(int bitcrush)
	{
		int limit = 0;
		int b = 0;

		switch(this.format)
		{
			case I16:
				limit = 15;
				break;

			case I24:
				limit = 23;
				break;

			default:
				return false;
		}

		if(bitcrush < 0)
		{
			this.errMsg = "AudioBitCrush.setCutoff: Error: given bitcrush value is invalid.";
			return false;
		}

		if(bitcrush >= limit)
		{
			this.errMsg = "AudioBitCrush.setCutoff: Error: given bitcrush value exceeds sample limit.";
			return false;
		}

		this.cutoff = 0;

		b = 0;
		while(b < bitcrush)
		{
			this.cutoff |= (1 << b);
			b++;
		}

		return true;
	}

	@Override
	public boolean runDSP()
	{
		if(this.status != AudioBaseClass.Status.INITIALIZED) return false;

		if(!this.fileTempCreate())
		{
			this.errMsg = "AudioBitCrush.runDSP: Error: failed to create temporary DSP file.";
			return false;
		}

		this.fileInPos = this.audioDataBegin;
		this.fileTempPos = 0L;

		switch(this.format)
		{
			case I16:
				if(!this.dspLoopI16())
				{
					this.fileTempClose();
					return false;
				}
				break;

			case I24:
				if(!this.dspLoopI24())
				{
					this.fileTempClose();
					return false;
				}
				break;
		}

		this.fileTempClose();

		return this.rawToWavProc();
	}

	private boolean dspLoopI16()
	{
		byte[] buffer = new byte[this.bufferSizeBytes];
		int sample = 0;
		int nSample = 0;

		while(this.fileInPos < this.audioDataEnd)
		{
			Arrays.fill(buffer, (byte) 0);

			try
			{
				this.fileIn.seek(this.fileInPos);
				this.fileIn.readFully(buffer);
			}
			catch(EOFException eof_e)
			{
				/*IGNORE*/
			}
			catch(Exception e)
			{
				this.errMsg = "AudioBitCrush.dspLoopI16: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos += (long) this.bufferSizeBytes;

			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				sample = (int) NumUtils.bytesToI16LE(buffer, (nSample*2));
				sample &= ~(this.cutoff);
				NumUtils.n16ToBytesLE((short) sample, buffer, (nSample*2));
			}

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.write(buffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioBitCrush.dspLoopI16: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}

	private boolean dspLoopI24()
	{
		byte[] buffer = new byte[this.bufferSizeBytes];
		int sample = 0;
		int nSample = 0;

		while(this.fileInPos < this.audioDataEnd)
		{
			Arrays.fill(buffer, (byte) 0);

			try
			{
				this.fileIn.seek(this.fileInPos);
				this.fileIn.readFully(buffer);
			}
			catch(EOFException eof_e)
			{
				/*IGNORE*/
			}
			catch(Exception e)
			{
				this.errMsg = "AudioBitCrush.dspLoopI24: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos += (long) this.bufferSizeBytes;

			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				sample = (int) NumUtils.bytesToI24LE(buffer, (nSample*3));
				sample &= ~(this.cutoff);
				NumUtils.n24ToBytesLE(sample, buffer, (nSample*3));
			}

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.write(buffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioBitCrush.dspLoopI24: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}
}

