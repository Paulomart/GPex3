package de.paulomart.gpex.permissions;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GPexGroup {
	
	private List<GPexPermission> permissions = new ArrayList<GPexPermission>();	
	private String chatPrefix = "";
	private String chatSuffix = "";
	private String tabPrefix = "";
	private String tabSuffix = "";
	

}
