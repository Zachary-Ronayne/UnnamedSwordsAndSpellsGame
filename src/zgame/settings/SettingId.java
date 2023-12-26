package zgame.settings;

/** A class to generate ids to use for identifying settings in an array */
public class SettingId{
	/** The current id used for {@link SettingType} */
	private static int currentId = 0;
	
	/** @return The next id to use for a setting */
	public synchronized static int next(){
		var current = currentId;
		currentId += 1;
		return current;
	}
	
	/** @return The number of ids currently existing for {@link SettingType} */
	public static int numIds(){
		return currentId;
	}
	/** Cannot instantiate {@link SettingId} */
	private SettingId(){}
}