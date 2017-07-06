package ccl.rt.use;

/**
 * Created by Matthias on 05.07.2017.
 */
public interface InstructionBytes {

    String[] BYTE_TO_STRING = new String[]{
            "__whiletrue","__while","__forarr",
            "__fornum","__whiletrue_nb","__while_nb",
            "__forarr_nb","__fornum_nb","__println",
            "__println_f","__println_c","__println_cf",
            "__setvar","__mkvar","__exit",
            "__throw","__throw_c","nnr",
            "nnr2","load","putI",
            "invoke","invoke1","store",
            "store1","putS","putA",
            "putM","get","duplicate",
            "pop","ret","reserve",
            "__undefined", "__mkvar_u", "__java"
    };

    byte __WHILETRUE = 0;
    byte __WHILE = 1;
    byte __FORARR = 2;
    byte __FORNUM = 3;
    byte __WHILETRUE_NB = 4;
    byte __WHILE_NB = 5;
    byte __FORARR_NB = 6;
    byte __FORNUM_NB = 7;
    byte __PRINTLN = 8;
    byte __PRINTLN_F = 9;
    byte __PRINTLN_C = 10;
    byte __PRINTLN_CF = 11;
    byte __SETVAR = 12;
    byte __MKVAR = 13;
    byte __EXIT = 14;
    byte __THROW = 15;
    byte __THROW_C = 16;
    byte NNR = 17;
    byte NNR2 = 18;
    byte LOAD = 19;
    byte PUTI = 20;
    byte INVOKE = 21;
    byte INVOKE1 = 22;
    byte STORE = 23;
    byte STORE1 = 24;
    byte PUTS = 25;
    byte PUTA = 26;
    byte PUTM = 27;
    byte GET = 28;
    byte DUPLICATE = 29;
    byte POP = 30;
    byte RET = 31;
    byte RESERVE = 32;
    byte __UNDEFINED = 33;
    byte __MKVAR_U = 34;
    byte __JAVA = 35;

}
