package wackyTracky.clientbindings.java.model;

import java.io.File;
import java.io.FileWriter;

import net.minidev.json.JSONObject;

public class DataStore {

	public static final DataStore instance = new DataStore();

	public ListOfLists listOfLists = new ListOfLists();

	public void clear() {
		this.listOfLists = new ListOfLists();
	}

	private JSONObject getJson() {
		JSONObject me = new JSONObject();
		
		me.
		return me;
	}

	public void println() {
		this.listOfLists.println();
	}

	public void save() {
		JSONObject o = this.getJson();
		String out = o.toJSONString();

		File homedir = new File("/home/jread/");
		File wtDir = new File(homedir, ".wt");
		wtDir.mkdir();

		File outFile = new File(wtDir, "database.json");

		try {
			FileWriter writer = new FileWriter(outFile);
			writer.write(out);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
