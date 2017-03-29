package simulator;

import java.util.Scanner;

public class Console {
	
	static short mainMemory[];

	public static void main(String[] args) {

		Console prgm = new Console();
		prgm.run();
	}


	private void run(){
		
		//Create the "Main Memory" as an array, and load values
		mainMemory = new short[2048];
		short mmVal = 0;

		for(short i = 0; i < mainMemory.length; i++){ 
			mainMemory[i] = mmVal; 
			mmVal++;
			
			//If the value of the next value is larger than 
			//0xFF then reset the mmVal back to 0
			if(mmVal > 0xFF){ 
				mmVal = 0; 
			}
			
		}

		

		Cache c = new Cache(16, 16, mainMemory); //16 bytes, 16 slots
				
		Scanner input = new Scanner(System.in);

		boolean quit = false;
		while(quit == false){
			System.out.println("Read (R), Write (W), or Display (D) Cache?");
			String in = input.nextLine();

			switch (in.toUpperCase()){
				case "R":
					System.out.println("What address would you like to read?");
					System.out.println(c.read(input.nextShort(16)));
					break;
				
				case "W":
					System.out.println("What address would you like to write to?");
					short address = input.nextShort(16);
					System.out.println("What data would you like to write "
							+ "at that address?");
					short data = input.nextShort(16);
					System.out.println(c.write(address, data));
					break;
				
				case "D":
					c.display();
					break;
				
				case "Q":
					input.close();
					quit = true;
					break;
				
				default:
					System.out.println("Invalid response. Try again.");
					break;
			}
			input.nextLine();
			System.out.println();
		}
	}
	
	
	public void setMainMem(int address, short data){
		mainMemory[address] = data;
	}
	

}
