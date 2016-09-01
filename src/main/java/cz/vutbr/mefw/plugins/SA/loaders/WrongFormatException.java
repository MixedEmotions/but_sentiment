package cz.vutbr.mefw.plugins.SA.loaders;

public class WrongFormatException extends java.io.IOException
{
  public final String filename;
  public final int line;

  public WrongFormatException(String filename, int line)
  {
    this.filename = filename;
    this.line = line;
  }
}
