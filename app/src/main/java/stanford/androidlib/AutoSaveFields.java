package stanford.androidlib;

import java.lang.annotation.*;

/**
 * This annotation can be placed at the top of a SimpleActivity class to make its
 * fields auto-save themselves when that activity loads and unloads from memory.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AutoSaveFields {
    // TODO
    // String[] names() default {};
}
