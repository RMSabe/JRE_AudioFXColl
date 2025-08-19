/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.io.*;
import java.util.Arrays;

public class AudioChannelSwap extends AudioBaseClass
{
	public AudioChannelSwap(String fileInDir)
	{
		super(fileInDir);
	}

	public AudioChannelSwap(String fileInDir, String fileOutDir)
	{
		super(fileInDir, fileOutDir);
	}

	@Override
	public boolean runDSP()
	{
		if(this.status != AudioBaseClass.Status.INITIALIZED) return false;

		if(this.nChannels < 2)
		{
			this.errMsg = "AudioChannelSwap.runDSP: Error: this effect requires at least 2 channel audio signal.";
			return false;
		}

		if(!this.fileTempCreate())
		{
			this.errMsg = "AudioChannelSwap.runDSP: Error: failed to create temporary DSP file.";
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
		byte[] byteBuffer = new byte[this.bufferSizeBytes];

		short[] bufferIn = new short[this.bufferSizeSamples];
		short[] bufferOut = new short[this.bufferSizeSamples];

		int nFrame = 0;
		int nSample = 0;
		int nCounterSample = 0;
		int nChannel = 0;
		int nCounterChannel = 0;
		int nByte = 0;

		while(this.fileInPos < this.audioDataEnd)
		{
			Arrays.fill(byteBuffer, (byte) 0);

			try
			{
				this.fileIn.seek(this.fileInPos);
				this.fileIn.readFully(byteBuffer);
			}
			catch(EOFException eof_e)
			{
				/*IGNORE*/
			}
			catch(Exception e)
			{
				this.errMsg = "AudioChannelSwap.dspLoopI16: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos += (long) this.bufferSizeBytes;

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				bufferIn[nSample] = (short) NumUtils.bytesToI16LE(byteBuffer, nByte);
				nByte += 2;
			}

			for(nFrame = 0; nFrame < this.bufferSizeFrames; nFrame++)
			{
				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nCounterChannel = this.nChannels - nChannel - 1;

					nSample = nFrame*this.nChannels + nChannel;
					nCounterSample = nFrame*this.nChannels + nCounterChannel;

					bufferOut[nSample] = bufferIn[nCounterSample];
				}
			}

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				NumUtils.n16ToBytesLE(bufferOut[nSample], byteBuffer, nByte);
				nByte += 2;
			}

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.write(byteBuffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioChannelSwap.dspLoopI16: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}

	private boolean dspLoopI24()
	{
		byte[] byteBuffer = new byte[this.bufferSizeBytes];

		int[] bufferIn = new int[this.bufferSizeSamples];
		int[] bufferOut = new int[this.bufferSizeSamples];

		int nFrame = 0;
		int nSample = 0;
		int nCounterSample = 0;
		int nChannel = 0;
		int nCounterChannel = 0;
		int nByte = 0;

		while(this.fileInPos < this.audioDataEnd)
		{
			Arrays.fill(byteBuffer, (byte) 0);

			try
			{
				this.fileIn.seek(this.fileInPos);
				this.fileIn.readFully(byteBuffer);
			}
			catch(EOFException eof_e)
			{
				/*IGNORE*/
			}
			catch(Exception e)
			{
				this.errMsg = "AudioChannelSwap.dspLoopI24: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos += (long) this.bufferSizeBytes;

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				bufferIn[nSample] = (int) NumUtils.bytesToI24LE(byteBuffer, nByte);
				nByte += 3;
			}

			for(nFrame = 0; nFrame < this.bufferSizeFrames; nFrame++)
			{
				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nCounterChannel = this.nChannels - nChannel - 1;

					nSample = nFrame*this.nChannels + nChannel;
					nCounterSample = nFrame*this.nChannels + nCounterChannel;

					bufferOut[nSample] = bufferIn[nCounterSample];
				}
			}

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				NumUtils.n24ToBytesLE(bufferOut[nSample], byteBuffer, nByte);
				nByte += 3;
			}

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.write(byteBuffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioChannelSwap.dspLoopI24: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}
}

