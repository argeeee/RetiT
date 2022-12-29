package fs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Consumer {

	public static void main(String[] args) {
		BufferedReader in = null;
		int c;
		char read;
		
		if (args.length < 1 || args.length > 2) { //Invocation error
			System.out.println("usage: java Consumer <params> [filename]");
			System.out.println("OR");
			System.out.println("usage: java Consumer <params> < [filename]");
			System.exit(0);
		}
		else if (args.length == 1) { // only params
			in = new BufferedReader(new InputStreamReader(System.in));		
		}
		else if (args.length == 2) {// params and filename
			try {
				in = new BufferedReader(new FileReader(args[1]));
				System.out.println("Reading file...");
			}catch(FileNotFoundException e){
				System.out.println("File not found");
				System.exit(1);
			}
		}

		try {
			// Filter by character
			while ((c = in.read()) > 0) {
				read = (char)c;
				
				// write here your logic

				// end logic

				System.out.print(read);
			}
		} catch(IOException ex){
			System.out.println("Errore di input");
			System.exit(2);
		}
	}
		
}
