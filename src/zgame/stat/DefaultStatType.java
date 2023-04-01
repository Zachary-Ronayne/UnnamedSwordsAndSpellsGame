package zgame.stat;

/** A {@link StatType} which exists as a default fallback value. */
public enum DefaultStatType implements StatType<DefaultStatType>{
	DEFAULT;
	
	/** Called to ensure the stat ids get initialized */
	public static void init(){
		DEFAULT.getId();
		StatType.add(DefaultStatType.values());
		
		// Weird nonsense to make sure the static stat dependency array has the DEFAULT stat enum
		var s = new Stats();
		s.add(new Stat(s, DEFAULT){
			@Override
			public double calculateValue(){
				return 0;
			}
		});
	}
	
	/** The id of the default stat */
	private final int id;
	
	/** Init the default stat */
	DefaultStatType(){
		this.id = StatId.next();
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	public DefaultStatType getFromId(int id){
		return DEFAULT;
	}
}
