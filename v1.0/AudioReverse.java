/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.io.*;
import java.util.Arrays;

public class AudioReverse extends AudioBaseClass
{
	public AudioReverse(String fileInDir)
	{
		super(fileInDir);
	}

	public AudioReverse(String fileInDir, String fileOutDir)
	{
		super(fileInDir, fileOutDir);
	}

	@Override
	public boolean runDSP()
	{
		if(this.status != AudioBaseClass.Status.INITIALIZED) return false;

		if(!this.fileTempCreate())
		{
			this.errMsg = "AudioReverse.runDSP: Error: failed to create temporary DSP file.";
			return false;
		}

		this.fileTempPos = 0L;

		switch(this.format)
		{
			case I16:
				if(!this.dspInitI16())
				{
					this.fileTempClose();
					return false;
				}

				if(!this.dspLoopI16())
				{
					this.fileTempClose();
					return false;
				}

				break;

			case I24:
				if(!this.dspInitI24())
				{
					this.fileTempClose();
					return false;
				}

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

	private boolean dspInitI16()
	{
		final long AUDIO_DATALENGTH_BYTES = this.audioDataEnd - this.audioDataBegin;
		final long AUDIO_DATALENGTH_SAMPLES = AUDIO_DATALENGTH_BYTES/2;
		final long AUDIO_DATALENGTH_FRAMES = AUDIO_DATALENGTH_SAMPLES/((long) this.nChannels);

		final int N_FRAMES_REMAINING = (int) (AUDIO_DATALENGTH_FRAMES%((long) this.bufferSizeFrames));
		final int N_SAMPLES_REMAINING = N_FRAMES_REMAINING*this.nChannels;
		final int N_BYTES_REMAINING = N_SAMPLES_REMAINING*2;

		byte[] byteBuffer = new byte[N_BYTES_REMAINING];

		short[] bufferIn = new short[N_SAMPLES_REMAINING];
		short[] bufferOut = new short[N_SAMPLES_REMAINING];

		int nFrame = 0;
		int nCounterFrame = 0;
		int nSample = 0;
		int nCounterSample = 0;
		int nChannel = 0;
		int nByte = 0;

		Arrays.fill(byteBuffer, (byte) 0);

		this.fileInPos = this.audioDataEnd - ((long) N_BYTES_REMAINING);

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
			this.errMsg = "AudioReverse.dspInitI16: Error: RandomAccessFile.readFully failed.";
			return false;
		}

		this.fileInPos -= (long) this.bufferSizeBytes;

		nByte = 0;
		for(nSample = 0; nSample < N_SAMPLES_REMAINING; nSample++)
		{
			bufferIn[nSample] = (short) NumUtils.bytesToI16LE(byteBuffer, nByte);
			nByte += 2;
		}

		for(nFrame = 0; nFrame < N_FRAMES_REMAINING; nFrame++)
		{
			nCounterFrame = N_FRAMES_REMAINING - nFrame - 1;
			for(nChannel = 0; nChannel < this.nChannels; nChannel++)
			{
				nSample = nFrame*this.nChannels + nChannel;
				nCounterSample = nCounterFrame*this.nChannels + nChannel;

				bufferOut[nSample] = bufferIn[nCounterSample];
			}
		}

		nByte = 0;
		for(nSample = 0; nSample < N_SAMPLES_REMAINING; nSample++)
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
			this.errMsg = "AudioReverse.dspInitI16: Error: RandomAccessFile.write failed.";
			return false;
		}

		this.fileTempPos = (long) N_BYTES_REMAINING;

		return true;
	}

	private boolean dspInitI24()
	{
		final long AUDIO_DATALENGTH_BYTES = this.audioDataEnd - this.audioDataBegin;
		final long AUDIO_DATALENGTH_SAMPLES = AUDIO_DATALENGTH_BYTES/3;
		final long AUDIO_DATALENGTH_FRAMES = AUDIO_DATALENGTH_SAMPLES/((long) this.nChannels);

		final int N_FRAMES_REMAINING = (int) (AUDIO_DATALENGTH_FRAMES%((long) this.bufferSizeFrames));
		final int N_SAMPLES_REMAINING = N_FRAMES_REMAINING*this.nChannels;
		final int N_BYTES_REMAINING = N_SAMPLES_REMAINING*3;

		byte[] byteBuffer = new byte[N_BYTES_REMAINING];

		int[] bufferIn = new int[N_SAMPLES_REMAINING];
		int[] bufferOut = new int[N_SAMPLES_REMAINING];

		int nFrame = 0;
		int nCounterFrame = 0;
		int nSample = 0;
		int nCounterSample = 0;
		int nChannel = 0;
		int nByte = 0;

		Arrays.fill(byteBuffer, (byte) 0);

		this.fileInPos = this.audioDataEnd - ((long) N_BYTES_REMAINING);

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
			this.errMsg = "AudioReverse.dspInitI24: Error: RandomAccessFile.readFully failed.";
			return false;
		}

		this.fileInPos -= (long) this.bufferSizeBytes;

		nByte = 0;
		for(nSample = 0; nSample < N_SAMPLES_REMAINING; nSample++)
		{
			bufferIn[nSample] = (int) NumUtils.bytesToI24LE(byteBuffer, nByte);
			nByte += 3;
		}

		for(nFrame = 0; nFrame < N_FRAMES_REMAINING; nFrame++)
		{
			nCounterFrame = N_FRAMES_REMAINING - nFrame - 1;
			for(nChannel = 0; nChannel < this.nChannels; nChannel++)
			{
				nSample = nFrame*this.nChannels + nChannel;
				nCounterSample = nCounterFrame*this.nChannels + nChannel;

				bufferOut[nSample] = bufferIn[nCounterSample];
			}
		}

		nByte = 0;
		for(nSample = 0; nSample < N_SAMPLES_REMAINING; nSample++)
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
			this.errMsg = "AudioReverse.dspInitI24: Error: RandomAccessFile.write failed.";
			return false;
		}

		this.fileTempPos = (long) N_BYTES_REMAINING;

		return true;
	}

	private boolean dspLoopI16()
	{
		byte[] byteBuffer = new byte[this.bufferSizeBytes];

		short[] bufferIn = new short[this.bufferSizeSamples];
		short[] bufferOut = new short[this.bufferSizeSamples];

		int nFrame = 0;
		int nCounterFrame = 0;
		int nSample = 0;
		int nCounterSample = 0;
		int nChannel = 0;
		int nByte = 0;

		while(this.fileInPos >= this.audioDataBegin)
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
				this.errMsg = "AudioReverse.dspLoopI16: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos -= (long) this.bufferSizeBytes;

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				bufferIn[nSample] = (short) NumUtils.bytesToI16LE(byteBuffer, nByte);
				nByte += 2;
			}

			for(nFrame = 0; nFrame < this.bufferSizeFrames; nFrame++)
			{
				nCounterFrame = this.bufferSizeFrames - nFrame - 1;
				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nSample = nFrame*this.nChannels + nChannel;
					nCounterSample = nCounterFrame*this.nChannels + nChannel;

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
				this.errMsg = "AudioReverse.dspLoopI16: Error: RandomAccessFile.write failed.";
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
		int nCounterFrame = 0;
		int nSample = 0;
		int nCounterSample = 0;
		int nChannel = 0;
		int nByte = 0;

		while(this.fileInPos >= this.audioDataBegin)
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
				this.errMsg = "AudioReverse.dspLoopI24: Error: RandomAccessFile.readFully failed.";
				return false;
			}

			this.fileInPos -= (long) this.bufferSizeBytes;

			nByte = 0;
			for(nSample = 0; nSample < this.bufferSizeSamples; nSample++)
			{
				bufferIn[nSample] = (int) NumUtils.bytesToI24LE(byteBuffer, nByte);
				nByte += 3;
			}

			for(nFrame = 0; nFrame < this.bufferSizeFrames; nFrame++)
			{
				nCounterFrame = this.bufferSizeFrames - nFrame - 1;
				for(nChannel = 0; nChannel < this.nChannels; nChannel++)
				{
					nSample = nFrame*this.nChannels + nChannel;
					nCounterSample = nCounterFrame*this.nChannels + nChannel;

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
				this.errMsg = "AudioReverse.dspLoopI24: Error: RandomAccessFile.write failed.";
				return false;
			}

			this.fileTempPos += (long) this.bufferSizeBytes;
		}

		return true;
	}
}

