import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * A utility class containing static methods used to solve Sudoku, also with a
 * main for use as a standalone application if desired.
 */
public class SudokuSolver 
{
	final static int BOARD_SIZE = 9;
	final static byte[] MOVES = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	final static byte EMPTY = 0; // represents an empty square
	
	
	/**
	 * Retrieves a file path from the user which contains Sudoku puzzle data, 
	 * and displays the puzzle solution to the console. 
	 * 
	 * The file is expected to contain 9 lines, each containing space-separated
	 * digits corresponding to the known numbers in that row of the Sudoku 
	 * puzzle, with 0 indicating an empty space.
	 * 
	 * @param args - args[0] : the file path
	 */
	public static void main(String[] args) 
	{
		byte[][] board = null;
		String filePath;
		boolean solutionFound;
		
		// if a file is provided as an arg, use it
		if (args.length > 0)
		{
			filePath = args[0];
		}
		else // prompt for a file
		{
			Scanner in = new Scanner(System.in);
			System.out.println("Enter a file path containing a sudoku puzzle: ");
			filePath = in.nextLine();
		}
	
		try
		{
			board = populateBoard(Paths.get(filePath));
		}
		catch (IOException e)
		{
			System.out.println("File error: " +e);
			System.exit(1);
		}
		
		solutionFound = findSolution(board);
		if (!solutionFound)
		{
			System.out.println("Error: Invalid Board State");
			System.exit(1);
		}
		else
		{
			displayBoard(board);
		}
	}
	
	
	/**
	 * Populates a Sudoku board with the provided Sudoku puzzle data.
	 * 
	 * @param puzzleRows - a list of Strings, each containing the moves for the
	 * 		corresponding row
	 * @return 2D byte array representing the board
	 */
	public static byte[][] populateBoard(List<String> puzzleRows)
	{
		byte[][] board = new byte[BOARD_SIZE][BOARD_SIZE];
		Scanner rowData;
		
		for (int row = 0; row < BOARD_SIZE && row < puzzleRows.size(); row++)
		{
			rowData = new Scanner(puzzleRows.get(row));
			for (int col = 0; col < BOARD_SIZE && rowData.hasNextInt(); col++)
			{
				board[row][col] = (byte)rowData.nextInt();
			}
		}
		
		return board;
	}
	
	
	/**
	 * Populates a Sudoku board with the puzzle data in the file at the given
	 * path. 
	 * 
	 * @param filePath - the given file path
	 * @return 2D byte array representing the board
	 * @throws IOException if there was an error accessing the file
	 */
	public static byte[][] populateBoard(Path filePath) throws IOException
	{
		List<String> lines = Files.readAllLines(filePath);
		return populateBoard(lines);
	}
	
	
	/**
	 * Attempts to find a solution to the given Sudoku board puzzle.
	 * Postcondition : the given board contains the solution if one was found.
	 * 
	 * @param board - 2D byte array representing the board
	 * @return true if a solution was found; otherwise false
	 */
	public static boolean findSolution(byte[][] board)
	{
		if (!isValidBoard(board))
		{
			return false;
		}
		else
		{
			return exploreBoard(board);
		}
	}
	
	
	/**
	 * Prints out a 9x9 visual representation of the Sudoku board
	 * 
	 * @param board - 2D byte array representing the board
	 */
	public static void displayBoard(byte[][] board)
	{
		for (int row = 0; row < BOARD_SIZE; row++)
		{
			for (int col = 0; col < BOARD_SIZE; col++)
			{
				System.out.print(board[row][col] + " ");
			}
			System.out.println();
		}
	}
	
	
	/**
	 * Explores the Sudoku board using recursive backtracking in an attempt to
	 * find a solution. Exploration entails finding an empty square, placing a
	 * valid move in it, and recursively calling the method. 
	 * 
	 * @param board - 2D byte array representing the board
	 * @return true if a solution was found; otherwise false
	 */
	private static boolean exploreBoard(byte[][] board)
	{
		boolean solutionFound = false;
		
		boolean emptySquareFound = false;
		int emptyRow = -1;
		int emptyCol = -1;
		
		// search for an empty square to place a move in
		for (int row = 0; row < BOARD_SIZE && !emptySquareFound; row++)
		{
			for (int col = 0; col < BOARD_SIZE && !emptySquareFound; col++)
			{
				if (board[row][col] == EMPTY)
				{
					emptySquareFound = true;
					emptyRow = row;
					emptyCol = col;
				}
			}
		}
		
		if (!emptySquareFound)
		{
			solutionFound = true;
		}
		else // further explore the board for each valid move
		{
			for (int i = 0; i < MOVES.length && !solutionFound; i++)
			{
				if (isLegalMove(MOVES[i], emptyRow, emptyCol, board))
				{
					board[emptyRow][emptyCol] = MOVES[i];
					solutionFound = exploreBoard(board);
				}
			}
			// if we were not successful with any move, return the square to empty
			if (!solutionFound)
			{
				board[emptyRow][emptyCol] = EMPTY;
			}
		}
		
		return solutionFound;
	}
	
	
	/**
	 * Tests if the board contains any illegal moves.
	 * @param board - 2D byte array representing the board
	 * @return true if no illegal or invalid moves are found; otherwise false
	 */
	private static boolean isValidBoard(byte[][] board)
	{
		byte move;
		for (int row = 0; row < BOARD_SIZE; row++)
		{
			for (int col = 0; col < BOARD_SIZE; col++)
			{
				move = board[row][col];
				
				// each space much be either empty or contain a legal move
				if (move != EMPTY && !isLegalMove(move, row, col, board))
				{
					return false;
				}
			}
		}
		
		return true;
	}	
	
	
	/**
	 * Tests if the given move in the given row and column of the board is
	 * legal as defined by the rules of Sudoku.
	 * 
	 * @param move - the given move
	 * @param row - the given row
	 * @param col - the given column
	 * @param board - 2D byte array representing the board
	 * @return true if the move is legal; otherwise false
	 */
	private static boolean isLegalMove(byte move, int row, int col, byte[][] board)
	{
		if (!isValidMove(move))
		{
			return false;
		}
		
		// check the row for a duplicate move
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			if (board[row][i] == move && i != col)
			{
				return false;
			}
		}
		// check the column for a duplicate move
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			if (board[i][col] == move && i != row)
			{
				return false;
			}
		}
		// check the 3x3 subsection for a duplicate move
		for (int i = 3*(row/3); i < 3*((row/3)+1); i++)
		{
			for (int j = 3*(col/3); j < 3*((col/3)+1); j++)
			{
				if (board[i][j] == move && ! (i == row && j == col))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	/**
	 * Tests if the specified move is valid for Sudoku (numeric 1-9).
	 * 
	 * @param move - the given move
	 * @return true if the move is valid; otherwise false
	 */
	private static boolean isValidMove(byte move)
	{
		for (byte option: MOVES)
		{
			if (option == move)
			{
				return true;
			}
		}
		return false;
	}
}