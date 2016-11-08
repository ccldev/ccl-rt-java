package ccl.rt.vm;

public interface Factory<T> {

	T make() throws Exception;

}
