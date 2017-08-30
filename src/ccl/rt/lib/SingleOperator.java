package ccl.rt.lib;

import ccl.rt.Value;
import ccl.rt.vm.IVM;

public interface SingleOperator {

    Value op(IVM vm, Value a);

}
