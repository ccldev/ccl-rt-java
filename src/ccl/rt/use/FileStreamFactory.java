package ccl.rt.use;

import cpa.subos.io.IO;
import cpa.subos.io.IOBase;
import io.github.coalangsoft.lib.data.Func;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileStreamFactory implements Func<Void, IOBase<?>> {

	private File f;

	public FileStreamFactory(File file){
		this.f = file;
	}
	
	@Override
	public IOBase<?> call(Void v) {
		return IO.file(f);
	}

	@Override
	public String toString() {
		return "FileStreamFactory [f=" + f + "]";
	}

}
