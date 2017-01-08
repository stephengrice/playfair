import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Playfair Encryption / Decryption Program
 * Written for CSIS 4481 Cryptography and Data Security
 * Professor: Dr. Vincent Cicirello
 * 
 * @author Stephen Grice
 *
 */

public class Playfair {
	
	public static final String ENCRYPT_IN = "plaintext.txt";
	public static final String DECRYPT_IN = "ciphertext.txt";
	public static final String ENCRYPT_OUT = "out1.txt";
	public static final String DECRYPT_OUT = "out2.txt";
	private static char[][] matrix;
	
	/**
	 * getMatrix: return 2-dimensional array representing the Playfair matrix for given key
	 * @param key The key to use in creating the matrix
	 * @return 2-d array representing Playfair matrix
	 */
	public static char[][] getMatrix(String key) {
		char[][] matrix = new char[5][5];
		int length = 0;
		// First add the key to the matrix
		for (int i = 0; i < key.length(); i++) {
			char curChar = key.charAt(i);
			int firstOccurance = key.indexOf(curChar);
			// If the letter occurs more than once, only add it to the matrix once
			if (firstOccurance == i) {
				// TODO: make sure this is okay
				matrix[length / 5][length % 5] = curChar;
				length++;
			}
		}
		
		// Then add the rest of the alphabet
		char[] alphabet = {'a','b','c','d','e','f','g','h','i','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		for (int i = 0; i < alphabet.length; i++) {
			if (key.indexOf(alphabet[i]) == -1) {
				matrix[length / 5][length % 5] = alphabet[i];
				length++;
			}
		}
		
		return matrix;
	}
	
	/**
	 * printMatrix: Print out the Playfair matrix in a human-readable format
	 * @param matrix The Playfair matrix to be printed
	 */
	public static void printMatrix(char[][] matrix) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.print(matrix[i][j] + "  ");
			}
			System.out.println();
		}
	}
	
	/**
	 * getDataString: Get the plain/ciphertext data from file and return as a String object
	 * @param filename Name of file containing the data to be encrypted / decrypted
	 * @return String object with the data from file OR null if file not found
	 */
	public static String getDataString(String filename) {
		try {
			String out = "";
			Scanner scanner = new Scanner(new File(filename));
			while (scanner.hasNext()) {
				String raw_in = scanner.next().toLowerCase();
				for (int i = 0; i < raw_in.length(); i++) {
					char curChar = raw_in.charAt(i);
					if (Character.isAlphabetic(curChar)) {
						out += curChar;
					}
				}
			}
			scanner.close();
			return out;
		} catch (FileNotFoundException e) {
			System.out.println("Data file not found.");
			return null;
		}
	}
	
	/**
	 * encrypt: Convert given plaintext data to ciphertext
	 * @param data String representing plaintext
	 * @param matrix Playfair matrix for message key
	 * @return String representing ciphertext
	 */
	public static String encrypt(String data, char[][] matrix) {
		String result = "";
		String[] pairs = getPairs(data);
		
		for (String pair : pairs) {
			char left = pair.charAt(0);
			int[] left_pos = getPos(left, matrix);
			char right = pair.charAt(1);
			int[] right_pos = getPos(right, matrix);
			
			int left_x, left_y, right_x, right_y;
			if (left_pos[0] == right_pos[0]) {
				// Same row: Shift to the right (add one to column)
				left_x = left_pos[0];
				left_y = (left_pos[1] + 1) % 5;
				right_x = right_pos[0];
				right_y = (right_pos[1] + 1) % 5;
			} else if (left_pos[1] == right_pos[1]) {
				// Same column: shift down (add one to row)
				left_x = (left_pos[0] + 1) % 5;
				left_y = left_pos[1];
				right_x = (right_pos[0] + 1) % 5;
				right_y = right_pos[1];
			} else {
				// Rectangle method: Same row, column of other
				left_x = left_pos[0];
				left_y = right_pos[1];
				right_x = right_pos[0];
				right_y = left_pos[1];
			}
			result += matrix[left_x][left_y];
			result += matrix[right_x][right_y];
		}
		
		return result;
	}
	
	/**
	 * decrypt: Convert given ciphertext data to plaintext
	 * @param data String representing ciphertext
	 * @param matrix Playfair matrix for message key
	 * @return String representing plaintext
	 */
	public static String decrypt(String data, char[][] matrix) {
		String result = "";
		String[] pairs = getPairs(data);
		
		for (String pair : pairs) {
			char left = pair.charAt(0);
			int[] left_pos = getPos(left, matrix);
			char right = pair.charAt(1);
			int[] right_pos = getPos(right, matrix);
			
			int left_x, left_y, right_x, right_y;
			if (left_pos[0] == right_pos[0]) {
				// Same row: Shift to the left (subtract one from column)
				left_x = left_pos[0];
				left_y = (left_pos[1] - 1 + 5) % 5;
				right_x = right_pos[0];
				right_y = (right_pos[1] - 1 + 5) % 5;
			} else if (left_pos[1] == right_pos[1]) {
				// Same column: shift up (subtract one from row)
				left_x = (left_pos[0] - 1 + 5) % 5;
				left_y = left_pos[1];
				right_x = (right_pos[0] - 1 + 5) % 5;
				right_y = right_pos[1];
			} else {
				// Rectangle method: Same row, column of other
				left_x = left_pos[0];
				left_y = right_pos[1];
				right_x = right_pos[0];
				right_y = left_pos[1];
			}
			result += matrix[left_x][left_y];
			result += matrix[right_x][right_y];
		}
		
		return result;
	}
	
	/**
	 * getPairs: Get array of letters pairs, adding padding as 'x' character as necessary
	 * @param message The message
	 * @return Array of letter pairs with proper padding
	 */
	public static String[] getPairs(String message) {
		ArrayList<String> result = new ArrayList<String>();
		int i = 0;
		while (i < message.length()) {
			char c1 = message.charAt(i);
			char c2;
			
			if (i + 1 < message.length()) {
				c2 = message.charAt(i + 1);
			} else {
				// For the last character: prevent out of bounds exception
				c2 = 'x';
			}

			if (c1 == c2) {
				// Two characters in a row. Pad and advance one
				result.add(new String("" + c1 + 'x'));
				i++;
			} else {
				result.add(new String("" + c1 + c2));
				i += 2;
			}
		}
		
		return (String[]) result.toArray(new String[0]);
	}
	
	/**
	 * getCharPos: Get position of character in Playfair matrixReturn an integer array with row and column of character in array, or null if not found
	 * @param c Character to find in matrix
	 * @param matrix Playfair matrix
	 * @return int[] pos with pos[0] corresponding to row of c and pos[1] corresponding to column of c
	 */
	public static int[] getPos(char c, char[][] matrix) {
		// Substitute j for i
		if (c == 'j')
			c = 'i';
		// Traverse matrix
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (c == matrix[i][j]) {
					// Found it, no need to continue searching
					return new int[] {i, j};
				}
			}
		}
		// Not found:
		return null;
	}
	
	/**
	 * writeToFile: Write String data to a system file
	 * @param data The String to be written to the file
	 * @param file The name of the file to be written to
	 */
	public static void writeToFile(String data, String file) {
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.write(data);
			writer.close();
			System.out.println("Data written to " + file);
		} catch (FileNotFoundException e) {
			System.out.println("Output file not found.");
		}
	}
	
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		System.out.println("Playfair Cipher Solver - Stephen Grice");
		System.out.println("CSIS 4481 Cryptography and Data Security");
		
		Scanner scanner = new Scanner(System.in);
		// Determine key from user
		System.out.print("Enter the key: ");
		String key = scanner.nextLine();
		// Determine mode form user
		System.out.print("Would you like to \"encrypt\" or \"decrypt\"?: ");
		String input = scanner.nextLine().toLowerCase();
		
		// Output Playfair matrix
		System.out.println("Playfair Matrix:");
		matrix = getMatrix(key);
		printMatrix(matrix);
		
		String data, output;
		if (input.equals("encrypt")) {
			// Encrypt message
			data = getDataString(ENCRYPT_IN).toLowerCase();
			System.out.println("Data to encrypt: " + data);
			output = encrypt(data, matrix);
			System.out.println("Encrypted data: " + output);
			// Write output to file
			writeToFile(output, ENCRYPT_OUT);
		} else if (input.equals("decrypt")) {
			// Decrypt message
			data = getDataString(DECRYPT_IN).toLowerCase();
			System.out.println("Data to decrypt: " + data);
			output = decrypt(data, matrix);
			System.out.println("Decrypted data: " + output);
			// Write output to file
			writeToFile(output, DECRYPT_OUT);
		} else {
			System.out.println("Invalid input.");
		}
		
		scanner.close();
	}
}
