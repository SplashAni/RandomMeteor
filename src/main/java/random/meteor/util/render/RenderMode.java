package random.meteor.util.render;

import meteordevelopment.meteorclient.renderer.ShapeMode;

public enum RenderMode { // only made custom cuz i have some future iedads for this <3
    Fill,
    Outline,
    Both;

    public ShapeMode toShapeMode() {
        return switch (this) {
            case Fill -> ShapeMode.Sides;
            case Outline -> ShapeMode.Lines;
            case Both -> ShapeMode.Both;
        };
    }
}
