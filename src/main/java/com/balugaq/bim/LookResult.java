package com.balugaq.bim;

public record LookResult(boolean success, InteractUnit unit) {
    public static final LookResult FAIL = new LookResult(false, null);
    public static LookResult fail() {
        return FAIL;
    }

    public static LookResult success(InteractUnit unit) {
        return new LookResult(true, unit);
    }
}
