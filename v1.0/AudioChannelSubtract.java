/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.io.*;
import java.util.Arrays;

public class AudioChannelSubtract extends AudioBaseClass
{
	public AudioChannelSubtract(String fileInDir)
	{
		super(fileInDir);
	}

	public AudioChannelSubtract(String fileInDir, String fileOutDir)
	{
		super(fileInDir, fileOutDir);
	}

	@Override
	public boolean runDSP()
	{
		if(this.status != AudioBaseClass.Status.INITIALIZED) return false;

		if(this.nChannels < 2)
		{
			this.errMsg = "AudioChannelSubtract.runDSP: Error: this effect requires at least 2 channel audio signal.";
			return false;
		}

		if(!this.fileTempCreate())
		{
			this.errMsg = "AudioChannelSubtract.runDSP: Error: failed to create temporary DSP file.";
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
		final int SAMPLE_MAX_VALUE = 0x7fff;
		final int SAMPLE_MIN_VALUE = -0x8000;

		byte[] byteBuffer = new byte[this.bufferSizeBytes];

		short[] buffer = new short[this.bufferSizeSamples];

		int nFrame = 0;
		int nSample = 0;
		int nChannel = 0;
		int nByte = 0;

		int monoSample = 0;
		int channelSample = 0;

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
				this.errMsg = "AudioChannelSubtract.dspLoopI16: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos += (long) this.bufferSizeBytes;

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				buffer[nSample] = (short) NumUtils.bytesToI16LE(byteBuffer, nByte);
				nByte += 2;
			}

			for(nFrame = 0; nFrame < this.bufferSizeFrames; nFrame++)
			{
				monoSample = 0;
				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nSample = nFrame*this.nChannels + nChannel;
					monoSample += (int) buffer[nSample];
				}

				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nSample = nFrame*this.nChannels + nChannel;

					channelSample = (int) buffer[nSample];
					channelSample *= this.nChannels;
					channelSample -= monoSample;
					channelSample /= this.nChannels;

					if(channelSample > SAMPLE_MAX_VALUE) buffer[nSample] = (short) SAMPLE_MAX_VALUE;
					else if(channelSample < SAMPLE_MIN_VALUE) buffer[nSample] = (short) SAMPLE_MIN_VALUE;
					else buffer[nSample] = (short) channelSample;
				}
			}

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				NumUtils.n16ToBytesLE(buffer[nSample], byteBuffer, nByte);
				nByte += 2;
			}

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.write(byteBuffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioChannelSubtract.dspLoopI16: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}

	private boolean dspLoopI24()
	{
		final int SAMPLE_MAX_VALUE = 0x7fffff;
		final int SAMPLE_MIN_VALUE = -0x800000;

		byte[] byteBuffer = new byte[this.bufferSizeBytes];

		int[] buffer = new int[this.bufferSizeSamples];

		int nFrame = 0;
		int nSample = 0;
		int nChannel = 0;
		int nByte = 0;

		int monoSample = 0;

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
				this.errMsg = "AudioChannelSubtract.dspLoopI24: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos += (long) this.bufferSizeBytes;

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				buffer[nSample] = (int) NumUtils.bytesToI24LE(byteBuffer, nByte);
				nByte += 3;
			}

			for(nFrame = 0; nFrame < this.bufferSizeFrames; nFrame++)
			{
				monoSample = 0;

				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nSample = nFrame*this.nChannels + nChannel;
					monoSample += buffer[nSample];
				}

				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nSample = nFrame*this.nChannels + nChannel;

					buffer[nSample] *= this.nChannels;
					buffer[nSample] -= monoSample;
					buffer[nSample] /= this.nChannels;

					if(buffer[nSample] > SAMPLE_MAX_VALUE) buffer[nSample] = SAMPLE_MAX_VALUE;
					else if(buffer[nSample] < SAMPLE_MIN_VALUE) buffer[nSample] = SAMPLE_MIN_VALUE;
				}
			}

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				NumUtils.n24ToBytesLE(buffer[nSample], byteBuffer, nByte);
				nByte += 3;
			}

			try
			{
				this.fileTemp.seek(this.fileTempPos);
				this.fileTemp.write(byteBuffer);
			}
			catch(Exception e)
			{
				this.errMsg = "AudioChannelSubtract.dspLoopI24: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}
}

