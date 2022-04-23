package di.uniba.it.wikioie.preprocessing;

import java.io.File;

/**
 * PreFile class wraps a file.
 *
 * @author angelica
 */
public class PreFile {
	
	private File file;
	private boolean poison;
	private int id;

	/**
	 * Wraps a file with its corresponding id.
	 * @param file
	 * @param id
	 */
	public PreFile(File file, int id) {
		this.file = file;
		poison = false;
		this.id = id;
	}

	/**
	 * Creates a poison file. When a thread receives a poison file, it stops.
	 */
	public PreFile() {
		file = new File("poison");
		poison = true;
	}
	
	boolean isPoison() {
		return poison;
	}
	
	File getFile() {
		return file;
	}
	
	int getId() {
		return id;
	}

}
