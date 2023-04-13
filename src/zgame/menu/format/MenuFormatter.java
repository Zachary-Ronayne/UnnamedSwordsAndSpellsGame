package zgame.menu.format;

import zgame.menu.MenuThing;

/** An object for {@link MenuThing} which allows its position to be updated when its parent's width and height change */
public interface MenuFormatter{
	
	/**
	 * Called whenever a {@link MenuThing}'s {@link MenuThing#parent}'s width is changed
	 * @param thing The thing which is under the parent
	 * @param width The new width of the parent
	 */
	void onWidthChange(MenuThing thing, double width);
	
	/**
	 * Called whenever a {@link MenuThing}'s {@link MenuThing#parent}'s height is changed
	 * @param thing The thing which is under the parent
	 * @param height The new height of the parent
	 */
	void onHeightChange(MenuThing thing, double height);

}
