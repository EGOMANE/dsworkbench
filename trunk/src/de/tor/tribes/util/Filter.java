/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

/**
 *
 * @author Torridity
 */
public abstract interface Filter<C> {

    public boolean isValid(C c);
}
