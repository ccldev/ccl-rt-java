package ccl.rt.use;

import io.github.coalangsoft.lib.data.Func;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileStreamFactory implements Func<Void, InputStream> {

	private File f;

	public FileStreamFactory(File file){
		this.f = file;
	}
	
	@Override
	public InputStream call(Void v) {
		try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "FileStreamFactory [f=" + f + "]";
	}

}
