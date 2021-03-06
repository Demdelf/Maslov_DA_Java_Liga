package com.bad.code2.shapes.shape3D;

/**
 * Интерфейс объёмной фигуры
 */
public interface Shape3D {

    /**
     * Возвращает центральную точку фигуры по оси X
     * @return центральная точка фигуры по оси X
     */
    Double getCenterX();

    /**
     * Возвращает центральную точку фигуры по оси Y
     * @return центральная точка фигуры по оси Y
     */
    Double getCenterY();

    /**
     * Возвращает центральную точку фигуры по оси Z
     * @return центральная точка фигуры по оси Z
     */
    Double getCenterZ();

    /**
     * Возвращает объём фигуры
     * @return объём фигуры
     */
    Double getVolume();
}
