package com.balugaq.bim;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@FunctionalInterface
public interface ClickHandler {
    public static final ClickHandler cancel = (b) -> b.event().setCancelled(true);

    void onClick(ClickDTO bundle);
}
