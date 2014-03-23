package de.paulomart.gpex.conf;

import lombok.Getter;

import de.paulomart.gpex.GPex;

@Getter
public class GPexConfig extends BaseConfig{

	private GPex gpex;
	
	public GPexConfig(){
		super(GPex.getInstance(), "config.yml", "config.yml", true, true);
		gpex = GPex.getInstance();
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onSave() {
		
	}
	
	
}
