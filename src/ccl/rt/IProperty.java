package ccl.rt;

import ccl.rt.Value;

public interface IProperty extends Value{

    String getName();
    void setValue(Value n);
    Value getHolder();

}
