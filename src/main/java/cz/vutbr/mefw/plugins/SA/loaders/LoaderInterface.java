package cz.vutbr.mefw.plugins.SA.loaders;

import cz.vutbr.mefw.plugins.SA.src.*;
import java.util.List;
import java.io.*;
import java.lang.Exception.*;

// Interface for every loader
public interface LoaderInterface
{
	// Load sentiment data from file
	public List<Sentiment> load(String file) throws IOException;

	// Name of loader, used in config files to determine suitable loader for that particular file
	// NOT USED
	// public String name();
}
