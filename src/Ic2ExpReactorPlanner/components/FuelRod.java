/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ic2ExpReactorPlanner.components;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents some form of fuel rod (may be single, dual, or quad).
 * @author Brian McCloud
 */
public class FuelRod extends ReactorItem {
    
    private final int energyMult;
    private final double heatMult;
    private final int rodCount;
    private final boolean moxStyle;
    
    private static boolean GT509behavior = false;
    private static boolean GTNHbehavior = false;
    
    public static void setGT509Behavior(boolean value) {
        GT509behavior = value;
    }
    
    public static void setGTNHBehavior(boolean value) {
        GTNHbehavior = value;
    }

    public FuelRod(final int id, final String baseName, final String name, final Image image, final double maxDamage, final double maxHeat, final String sourceMod,
            final int energyMult, final double heatMult, final int rodCount, final boolean moxStyle) {
        super(id, baseName, name, image, maxDamage, maxHeat, sourceMod);
        this.energyMult = energyMult;
        this.heatMult = heatMult;
        this.rodCount = rodCount;
        this.moxStyle = moxStyle;
    }
    
    public FuelRod(final FuelRod other) {
        super(other);
        this.energyMult = other.energyMult;
        this.heatMult = other.heatMult;
        this.rodCount = other.rodCount;
        this.moxStyle = other.moxStyle;
    }

    @Override
    public boolean isNeutronReflector() {
        return !isBroken();
    }

    private int countNeutronNeighbors() {
        int neutronNeighbors = 0;
        ReactorItem component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        return neutronNeighbors;
    }

    protected List<ReactorItem> getHeatableNeighbors() {
        List<ReactorItem> heatableNeighbors = new ArrayList<>(4);
        ReactorItem component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        return heatableNeighbors;
    }

    protected List<ReactorItem> getGTHeatableNeighbors() {
        List<ReactorItem> heatableNeighbors = new ArrayList<>(4);
        ReactorItem component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        return heatableNeighbors;
    }

    protected void handleHeat(final int heat) {
        List<ReactorItem> heatableNeighbors = getHeatableNeighbors();
        if (heatableNeighbors.isEmpty()) {
            parent.adjustCurrentHeat(heat);
            currentHullHeating = heat;
        } else {
            currentComponentHeating = heat;
            for (ReactorItem heatableNeighbor : heatableNeighbors) {
                heatableNeighbor.adjustCurrentHeat(heat / heatableNeighbors.size());
            }
            int remainderHeat = heat % heatableNeighbors.size();
            heatableNeighbors.get(0).adjustCurrentHeat(remainderHeat);
        }
    }

    protected void handleGTHeat(final int heat) {
        List<ReactorItem> heatableNeighbors = getGTHeatableNeighbors();
        if (heatableNeighbors.isEmpty()) {
            parent.adjustCurrentHeat(heat);
            currentHullHeating = heat;
        } else {
            currentComponentHeating = heat;
            int everCycleHeat = heat / rodCount;
            for (int i = 0; i < heatableNeighbors.size(); i++) {
                int toNeighborHeat = everCycleHeat / (heatableNeighbors.size() - i);
                everCycleHeat -= toNeighborHeat;
                heatableNeighbors.get(i).adjustCurrentHeat(rodCount * toNeighborHeat);
            }
        }
    }
    
    @Override
    public double generateHeat() {
        int pulses = countNeutronNeighbors() + 1 + rodCount / 2;
        int heat = (int)(heatMult * pulses * (pulses + 1));
        if (moxStyle && parent.isFluid() && (parent.getCurrentHeat() / parent.getMaxHeat()) > 0.5) {
            heat *= 2;
        }
        currentHeatGenerated = heat;
        minHeatGenerated = Math.min(minHeatGenerated, heat);
        maxHeatGenerated = Math.max(maxHeatGenerated, heat);
        if (GT509behavior || GTNHbehavior) {
            handleGTHeat(heat);
        } else {
            handleHeat(heat);
        }
        return currentHeatGenerated;
    }

    @Override
    public double generateEnergy() {
        int pulses = countNeutronNeighbors() + 1 + rodCount / 2;
        double energy = energyMult * pulses;
        if (GT509behavior || "GT5.09".equals(sourceMod)) {
            energy *= 2;//EUx2 if from GT5.09 or in GT5.09 mode
            if (moxStyle) {
                energy *= (1 + 1.5 * parent.getCurrentHeat() / parent.getMaxHeat());
            }
        } else if (GTNHbehavior || "GTNH".equals(sourceMod)) {
            energy *= 10;//EUx10 if from GTNH or in GTNH mode
            if (moxStyle) {
                energy *= (1 + 1.5 * parent.getCurrentHeat() / parent.getMaxHeat());
            }
        } else if (moxStyle) {
            energy *= (1 + 4.0 * parent.getCurrentHeat() / parent.getMaxHeat());
        }
        minEUGenerated = Math.min(minEUGenerated, energy);
        maxEUGenerated = Math.max(maxEUGenerated, energy);
        currentEUGenerated = energy;
        parent.addEUOutput(energy);
        applyDamage(1.0);
        return energy;
    }
    
    @Override
    public int getRodCount() {
        return rodCount;
    }
    
    @Override
    public double getCurrentOutput() {
        if (parent != null) {
            if (parent.isFluid()) {
                return currentHeatGenerated;
            } else {
                return currentEUGenerated;
            }
        }
        return 0;
    }

}
