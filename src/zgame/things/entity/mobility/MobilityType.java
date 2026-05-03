package zgame.things.entity.mobility;

import zgame.things.entity.MobilityState;

/** An enum describing different kinds of movement */
public enum MobilityType{
	/** Normal mobility on the ground where gravity applies */
	WALKING(Mobility::walkingTick, MobilityState::updateWalkForces),
	/** Mobility where gravity does not apply and movement on the y axis is allowed, movement is based on the direction facing on both axes */
	FLYING(Mobility::flyingTick, MobilityState::updateFlyForces),
	/** Mobility where gravity does not apply and movement on the y axis is allowed, facing direction only effects horizontal movement */
	FLYING_AXIS(Mobility::flyingTick, MobilityState::updateFlyForces);
	
	/** The function in the {@link Mobility} class to call when this type of movement happens */
	private final TickFunc tickFunc;
	
	/** The function in the {@link MobilityState} class to call when this type of movement is selected, cleaning up any necessary forces */
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
	 */
	public void tick(Mobility<?> m){
		this.tickFunc.tick(m);
	}
	
	/**
	 * Apply {@link #mobilityDataFunc}
	 * @param m The {@link MobilityState} object to update
	 */
	public void updateForces(MobilityState<?> m){
		this.mobilityDataFunc.run(m);
	}
	
	/** A shortcut interface for a function that consumes the data needed to process movement */
	public interface TickFunc{
		/**
		 * Perform one tick on the given {@link Mobility}
		 * @param m The object to perform the tick on
		 */
		void tick(Mobility<?> m);
	}
	
	/** A shortcut interface for a function that consumes the data needed to process updating something on a {@link MobilityState} object */
	public interface MobilityDataFunc{
		/**
		 * Perform the action on the given {@link Mobility}
		 * @param m The object to perform on
		 */
		void run(MobilityState<?> m);
	}
	
}
