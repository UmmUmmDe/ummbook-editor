package uud.ummbook.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
	
	public String text;
	public String name;
	public List<Choice> choices;
	
	public Scene(String name) {
		this(name, "");
	}
	
	public Scene(String name, String text) {
		this(name, text, new ArrayList<Choice>());
	}
	
	public Scene(String name, String text, Choice[] choices) {
		this(name, text);
		for (Choice c : choices) {
			this.choices.add(c);
		}
	}
	
	public Scene(String name, String text, List<Choice> choices) {
		this.name = name;
		this.text = text;
		this.choices = choices;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", text);
		List<Map<String, Object>> clist = new ArrayList<Map<String, Object>>();
		for (Choice c : choices) {
			Map<String, Object> cmap = new HashMap<String, Object>();
			cmap.put("scene", c.scene);
			cmap.put("text", c.text);
			clist.add(cmap);
		}
		map.put("choices", clist);
		return map;
	}

}
