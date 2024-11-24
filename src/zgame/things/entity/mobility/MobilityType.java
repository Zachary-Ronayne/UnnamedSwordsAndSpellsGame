package zgame.things.entity.mobility;

import zgame.things.entity.MobilityData;

/** An enum describing different kinds of movement */
public enum MobilityType{
	/** Normal mobility on the ground where gravity applies */
	WALKING(Mobility::walkingTick, MobilityData::updateWalkForces),
	/** Mobility where gravity does not apply and movement on the y axis is allowed, movement is based on the direction facing on both axes */
	FLYING(Mobility::flyingTick, MobilityData::updateFlyForces),
	/** Mobility where gravity does not apply and movement on the y axis is allowed, facing direction only effects horizontal movement */
	FLYING_AXIS(Mobility::flyingTick, MobilityData::updateFlyForces);
	
	/** The function in the {@link Mobility} class to call when this type of movement happens */
	private final TickFunc tickFunc;
	
	/** The function in the {@link MobilityData} class to call when this type of movement is selected, cleaning up any necessary forces */
	private final MobilityDataFunc mobilityDataFunc;
	
	/**
	 * Create a new instance of the {@link MobilityType} enum
	 * @param tickFunc See {@link #tickFunc}
	 */
	MobilityType(TickFunc tickFunc, MobilityDataFunc mobilityDataFunc){
		this.tickFunc = tickFunc;
		this.mobilityDataFunc = mobilityDataFunc;
	}
	
	/**
	 * Apply {@link #tickFunc}
	 * @param m The {@link Mobility} object to apply
	 * @param dt The amount of time in seconds passed in the tick
	 */
	public void tick(Mobility<?, ?, ?, ?, ?> m, double dt){
		this.tickFunc.tick(m, dt);
	}
	
	/**
	 * Apply {@link #mobilityDataFunc}
	 * @param m The {@link MobilityData} object to update
	 */
	public void updateForces(MobilityData<?, ?, ?, ?, ?> m){
		this.mobilityDataFunc.run(m);
	}
	
	/** A shortcut interface for a function that consumes the data needed to process movement */
	public interface TickFunc{
		/**
		 * Perform one tick on the given {@link Mobility}
		 * @param m The object to perform the tick on
		 * @param dt The amount of time in seconds passed in the tick
		 */
		void tick(Mobility<?, ?, ?, ?, ?> m, double dt);
	}
	
	/** A shortcut interface for a function that consumes the data needed to process updating something on a {@link MobilityData} object */
	public interface MobilityDataFunc{
		/**
		 * Perform the action on the given {@link Mobility}
		 * @param m The object to perform on
		 */
		void run(MobilityData<?, ?, ?, ?, ?> m);
	}
	
}
