package zgame.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/** Marker annotation for methods to indicate that they are intentionally package private, the modifier was not accidentally excluded */
@Target(ElementType.METHOD)
public @interface PackagePrivate{
}
