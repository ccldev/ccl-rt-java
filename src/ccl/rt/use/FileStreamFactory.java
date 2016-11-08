package ccl.rt.use;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ccl.rt.vm.Factory;

public class FileStreamFactory implements Factory<InputStream> {

	private File f;

	public FileStreamFactory(File file){
		this.f = file;
	}
	
	@Override
	public InputStream make() throws FileNotFoundException {
		return new FileInputStream(f);
	}

}
