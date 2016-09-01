/* File: SA_lingpipe_czech.java
 * Description: Sentiment analysis module for MixedEmotions framework using lingpipe
 *              used for analysing czech sentences
 * Author: xorman00 <xorman00@stud.fit.vutbr.cz>
 */

package cz.vutbr.mefw.plugins;
// IMPORTS ////////////////////////////////////////////////
import cz.vutbr.mefw.*;
import java.io.*;
import java.nio.file.Files;
import java.lang.Exception;
// Config
import java.util.Properties;
// Logging
import java.util.logging.Logger;
import java.util.regex.*;
import java.util.*;
// Own custom classes
// Loaders of datasets
import cz.vutbr.mefw.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import cz.vutbr.mefw.plugins.SA.loaders.*;
import cz.vutbr.mefw.plugins.*;

// Lingpipe
import com.aliasi.classify.*;
import com.aliasi.lm.*;

// MAIN CLASS ////////////////////////////////////////////////
public class SA_lingpipe_czech extends SA_lingpipe
{
	public SA_lingpipe_czech()
	{
		super.processor_name = "lingpipe_czech";
	}

	public SA_lingpipe_czech(Config config)

	{
		super.path_to_resources=config.get("resoucesPath")+"/but_sentiment/";
		super.processor_name = "lingpipe_czech";
	}

	// Main for testing purposes
	public static void main(String[] args)
	{
		SA_lingpipe_czech tool = new SA_lingpipe_czech();
		System.out.println("LOAD");
		try
		{
			tool.load();
			System.out.println(tool.process("Zle."));
			System.out.println(tool.process("Funguje."));
			tool.unload();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("THANKS");
	}
}