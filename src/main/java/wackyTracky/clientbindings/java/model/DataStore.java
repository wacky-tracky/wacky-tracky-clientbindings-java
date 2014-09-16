package wackyTracky.clientbindings.java.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class DataStore {

	private static File getDbFile() {
		File homedir = new File(System.getProperty("user.home"));
		File wtDir = new File(homedir, ".wt");
		wtDir.mkdir();

		File dbFile = new File(wtDir, "database.json");

		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dbFile;
	}

	public static DataStore load() {
		File dbFile = getDbFile();
		FileReader reader;
		try {
			reader = new FileReader(dbFile);

			return new Gson().fromJson(reader, DataStore.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ListOfLists listOfLists = new ListOfLists();

	public DataStore(String title) {
		System.out.println("Creating new DataStore: " + title);
	}

	public void clear() {
		this.listOfLists = new ListOfLists();
	}

	/**
	private JsonObject getJson() {

		JSONObject me = new JSONObject();
		
		return me;
	}
	*/

	public void println() {
		this.listOfLists.println();
	}

	public void save() {
		JsonObject o = (JsonObject) new Gson().toJsonTree(this);
		o.addProperty("version", 0);

		File dbFile = DataStore.getDbFile();
		FileWriter writer;

		try {
			writer = new FileWriter(dbFile);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(o, writer);

			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
