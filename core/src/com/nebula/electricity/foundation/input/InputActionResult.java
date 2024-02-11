package com.nebula.electricity.foundation.input;

public class InputActionResult {
    private final boolean successful;
    private final InputState newMode;

    private InputActionResult (boolean successful, InputState mode) {
        this.successful = successful;
        this.newMode = mode;
    }

    public static InputActionResult success (InputState mode) {
        return new InputActionResult(true, mode);
    }

    public static InputActionResult failure (InputState mode) {
        return new InputActionResult(false, mode);
    }

    boolean wasSuccessful () { return successful; }
    InputState getNewMode () { return newMode; }
}
