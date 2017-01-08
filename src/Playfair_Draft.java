import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


public class Playfair_Draft {
	
	public static final String ENCRYPT_IN = "plaintext.txt";
	public static final String DECRYPT_IN = "ciphertext.txt";
	public static final String ENCRYPT_OUT = "out1.txt";
	public static final String DECRYPT_OUT = "out2.txt";
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		boolean encrypting;

		// Determine mode from user (encrypting or decrypting)
		while (true) {
			System.out.print("Would you like to \"encrypt\" or \"decrypt\"?: ");
			String input = scanner.nextLine().toLowerCase();
			
			if (input.equals("encrypt")) {
				encrypting = true;
				break;
			} else if (input.equals("decrypt")) {
				encrypting = false;
				break;
			} else {
				System.out.println("Invalid input.");
			}
		}
		
		// Determine key from user
		System.out.print("Enter the key: ");
		String key = scanner.nextLine();
		
		// Output Playfair matrix
		char[][] matrix = getMatrix(key);
		printMatrix(matrix);
		
		// Load data from file and Encrypt / Decrypt
		String data, output;
		PrintWriter printWriter;
		if (encrypting) {
			try {
				data = getDataString(ENCRYPT_IN);
				System.out.println("data:" + data);
				output = encrypt(key, data);
				printWriter = new PrintWriter(ENCRYPT_OUT);
				printWriter.write(output);
				printWriter.close();
				System.out.println(output);
			} catch (FileNotFoundException e) {
				System.out.println("Output file for encryption not found.");
				e.printStackTrace();
			}
		} else {
			try {
				data = getDataString(DECRYPT_IN);
				output = decrypt(key, data);
				printWriter = new PrintWriter(DECRYPT_OUT);
				printWriter.write(output);
				printWriter.close();
			} catch (FileNotFoundException e) {
				System.out.println("Output file for decryption not found.");
				e.printStackTrace();
			}
		}
		
		// End program
		System.out.println("Success.");
		scanner.close();
	}
	
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
	
	public static void printMatrix(char[][] matrix) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.print(matrix[i][j] + "  ");
			}
			System.out.println();
		}
	}
	
	public static String getDataString(String filename) throws FileNotFoundException {
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
	}
	
	public static String encrypt(String plaintext, String key) {
		char[][] matrix = getMatrix(key);
		String result = "";
		// Encryption is done two characters at a time
		int i = 0;
		while (i < plaintext.length()) {
			char cur = plaintext.charAt(i);
			if (i + 1 >= plaintext.length()) {
				// There is no next character.
				// Add 'x' padding
				result += performShift(cur, 'x', matrix);
				break;
			} else if (cur == plaintext.charAt(i+1)) {
				// Next character is a repeat.
				// Add 'x' padding
				result += performShift(cur, 'x', matrix);
				// Advance only by one (we only encrypted one plaintext character)
				i += 1;
			} else {
				// Characters do not match
				// Perform the shift
				result += performShift(cur, plaintext.charAt(i + 1), matrix);
				// We encrypted two characters
				i += 2;
			}
		}
		
		return result;
	}
	
	public static String decrypt(String ciphertext, String key) {
		
		return null;
	}
	
	/**
	 * getCharPos
	 * Return an integer array with row and column of character in array, or null if not found
	 * @param c
	 * @param matrix
	 * @return
	 */
	public static int[] getCharPos(char c, char[][] matrix) {
		if (c == 'j')
			c = 'i';
		
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (c == matrix[i][j]) {
					return new int[] {i, j};
				}
			}
		}
		// Not found:
		return null;
	}
	
	public static String shiftDown(char c1, char c2, char[][] matrix) {
		String result = "";
		/*char c1_pos = getCharPos(c1, matrix);
		char c2_pos = getCharPos(c2, matrix);
		result += matrix[c1_pos[0]][(c1_pos[1] + 1) % 5];
		result += matrix[c2_pos[0]][(c2_pos[1] + 1) % 5];*/
		return result;
	}
	
	public static String performShift(char c1, char c2, char[][] matrix) {
		int[] c1_pos = getCharPos(c1, matrix);
		int[] c2_pos = getCharPos(c2, matrix);
		String result = "";
		
		if (c1_pos[0] == c2_pos[0]) {
			// Same row:
			// Shift each one to the right in their row
			
		} else if (c1_pos[1] == c1_pos[1]) {
			// Same column:
			// Shift each one down one in their column
			result += matrix[(c1_pos[0] + 1) % 5][c1_pos[1]];
			result += matrix[(c2_pos[0] + 1) % 5][c2_pos[1]];
		} else {
			// Different column, different row:
			// Replace with letter in same row, but in the column of the other
			result += matrix[c1_pos[0]][c2_pos[1]]; // Order matters: this is the first character
			result += matrix[c2_pos[0]][c1_pos[1]]; // Second char
		}
		
		return result;
	}
}