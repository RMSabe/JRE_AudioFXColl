JRE Audio FX Collection
Version 1.0

This is a collection of some audio effects for Java Runtime.

All the effects are non real-time effects, meaning they generate an output audio file with the processed signal from the input file.

It only supports .wav files, 16-bit and 24-bit encoding.

Effect description:

Bit Crush: reduce the sample resolution by removing a certain amount of data bits. Only data is changed, output file format remains the same as input file.

Reverse (backward audio): copies all the samples from the input file to the output file in opposite order (time-wise), but without swapping the channels.

Channel Swap: invert channels from the input file.

Channel Subtract: subtract the summed signals of all other audio channels from each audio channel.
Example:
{
	Stereo Audio:
	L = L - R
	R = R - L

	5.1 Audio:
	FL = FL - (FR + C + SL + SR + Sub)
	FR = FR - (FL + C + SL + SR + Sub)
	C = C - (FL + FR + SL + SR + Sub)
	SL = SL - (FL + FR + C + SR + Sub)
	SR = SR - (FL + FR + C + SL + Sub)
	Sub = Sub - (FL + FR + C + SL + SR)
}

Author: Rafael Sabe
Email: rafaelmsabe@gmail.com

