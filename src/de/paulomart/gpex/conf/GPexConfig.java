package de.paulomart.gpex.conf;

import java.util.HashMap;

import lombok.Getter;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexGroup;

@Getter
public class GPexConfig extends BaseConfig{

	private GPex gpex;
	private HashMap<String, GPexGroup> groups = new HashMap<String, GPexGroup>();

	
	public GPexConfig(){
		super(GPex.getInstance(), "config.yml", "config.yml", true, true);
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onSave() {
		
	}
	
	
}
