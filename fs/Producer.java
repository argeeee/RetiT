package fs;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Producer {
	public static void main(String[] args) {		
		BufferedReader in = null;
		String inputl;
		
		if (args.length != 1){
			System.out.println("usage: java Producer <inputFilename>");
			System.exit(0);
		}

		System.out.println("Insert lines on file till EOF");
		in = new BufferedReader(new InputStreamReader(System.in));
		
		FileWriter fout;
		try {
			fout = new FileWriter(args[0]);
			while((inputl = in.readLine())!=null) {	//till EOF (CTRL+D)
				fout.write(inputl+"\n", 0, inputl.length()+1);
			}
			System.out.println("Found EOF");
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

