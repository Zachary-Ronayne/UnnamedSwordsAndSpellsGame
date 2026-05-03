package zusass.game.things.entities.mobs;

import zgame.core.graphics.Renderer;
import zgame.core.graphics.ZColor;
import zgame.core.graphics.buffer.DrawableBuffer;
import zgame.core.graphics.image.ImageManager;
import zgame.core.sound.ManagedSoundSource;
import zgame.core.utils.NotNullList;
import zgame.core.utils.ZMath;
import zgame.stat.modifier.ModifierType;
import zusass.ZusassGame;
import zusass.game.magic.ProjectileSpell;
import zusass.game.magic.Spell;
import zusass.game.magic.effect.SpellEffectStatAdd;
import zusass.utils.ZusassImages;
import zusass.utils.ZusassSounds;

import static zusass.game.stat.ZusassStat.*;

/** A generic mob which uses health, status, etc, and is not a player */
public class Npc extends ZusassMob{
	
	/** The amount of time, in seconds since the last spell cast */
	private double spellTime;
	
	/** The buffer for drawing this Npc's resource bar */
	private final DrawableBuffer resourceBarBuffer;
	
	/** The name of the source used to play the attack swing sound of this npc */
	private static final String SOUND_SOURCE_ATTACK = "attack";
	/** The name of the source used to play the damage sound of this npc */
	private static final String SOUND_SOURCE_TAKE_DAMAGE = "takeDamage";
	
	/**
	 * Create a new Npc with the given bounds
	 *
	 * @param x See {@link #getX()}
	 * @param y See {@link #getY()}
	 * @param z See {@link #getZ()}
	 * @param radius See {@link #radius}
	 * @param height See {@link #height}
	 */
	public Npc(double x, double y, double z, double radius, double height){
		super(x, y, z, radius, height);
		
		this.setStat(MOVE_SPEED, 0.6);
		this.setStat(ATTACK_SPEED, 1);
		this.setStat(STAMINA_REGEN, 1);
		this.setStat(ENDURANCE, 4);
		this.setStat(INTELLIGENCE, 5);
		
		// Set a default spell as speed
		this.getSpells().addSpell(Spell.selfEffect(MOVE_SPEED, 1.5, 1, ModifierType.MULT_MULT).named("Small Speed"));
		var hurtSpell = new ProjectileSpell(new NotNullList<>(new SpellEffectStatAdd(HEALTH, -10)), 0.2, 1.5, 0.6).named("hurt");
		this.getSpells().addSpell(hurtSpell);
		this.getSpells().setSelectedSpellIndex(0);
		
		this.spellTime = 0;
		
		int barBufferWidth = 300;
		int barPixelHeight = 24;
		int barBufferHeight = 90;
		this.resourceBarBuffer = new DrawableBuffer(barBufferWidth, barBufferHeight){
			@Override
			public void draw(Renderer r){
				drawResourceBars(r, 0, 0, barBufferWidth, barPixelHeight, false);
			}
		};
	}
	
	@Override
	public void initSounds(){
		super.initSounds();
		
		var sounds = this.getSounds();
		sounds.add(SOUND_SOURCE_ATTACK, new ManagedSoundSource(ZusassSounds.SWORD_SWING, this, 0.4, 0.44, 1));
		sounds.add(SOUND_SOURCE_TAKE_DAMAGE, new ManagedSoundSource(ZusassSounds.MONSTER_GROWL, this, 0.8, 0.9, 1));
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.resourceBarBuffer.destroy();
	}
	
	@Override
	public boolean beginAttack(){
		boolean attacked = super.beginAttack();
		if(attacked){
			this.getSounds().updateAndPlay(SOUND_SOURCE_ATTACK, s -> s.setOffsetY(this.getEyeHeight()));
		}
		return attacked;
	}
	
	@Override
	public void playDamageSound(){
		super.playDamageSound();
		this.getSounds().updateAndPlay(SOUND_SOURCE_TAKE_DAMAGE, s -> s.setOffsetY(this.getEyeHeight()));
	}
	
	@Override
	public void tick(double dt){
		super.tick(dt);
		
		var zgame = ZusassGame.get();
		var player = zgame.getPlayer();
		double playerDx = player.getX() - this.getX();
		double playerDy = (player.getY() + player.getHeight() * 0.5) - (this.getY() + this.getHeight());
		double playerDz = player.getZ() - this.getZ();
		double playerDist = Math.sqrt(playerDx * playerDx + playerDz * playerDz);
		double playerYaw = ZMath.PI_BY_2 + ZMath.atan2Normalized(playerDz, playerDx);
		double playerPitch = -ZMath.atan2Normalized(playerDy, playerDist);
		
		// Simplistic ai to move to the player
		boolean inRange = playerDist < this.stat(ATTACK_RANGE) * 0.9;
		this.handleMobilityControls(dt, playerYaw, playerPitch, false, false, !inRange, false, false, false);
		// Stop moving once close enough
		if(inRange) this.stopWalking();
		
		// If the AI has an attack available, and stamina is at least 75%, begin attacking
		var staminaPerc = this.currentStaminaPerc();
		if(this.getAttackTime() <= 0 && inRange && staminaPerc > .75){
			this.beginAttack();
		}
		
		// If not in range, use the speed spell, otherwise use the damage spell
		if(!inRange) this.getSpells().setSelectedSpellIndex(0);
		else this.getSpells().setSelectedSpellIndex(1);
		
		this.spellTime += dt;
		
		// Try to cast a spell if enough time has passed
		double intelligence = this.stat(INTELLIGENCE);
		if(intelligence > 0 && this.spellTime > 10 / intelligence) this.castSpell();
		
		// If sprinting and stamina is low, stop sprinting
		var sprinting = this.isSprinting();
		if(staminaPerc < .25 && sprinting || inRange) this.setSprinting(false);
		// If stamina is above 75% and not sprinting, start sprinting
		else if(staminaPerc > .75 && !sprinting) this.setSprinting(true);
	}
	
	@Override
	public boolean castSpell(){
		var success = super.castSpell();
		if(success) this.spellTime = 0;
		return success;
	}
	
	@Override
	protected void render(Renderer r){
		// Draw an attack timer
		r.setColor(0, 0.7, 0);
		this.renderAttackTimer(r, ImageManager.image(ZusassImages.CLUB));
		
		double facingAngle = this.getMobilityState().getFacingYaw();
		
		// If damaged, also render a red tint
		boolean tintRed = this.getLastDamageTime() < 0.3;
		if(tintRed){
			r.pushTextureTintAddShader();
			r.pushColor(new ZColor(0.5, 0, 0, 0));
		}
		
		// Render a billboard texture
		r.drawPlaneBufferSide(
				this.getX(), this.getY() + this.getHeight() * 0.5, this.getZ(), this.getWidth(), this.getHeight(),
				facingAngle, ImageManager.image(ZusassImages.GOBLIN).getId());
		
		if(tintRed){
			r.popShader();
			r.popColor();
		}
		
		// Draw bars to represent its remaining health, stamina, and mana
		this.resourceBarBuffer.redraw(r);
		
		double barWidth = this.getWidth() * 1.2;
		double barHeight = barWidth / this.resourceBarBuffer.getWidth() * this.resourceBarBuffer.getHeight();
		r.drawPlaneBufferSide(
				this.getX(), this.getY() + this.getHeight() + 0.05, this.getZ(), barWidth, barHeight, facingAngle,
				this.resourceBarBuffer.getTextureID());
	}
	
	@Override
	public ManagedSoundSource buildFootstepSource(){
		return new ManagedSoundSource(ZusassSounds.FOOTSTEP, this, 0.85, 0.98, 10);
	}
	
}
