package simulator;

public class Cache {
	private int totalSlots;
	private int byteSize;
	private Slot[] slots;
	private Console c;


	public Cache(int byteSize, int totalSlots, short[] mainMemory){

		this.totalSlots = totalSlots;
		this.byteSize = byteSize;

		slots = new Slot[totalSlots]; //create an array of slots

		for(short i = 0; i < totalSlots; i++){ //for every slot and instantiate
			slots[i] = new Slot(byteSize); 
		}
	}




	/**
	 * Method takes in the memory address which will be read. That 
	 * address is used to find the tag and slot. The tautology 
	 * of its valid, tag, and dirty bytes will determine whether the
	 * slot will be written back to memory and if it is a hit or miss
	 * The hit/miss string is returned
	 * @param address
	 * @return
	 */
	public String read(short address){

		short addressTag = findTag(address);
		short addressSlot = findSlot(address);
		
		//Valid bit is true
		if(slots[addressSlot].isValid()){
			
			//Tag matches
			if(addressTag == slots[addressSlot].getTag()){
				
				return stringResult(address, addressSlot, "Hit");
			
				
			//Tag doesn't match
			} else{
				
				//Dirty bit is true
				if(slots[addressSlot].isDirty()){
					
					//Copy block from cache into main memory
					writeToMemory(addressSlot);
					
					//Copy block with correct tag into cache
					writeToCache(address, addressSlot);
					
					//Change the tag
					slots[addressSlot].setTag(addressTag);
	
					return stringResult(address, addressSlot, "Miss");
				
				
				//Dirty bit is false
				} else{
					
					//copy block with correct tag into cache
					writeToCache(address, addressSlot);
					
					//Change the tag
					slots[addressSlot].setTag(addressTag);
	
					return stringResult(address, addressSlot, "Miss");
				}
			}
			
			
			//Valid bit is false... cache block is empty
		} else{
			
			//copy block to cache
			writeToCache(address, addressSlot);
			
			//set valid and tag
			slots[addressSlot].setValid(true);
			slots[addressSlot].setTag(addressTag);
			
			return stringResult(address, addressSlot, "Miss");
		}
		
	}



	/**
	 * This method takes in the memory address and the data which is to
	 * be written to the cache. It uses that info to write the data into
	 * the cache in the correct block and either writes back to MM and/or
	 * stores the data in the cache
	 * @param address
	 * @param data
	 * @return
	 */
	public String write(short address, short data){

		short addressTag = findTag(address);
		short addSlot = findSlot(address);
		short offset = findOffset(address);

		
		//Valid bit is true
		if(slots[addSlot].isValid()){

			//Tag matches
			if(addressTag == slots[addSlot].getTag()){           
				
				//Copy the data and then set block to dirty
				slots[addSlot].setBlockElem(offset, data);
				slots[addSlot].setDirty(true);
				
				return stringResult(address, addSlot, "Hit");

				
				
			//Tag doesn't match
			} else{
				
	
				//Dirty bit is true
				if(slots[addSlot].isDirty()){

					//Copy block from cache into main memory
					writeToMemory(addSlot);

					//Copy block with correct tag into cache
					writeToCache(address, addSlot);

					
					//Set the tag and change the data in the block
					slots[addSlot].setTag(addressTag);
					slots[addSlot].setBlockElem(offset, data);
					slots[addSlot].setDirty(true); 	//since we just added data

					
					return stringResult(address, addSlot, "Miss");


				//Dirty bit is false
				} else{
					
					//copy block with correct tag into cache
					writeToCache(address, addSlot);
					
					//Set the tag and change the data in the block
					slots[addSlot].setTag(addressTag);
					slots[addSlot].setBlockElem(offset, data);
					slots[addSlot].setDirty(true); 	//since we just added data

					return stringResult(address, addSlot, "Miss");
				}
			}


		//Valid bit is false... cache block is empty
		} else{
			
			writeToCache(address, addSlot);
			slots[addSlot].setBlockElem(offset, data);

			slots[addSlot].setValid(true);
			slots[addSlot].setTag(addressTag);
			slots[addSlot].setDirty(true);

			return stringResult(address, addSlot, "Miss");
		}
		
	}



	public void display(){
		System.out.println("Slot  Valid   Tag        Data");
		for(short i = 0; i < totalSlots; i++){
			System.out.print(Integer.toHexString(i) + "       ");
			System.out.println(slots[i].slotToString());

		}
	}


	private short findTag(short address){

		short tag = (short) ((address & 0xF00) >>> 8);
				return tag;
	}

	
	private short findSlot(short address){

		short slot = (short) ((address & 0xF0) >>> 4);
				return slot;
	}

	
	private short findOffset(short address){

		short offset = (short) (address & 0xF);
		return offset;
	}
	

	/**
	 * Method finds the first memory address for the tag/slot
	 * that is currently in cache. It then writes what is in 
	 * that block to memory starting at that address
	 * @param addSlot
	 */
	private void writeToMemory(short addSlot){
		
		//When writing to memory we are looking for address [tagAtTheMoment][slot][0]

		//Find beginning block address in main memory
		//Use the tag that is already in the cache
		short memBegBlockAdd = (short) ((slots[addSlot].getTag() << 8) + (addSlot << 4));
	
		System.arraycopy(slots[addSlot].getBlock(), 0, c.mainMemory, memBegBlockAdd, byteSize);
		
		
	}

	
	/**
	 * Method finds the first memory address for the block
	 * and then copies it to the slot in cache
	 * @param address
	 * @param addSlot
	 */
	private void writeToCache(short address, short addSlot){
		//Find beginning block address in main memory
		short memAddress = (short) (address & 0xFF0);
		
		//Copy the block from main memory into the cache
		for(short i = 0; i < byteSize; i++){
			
			slots[addSlot].setBlockElem(i, c.mainMemory[memAddress]);
			memAddress++;
		}
		
		//The new block has been put in cache so it is no longer dirty
		slots[addSlot].setDirty(false);  
		
	}
	

	/**
	 * Concatenates and returns a string which tells user the 
	 * data at that address and whether it was a hit or a miss
	 * @param address
	 * @param addSlot
	 * @param result
	 * @return
	 */
	private String stringResult(short address, short addSlot, String result){
		
		return "At that byte there is the value "
				+ Integer.toHexString(slots[addSlot].getBlockElem(findOffset(address)))
				+ " (Cache " + result + ")";
	}



}
