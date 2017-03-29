package simulator;

public class Slot {
	
	private boolean valid;
	private boolean dirty;
	private int tag;
	private short block[];
	
	public Slot(int byteSize){
		valid = false;
		dirty = false;
		tag = 0;
		
		//Instantiate each element in block[] to 0
		this.block = new short[byteSize];
		for(int i = 0; i < block.length; i++){
			block[i] = 0;
		}
	}


	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	
	
	/**
	 * Returns the whole block array
	 */
	public short[] getBlock() {
		return block;
	}
	
	/**
	 * Takes in index and returns data in block at that index
	 */
	public short getBlockElem(int i) {
		return block[i];
	}

	/**
	 * Takes in index and data to set an element in the block array
	 */
	public void setBlockElem(int i, int data) {
		this.block[i] = (short) data;
	}


	/**
	 * Method builds the String of the slot, working in order of 
	 * the valid bit, tag, and then the complete block, and then 
	 * returns that string.
	 * @return
	 */
	public String slotToString(){
		
		String slotString = "";
		
		//Give numeric value for boolean Valid bit
		if(valid == true){
			slotString += 1;
		} else{
			slotString += 0;
		}
		
		slotString += "      " + tag + "         ";
		
		//for every element in block, print to left of formatting 4 spaces
		for(int i = 0; i < block.length; i++){
			slotString += String.format("%-4s", Integer.toHexString(block[i]));
		}
		return slotString;
	}
	


}
