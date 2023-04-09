package zgame.menu.format;

import zgame.menu.MenuThing;

/** An object for {@link MenuThing} which allows its position to be updated when its parent's width and height change */
public interface MenuFormatter{
	
	/**
	 * Called whenever a {@link MenuThing}'s {@link MenuThing#parent}'s width is changed
	 * @param parent The parent which was changed
	 * @param thing The thing which is under the parent
	 * @param oldWidth The width before the change
	 * @param newWidth The width after the change
	 */
	void onWidthChange(MenuThing parent, MenuThing thing, double oldWidth, double newWidth);
	
	/**
	 * Called whenever a {@link MenuThing}'s {@link MenuThing#parent}'s height is changed
	 * @param parent The parent which was changed
	 * @param thing The thing which is under the parent
	 * @param oldHeight The height before the change
	 * @param newHeight The height after the change
	 */
	void onHeightChange(MenuThing parent, MenuThing thing, double oldHeight, double newHeight);

}
