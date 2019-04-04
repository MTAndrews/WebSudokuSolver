import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Utility class with methods to retrieve and display HTML source code.
 */
public class HTMLScraper 
{	
	/**
	 * Retrieves the source code from the given web address and prints it to
	 * the console.
	 * 
	 * @param address - the given address
	 */
	public static void printSourceCode(String address)
	{
		try
		{
			BufferedReader in = getSourceCode(address);
			
			// display each line to the console
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				System.out.println(inputLine);
			}
			in.close();	
		}
		catch (IOException e)
		{
			System.out.println("Connection Error: " + e);
		}
	}
	
	/**
	 * Returns a BufferedReader for the source code at the given web address.
	 * 
	 * @param address - the given address
	 * @return a BufferedReader for the source code
	 * @throws IOException if a connection or URL error occurs
	 */
	public static BufferedReader getSourceCode(String address) throws IOException
	{
		URL url = new URL(address); 
		return new BufferedReader(new InputStreamReader(url.openStream()));
	}
}
