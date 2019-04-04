import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

/** 
 * Application which displays the solution to a Sudoku puzzle from
 * WebSudoku.com. 
 * 
 * To guarantee that the desired puzzle is solved, the user should provide an
 * address which contains the specific level and set_id of the puzzle.
 * 
 * (Example: https://www.websudoku.com/?level=2&set_id=12345)
 * 
 * @author MTAndrews
 */
public class WebSudokuSolver 
{
	/**
	 * @param args - args[0] : the web address of the puzzle
	 */
	public static void main(String[] args)
	{
		String address;
		
		if (args.length > 0)
		{
			address = args[0];
		}
		// if no address was provided, prompt the user for input
		else
		{
			Scanner in = new Scanner(System.in); 
			System.out.println("Please provide a WebSudoku URL: ");
			address = in.nextLine();
			in.close();
		}
		
		// this is necessary in order to view the html table in the frame
		// which contains the puzzle data
		address = address.replaceFirst("www", "show");
		
		try
		{
			byte[][] board = parseWebSudokuBoard(address);
			SudokuSolver.findSolution(board);
			SudokuSolver.displayBoard(board);
		}
		catch (IOException e)
		{
			System.out.println("Connection Error: " + e);
			System.exit(1);
		}
	}
	
	
	/**
	 * Retrieves the HTML source code from the given web address, parses the
	 * Sudoku table data, and populates a board.
	 * 
	 * @param address - the given web address
	 * @return 2D byte array representing the board
	 * @throws IOException if a connection or URL error occurred
	 */
	private static byte[][] parseWebSudokuBoard(String address) throws IOException
	{
		final String boardIdentifier = "<TABLE id=\"puzzle_grid\"";
		final String squareStartTag = "<TD";
		final String valueField = "VALUE=";
		
		byte[][] board = new byte[SudokuSolver.BOARD_SIZE][SudokuSolver.BOARD_SIZE];
		
		BufferedReader sourceCodeReader = HTMLScraper.getSourceCode(address);
		
		String nextLine;
		while ((nextLine = sourceCodeReader.readLine()) != null)
		{
			if (nextLine.startsWith(boardIdentifier)) // We found the board!
			{
				// the table is a single line containing all the board squares,
				// so split on the tag so we can search each for its value
				String[] boardSquares = nextLine.split(squareStartTag);
				
				// (ignore the first element as it's just table formatting)
				for (int i = 1; i < boardSquares.length; i++)
				{
					int valIndex = boardSquares[i].indexOf(valueField);
					byte move;
					
					// if no value field is found, it's an empty square
					if (valIndex == -1)
					{
						move = SudokuSolver.EMPTY;
					}
					else
					{
						char moveChar = boardSquares[i].charAt(valIndex + valueField.length()+1);
						move = (byte) Character.getNumericValue(moveChar);
					}
					
					// subtract 1 from i since we're not using the first element
					board[(i-1)/SudokuSolver.BOARD_SIZE][(i-1)%SudokuSolver.BOARD_SIZE] = move;
				}
			}
		}
		sourceCodeReader.close();	
		
		return board;
	}
}