/*
 * Audio FX Collection for Java Runtime
 * Version 1.0
 *
 * Author: Rafael Sabe
 * Email: rafaelmsabe@gmail.com
 */

import java.io.*;

public class FileManager
{
	public static int fileExists(String fileDir)
	{
		File _file = null;
		boolean exists = false;

		try
		{
			_file = new File(fileDir);
			exists = _file.exists();
		}
		catch(Exception e)
		{
			return -1;
		}

		if(exists) return 1;

		return 0;
	}

	public static boolean fileDelete(String fileDir)
	{
		File _file = null;

		try
		{
			_file = new File(fileDir);
			_file.delete();
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}

	public static boolean fileCreate(String fileDir)
	{
		File _file = null;
		int n_ret = 0;

		n_ret = fileExists(fileDir);
		if(n_ret < 0) return false;

		if(n_ret == 1)
			if(!fileDelete(fileDir))
				return false;

		try
		{
			_file = new File(fileDir);
			_file.createNewFile();
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}

	public static long fileGetSize(String fileDir)
	{
		File _file = null;
		long _size = 0L;

		try
		{
			_file = new File(fileDir);
			_size = _file.length();
		}
		catch(Exception e)
		{
			return -1L;
		}

		return _size;
	}
}

