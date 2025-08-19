/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.util.Scanner;

public class Main
{
	public static AudioBaseClass audioobj = null;
	public static String userCmd = "";

	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("Error: missing arguments");
			printArgList();
			return;
		}

		userCmd = args[0].toLowerCase();

		if(userCmd.equals("bitcrush")) proc_bitcrush();
		else if(userCmd.equals("reverse")) proc_reverse();
		else if(userCmd.equals("chswap")) proc_chswap();
		else if(userCmd.equals("chsub")) proc_chsub();
		else
		{
			System.out.println("Error: invalid argument");
			printArgList();
		}
	}

	public static void printArgList()
	{
		System.out.print("Command Line Argument Options:\n\n");
		System.out.println("\"bitcrush\" : Bit Crush FX");
		System.out.println("\"reverse\" : Reverse Audio FX");
		System.out.println("\"chswap\" : Channel Swap Audio FX");
		System.out.println("\"chsub\" : Channel Subtract Audio FX");
	}

	public static void proc_bitcrush()
	{
		Scanner stdin = new Scanner(System.in);

		String inputDir = "";
		String outputDir = "";
		int bitcrush = 0;

		System.out.print("Enter input file directory: ");
		inputDir = stdin.nextLine();

		System.out.print("Enter output file directory: ");
		outputDir = stdin.nextLine();

		System.out.print("Enter bitcrush level (integer): ");
		userCmd = stdin.nextLine();

		try
		{
			bitcrush = Integer.parseInt(userCmd);
		}
		catch(Exception e)
		{
			stdin.close();
			System.out.println("Error: invalid value entered");
			return;
		}

		stdin.close();
		stdin = null;

		audioobj = new AudioBitCrush(inputDir, outputDir);

		if(!audioobj.initialize())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		if(!((AudioBitCrush) audioobj).setCutoff(bitcrush))
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Started...");

		if(!audioobj.runDSP())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Finished.");
		audioobj.deinitialize();
	}

	public static void proc_reverse()
	{
		Scanner stdin = new Scanner(System.in);
		String inputDir = "";
		String outputDir = "";

		System.out.print("Enter input file directory: ");
		inputDir = stdin.nextLine();

		System.out.print("Enter output file directory: ");
		outputDir = stdin.nextLine();

		stdin.close();
		stdin = null;

		audioobj = new AudioReverse(inputDir, outputDir);

		if(!audioobj.initialize())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Started...");

		if(!audioobj.runDSP())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Finished.");
		audioobj.deinitialize();
	}

	public static void proc_chswap()
	{
		Scanner stdin = new Scanner(System.in);
		String inputDir = "";
		String outputDir = "";

		System.out.print("Enter input file directory: ");
		inputDir = stdin.nextLine();

		System.out.print("Enter output file directory: ");
		outputDir = stdin.nextLine();

		stdin.close();
		stdin = null;

		audioobj = new AudioChannelSwap(inputDir, outputDir);

		if(!audioobj.initialize())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Started...");

		if(!audioobj.runDSP())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Finished.");
		audioobj.deinitialize();
	}

	public static void proc_chsub()
	{
		Scanner stdin = new Scanner(System.in);
		String inputDir = "";
		String outputDir = "";

		System.out.print("Enter input file directory: ");
		inputDir = stdin.nextLine();

		System.out.print("Enter output file directory: ");
		outputDir = stdin.nextLine();

		stdin.close();
		stdin = null;

		audioobj = new AudioChannelSubtract(inputDir, outputDir);

		if(!audioobj.initialize())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Started...");

		if(!audioobj.runDSP())
		{
			System.out.println(audioobj.getLastErrorMessage());
			audioobj.deinitialize();
			return;
		}

		System.out.println("DSP Finished.");
		audioobj.deinitialize();
	}
}

