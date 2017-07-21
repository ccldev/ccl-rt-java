package ccl.rt.v6;

import ccl.rt.Value;
import ccl.rt.vm.IVM;

public interface StandardOperator {

    Value op(IVM vm, Value a, Value b);

}
