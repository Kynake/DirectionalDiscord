package com.kynake.minecraft.directionablediscord.modules.base.block;

/**
 * Enum with possible states a block can have when pertaining to it's item form:
 * - It has no item
 * - It has a standard block-in-item-form item
 * - It has a custom item, to be defined elsewhere
 */
public enum BlockItemType { NONE, DEFAULT, CUSTOM }