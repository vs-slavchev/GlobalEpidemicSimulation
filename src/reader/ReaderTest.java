package reader;

/**
 * Owner: Nikolay
 */

public class ReaderTest
{
  public static void main(String[] args)
  {
    FileReader r = new FileReader();
    r.openFile();
    r.readFile();
    r.closeFile();
  }
}
