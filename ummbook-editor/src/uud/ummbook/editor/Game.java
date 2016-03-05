package uud.ummbook.editor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kopitubruk.util.json.JSONParser;
import org.kopitubruk.util.json.JSONUtil;

public class Game {
	
	public String file;
	public String original;
	public List<Scene> scenes;
	
	public Game() {
		this(null);
	}
	
	public Game(String filename) {
		scenes = new ArrayList<Scene>();
		if (filename == null) {
			original = "";
		} else {
			File file = new File(filename);
			try {
				byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
				String str = new String(encoded);
				original = str;
				fromJSON(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.file = filename;
	}
	
	public String toJSON() {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> smap = new HashMap<String, Object>();
		for (Scene s : scenes) {
			smap.put(s.name, s.getMap());
		}
		map.put("scenes", smap);
		return JSONUtil.toJSON(map);
	}
	
	@SuppressWarnings("unchecked")
	public void fromJSON(String str) {
		try {
			Map<String, Object> map = (Map<String, Object>) JSONParser.parseJSON(str);
			Map<String, Object> scenes = (Map<String, Object>) map.get("scenes");
			for (String s : scenes.keySet()) {
				Map<String, Object> scene = (Map<String, Object>) scenes.get(s);
				List<Map<String, Object>> choices = (List<Map<String, Object>>) scene.get("choices"); //?
				List<Choice> cchoices = new ArrayList<Choice>();
				if (choices != null) {
					for (Map<String, Object> choice : choices) {
						Choice newChoice = new Choice((String) choice.get("text"), (String) choice.get("scene"));
						cchoices.add(newChoice);
					}
				}
				Object txt = scene.get("text");
				if (txt instanceof List<?>) {
					String result = "";
					for (String st : (List<String>) txt) {
						if (!result.equals("")) {
							result += "\n\n";
						}
						result += st;
					}
					txt = result;
				}
				txt = ((String) txt).substring(0, ((String) txt).length() - 4);
				txt = ((String) txt).replaceAll("(\\\\n)", "\n");
				Scene newScene = new Scene(s, (String) txt, cchoices);
				this.scenes.add(newScene);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
