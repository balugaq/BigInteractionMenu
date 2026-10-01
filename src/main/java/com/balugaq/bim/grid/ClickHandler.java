/*
 * Copyright (c) 2026 balugaq
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.balugaq.bim.grid;

import org.jspecify.annotations.NullMarked;

/**
 * 类似 Slimefun 的 MenuClickHandler，这里使用一个 DTO 存储参数
 *
 * @author balugaq
 */
@NullMarked
@FunctionalInterface
public interface ClickHandler {
    public static final ClickHandler cancel = (b) -> b.event().setCancelled(true);

    void onClick(ClickDTO bundle);
}
