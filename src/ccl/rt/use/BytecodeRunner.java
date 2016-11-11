package ccl.rt.use;

import java.io.IOException;
import java.io.InputStream;

import ccl.rt.Expression;
import ccl.rt.Special;
import ccl.rt.Value;
import ccl.rt.vm.IVM;
import ccl.rt.vm.Runner;

public class BytecodeRunner implements Runner{

	private int phase = 0;
	
	//Globals
	private long[] nums;
	
	//Init
	private int numIndex = -1;
	
	
	@Override
	public Value execute(InputStream cclCode, IVM vm) {
		int val = 0;
		try {
			while((val = cclCode.read()) >= 0){
				System.out.println(phase);
				if(phase == 0){
					//init number array
					nums = new long[(int) readSmallNum(val, cclCode)];
					phase = 1;
				}else if(phase == 1){
					//read numbers
					numIndex++;
					if(numIndex >= nums.length){
						phase = 2;
						continue;
					}else{
						nums[numIndex] = readNum(val, cclCode);
					}
				}else{
					throw new RuntimeException("Unexpected phase " + phase);
				}
			}
		} catch (IOException e) {
			new RuntimeException(e).printStackTrace();
		}
		return new Expression(Special.UNDEFINED);
	}

	/**
	 * Reads a number.
	 * A char (length) specifies the length of the "number length". Sounds confusing, but means:
	 * If input is 3, a digit count of 100 to 999 is possible. If it is 4, 1000 to 9999 digits are possible.
	 * And so on, up to 255.<br>
	 * After that, a buffer is created and filled with the digits.
	 * Finally, the buffer is converted to a String and this is parsed as a Long value.
	 * @param length the length char.
	 * @param stream a stream to read digits count and buffer content from.
	 * @return the parsed long.
	 * @throws IOException on read failure.
	 */
	private long readNum(int length, InputStream stream) throws IOException {
		char[] c = new char[length];
		for(int i = 0; i < length; i++){
			c[i] = (char) stream.read();
		}
		char[] buffer = new char[Integer.parseInt(new String(c))];
		for(int i = 0; i < buffer.length; i++){
			buffer[i] = (char) stream.read();
		}
		return Long.parseLong(new String(buffer));
	}
	
	/**
	 * Reads a number with a maximum digit count of 255.
	 * The digit count is specified by a char, followed by
	 * a char sequence of the given length. The sequence
	 * contains Digits, that are parsed as a Long value.
	 * @param length the digit count (0 to 255 normally)
	 * @param stream an input stream to read the digits from.
	 * @return the parsed long value.
	 * @throws IOException on read failure.
	 */
	private long readSmallNum(int length, InputStream stream) throws IOException{
		char[] c = new char[length];
		for(int i = 0; i < length; i++){
			c[i] = (char) stream.read();
		}
		return Long.parseLong(new String(c));
	}

	@Override
	public Runner create() {
		return new BytecodeRunner();
	}

}
