package ccl.rt.use;

import java.util.HashMap;

/**
 * Created by Matthias on 05.07.2017.
 */
public class InstructionUtil {

    private static HashMap<String,Byte> map = new HashMap<>();

    static {
        for(byte i = 0; i < InstructionBytes.BYTE_TO_STRING.length; i++){
            map.put(InstructionBytes.BYTE_TO_STRING[i], i);
        }
    }

    public static byte get(String instr){
        return map.get(instr);
    }
    public static String get(byte instr){
        return InstructionBytes.BYTE_TO_STRING[instr];
    }

}
