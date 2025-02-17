/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ic2ExpReactorPlanner;

import static Ic2ExpReactorPlanner.BundleHelper.getI18n;

import Ic2ExpReactorPlanner.components.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to handle creating components by id or name.
 * @author Brian McCloud
 */
public class ComponentFactory {
    
    private ComponentFactory() {
        // do nothing, this class should not be instantiated.
    }
    
    private static final ReactorItem[] ITEMS = {
        null, // special entry to make sure id 0 corresponds to empty space (no component)
        new FuelRod(1, "fuelRodUranium", getI18n("ComponentName.FuelRodUranium"), TextureFactory.getImage("reactorUraniumSimple.png", "uranium.png"), 20e3, 1, null, 100, 2, 1, false),
        new FuelRod(2, "dualFuelRodUranium", getI18n("ComponentName.DualFuelRodUranium"), TextureFactory.getImage("reactorUraniumDual.png", "dual_uranium.png"), 20e3, 1, null, 200, 4, 2, false),
        new FuelRod(3, "quadFuelRodUranium", getI18n("ComponentName.QuadFuelRodUranium"), TextureFactory.getImage("reactorUraniumQuad.png", "quad_uranium.png"), 20e3, 1, null, 400, 8, 4, false),
        new FuelRod(4, "fuelRodMox", getI18n("ComponentName.FuelRodMox"), TextureFactory.getImage("reactorMOXSimple.png", "mox.png"), 10e3, 1, null, 100, 2, 1, true),
        new FuelRod(5, "dualFuelRodMox", getI18n("ComponentName.DualFuelRodMox"), TextureFactory.getImage("reactorMOXDual.png", "dual_mox.png"), 10e3, 1, null, 200, 4, 2, true),
        new FuelRod(6, "quadFuelRodMox", getI18n("ComponentName.QuadFuelRodMox"), TextureFactory.getImage("reactorMOXQuad.png", "quad_mox.png"), 10e3, 1, null, 400, 8, 4, true),
        new Reflector(7, "neutronReflector", getI18n("ComponentName.NeutronReflector"), TextureFactory.getImage("reactorReflector.png", "neutron_reflector.png"), 30e3, 1, null),
        new Reflector(8, "thickNeutronReflector", getI18n("ComponentName.ThickNeutronReflector"), TextureFactory.getImage("reactorReflectorThick.png", "thick_neutron_reflector.png"), 120e3, 1, null),
        new Vent(9, "heatVent", getI18n("ComponentName.HeatVent"), TextureFactory.getImage("reactorVent.png", "heat_vent.png"), 1, 1000, null, 6, 0, 0),
        new Vent(10, "advancedHeatVent", getI18n("ComponentName.AdvancedHeatVent"), TextureFactory.getImage("reactorVentDiamond.png", "advanced_heat_vent.png"), 1, 1000, null, 12, 0, 0),
        new Vent(11, "reactorHeatVent", getI18n("ComponentName.ReactorHeatVent"), TextureFactory.getImage("reactorVentCore.png", "reactor_heat_vent.png"), 1, 1000, null, 5, 5, 0),
        new Vent(12, "componentHeatVent", getI18n("ComponentName.ComponentHeatVent"), TextureFactory.getImage("reactorVentSpread.png", "component_heat_vent.png"), 1, 1, null, 0, 0, 4),
        new Vent(13, "overclockedHeatVent", getI18n("ComponentName.OverclockedHeatVent"), TextureFactory.getImage("reactorVentGold.png", "overclocked_heat_vent.png"), 1, 1000, null, 20, 36, 0),
        new CoolantCell(14, "coolantCell10k", getI18n("ComponentName.CoolantCell10k"), TextureFactory.getImage("reactorCoolantSimple.png", "heat_storage.png"), 1, 10e3, null),
        new CoolantCell(15, "coolantCell30k", getI18n("ComponentName.CoolantCell30k"), TextureFactory.getImage("reactorCoolantTriple.png", "tri_heat_storage.png"), 1, 30e3, null),
        new CoolantCell(16, "coolantCell60k", getI18n("ComponentName.CoolantCell60k"), TextureFactory.getImage("reactorCoolantSix.png", "hex_heat_storage.png"), 1, 60e3, null),
        new Exchanger(17, "heatExchanger", getI18n("ComponentName.HeatExchanger"), TextureFactory.getImage("reactorHeatSwitch.png", "heat_exchanger.png"), 1, 2500, null, 12, 4),
        new Exchanger(18, "advancedHeatExchanger", getI18n("ComponentName.AdvancedHeatExchanger"), TextureFactory.getImage("reactorHeatSwitchDiamond.png", "advanced_heat_exchanger.png"), 1, 10e3, null, 24, 8),
        new Exchanger(19, "coreHeatExchanger", getI18n("ComponentName.ReactorHeatExchanger"), TextureFactory.getImage("reactorHeatSwitchCore.png", "reactor_heat_exchanger.png"), 1, 5000, null, 0, 72),
        new Exchanger(20, "componentHeatExchanger", getI18n("ComponentName.ComponentHeatExchanger"), TextureFactory.getImage("reactorHeatSwitchSpread.png", "component_heat_exchanger.png"), 1, 5000, null, 36, 0),
        new Plating(21, "reactorPlating", getI18n("ComponentName.ReactorPlating"), TextureFactory.getImage("reactorPlating.png", "plating.png"), 1, 1, null, 1000, 0.9025),
        new Plating(22, "heatCapacityReactorPlating", getI18n("ComponentName.HeatCapacityReactorPlating"), TextureFactory.getImage("reactorPlatingHeat.png", "heat_plating.png"), 1, 1, null, 1700, 0.9801),
        new Plating(23, "containmentReactorPlating", getI18n("ComponentName.ContainmentReactorPlating"), TextureFactory.getImage("reactorPlatingExplosive.png", "containment_plating.png"), 1, 1, null, 500, 0.81),
        new Condensator(24, "rshCondensator", getI18n("ComponentName.RshCondensator"), TextureFactory.getImage("reactorCondensator.png", "rsh_condensator.png"), 1, 20e3, null),
        new Condensator(25, "lzhCondensator", getI18n("ComponentName.LzhCondensator"), TextureFactory.getImage("reactorCondensatorLap.png", "lzh_condensator.png"), 1, 100e3, null),
        new FuelRod(26, "fuelRodThorium", getI18n("ComponentName.FuelRodThorium"), TextureFactory.getImage("gt.Thoriumcell.png"), 50e3, 1, "GT5.08", 20, 0.5, 1, false),
        new FuelRod(27, "dualFuelRodThorium", getI18n("ComponentName.DualFuelRodThorium"), TextureFactory.getImage("gt.Double_Thoriumcell.png"), 50e3, 1, "GT5.08", 40, 1, 2, false),
        new FuelRod(28, "quadFuelRodThorium", getI18n("ComponentName.QuadFuelRodThorium"), TextureFactory.getImage("gt.Quad_Thoriumcell.png"), 50e3, 1, "GT5.08", 80, 2, 4, false),
        new CoolantCell(29, "coolantCellHelium60k", getI18n("ComponentName.CoolantCell60kHelium"), TextureFactory.getImage("gt.60k_Helium_Coolantcell.png"), 1, 60e3, "GT5.08"),
        new CoolantCell(30, "coolantCellHelium180k", getI18n("ComponentName.CoolantCell180kHelium"), TextureFactory.getImage("gt.180k_Helium_Coolantcell.png"), 1, 180e3, "GT5.08"),
        new CoolantCell(31, "coolantCellHelium360k", getI18n("ComponentName.CoolantCell360kHelium"), TextureFactory.getImage("gt.360k_Helium_Coolantcell.png"), 1, 360e3, "GT5.08"),
        new CoolantCell(32, "coolantCellNak60k", getI18n("ComponentName.CoolantCell60kNak"), TextureFactory.getImage("gt.60k_NaK_Coolantcell.png"), 1, 60e3, "GT5.08"),
        new CoolantCell(33, "coolantCellNak180k", getI18n("ComponentName.CoolantCell180kNak"), TextureFactory.getImage("gt.180k_NaK_Coolantcell.png"), 1, 180e3, "GT5.08"),
        new CoolantCell(34, "coolantCellNak360k", getI18n("ComponentName.CoolantCell360kNak"), TextureFactory.getImage("gt.360k_NaK_Coolantcell.png"), 1, 360e3, "GT5.08"),
        new Reflector(35, "iridiumNeutronReflector", getI18n("ComponentName.IridiumNeutronReflector"), TextureFactory.getImage("gt.neutronreflector.png", "neutron_reflector.png"), 1, 1, null),
       	new FuelRod(36, "fuelRodNaquadah", getI18n("ComponentName.FuelRodNaquadah"), TextureFactory.getImage("gt.Naquadahcell.png"), 100e3, 1, "GT5.09", 100, 2, 1, true),
       	new FuelRod(37, "dualFuelRodNaquadah", getI18n("ComponentName.DualFuelRodNaquadah"), TextureFactory.getImage("gt.Double_Naquadahcell.png"), 100e3, 1, "GT5.09", 200, 4, 2, true),
       	new FuelRod(38, "quadFuelRodNaquadah", getI18n("ComponentName.QuadFuelRodNaquadah"), TextureFactory.getImage("gt.Quad_Naquadahcell.png"), 100e3, 1, "GT5.09", 400, 8, 4, true),
        new FuelRod(39, "fuelRodCoaxium", getI18n("ComponentName.FuelRodCoaxium"), TextureFactory.getImage("coaxium_rod.png"), 20e3, 1, "Coaxium", 100, 0, 1, false),
        new FuelRod(40, "dualFuelRodCoaxium", getI18n("ComponentName.DualFuelRodCoaxium"), TextureFactory.getImage("coaxium_rod_dual.png"), 20e3, 1, "Coaxium", 200, 0, 2, false),
        new FuelRod(41, "quadFuelRodCoaxium", getI18n("ComponentName.QuadFuelRodCoaxium"), TextureFactory.getImage("coaxium_rod_quad.png"), 20e3, 1, "Coaxium", 400, 0, 4, false),
        new FuelRod(42, "fuelRodCesium", getI18n("ComponentName.FuelRodCesium"), TextureFactory.getImage("coaxium_rod.png"), 10861, 1, "Coaxium", 200, 1, 1, false),
        new FuelRod(43, "dualFuelRodCesium", getI18n("ComponentName.DualFuelRodCesium"), TextureFactory.getImage("coaxium_rod_dual.png"), 10861, 1, "Coaxium", 400, 6, 2, false),
        new FuelRod(44, "quadFuelRodCesium", getI18n("ComponentName.QuadFuelRodCesium"), TextureFactory.getImage("coaxium_rod_quad.png"), 10861, 1, "Coaxium", 800, 24, 4, false), 
        new FuelRod(45, "fuelRodNaquadahGTNH", getI18n("ComponentName.FuelRodNaquadahGTNH"), TextureFactory.getImage("gt.Naquadahcell.png"), 100e3, 1, "GTNH", 100, 2, 1, false),//Naq rods are not MOX-like in GTNH,
       	new FuelRod(46, "dualFuelRodNaquadahGTNH", getI18n("ComponentName.DualFuelRodNaquadahGTNH"), TextureFactory.getImage("gt.Double_Naquadahcell.png"), 100e3, 1, "GTNH", 200, 4, 2, false),//we have naquadria for that
       	new FuelRod(47, "quadFuelRodNaquadahGTNH", getI18n("ComponentName.QuadFuelRodNaquadahGTNH"), TextureFactory.getImage("gt.Quad_Naquadahcell.png"), 100e3, 1, "GTNH", 400, 8, 4, false),
        new FuelRod(48, "fuelRodNaquadria", getI18n("ComponentName.FuelRodNaquadria"), TextureFactory.getImage("gt.MNqCell.png"), 100e3, 1, "GTNH", 100, 2, 1, true),
        new FuelRod(49, "dualFuelRodNaquadria", getI18n("ComponentName.DualFuelRodNaquadria"), TextureFactory.getImage("gt.Double_MNqCell.png"), 100e3, 1, "GTNH", 200, 4, 2, true),
        new FuelRod(50, "quadFuelRodNaquadria", getI18n("ComponentName.QuadFuelRodNaquadria"), TextureFactory.getImage("gt.Quad_MNqCell.png"), 100e3, 1, "GTNH", 400, 8, 4, true),
        new FuelRod(51, "fuelRodTiberium", getI18n("ComponentName.FuelRodTiberium"), TextureFactory.getImage("gt.Tiberiumcell.png"), 50e3, 1, "GTNH", 100, 1, 1, false),
        new FuelRod(52, "dualFuelRodTiberium", getI18n("ComponentName.DualFuelRodTiberium"), TextureFactory.getImage("gt.Double_Tiberiumcell.png"), 50e3, 1, "GTNH", 200, 2, 2, false),
        new FuelRod(53, "quadFuelRodTiberium", getI18n("ComponentName.QuadFuelRodTiberium"), TextureFactory.getImage("gt.Quad_Tiberiumcell.png"), 50e3, 1, "GTNH", 400, 4, 4, false),
        new FuelRod(54, "fuelRodTheCore", getI18n("ComponentName.FuelRodTheCore"), TextureFactory.getImage("gt.Core_Reactor_Cell.png"), 100e3, 1, "GTNH", 12800, 64, 32, false),//had to reverse engineer, maybe
        new CoolantCell(55, "coolantCellSpace180k", getI18n("ComponentName.CoolantCell180kSpace"), TextureFactory.getImage("gt.180k_Space_Coolantcell.png"), 1, 180e3, "GTNH"),//it works differently with so
        new CoolantCell(56, "coolantCellSpace360k", getI18n("ComponentName.CoolantCell360kSpace"), TextureFactory.getImage("gt.360k_Space_Coolantcell.png"), 1, 360e3, "GTNH"),//many rods? Or I'm wrong
        new CoolantCell(57, "coolantCellSpace540k", getI18n("ComponentName.CoolantCell540kSpace"), TextureFactory.getImage("gt.540k_Space_Coolantcell.png"), 1, 540e3, "GTNH"),
        new CoolantCell(58, "coolantCellSpace1080k", getI18n("ComponentName.CoolantCell1080kSpace"), TextureFactory.getImage("gt.1080k_Space_Coolantcell.png"), 1, 1080e3, "GTNH"),
        new CoolantCell(59, "coolantCellNeutronium1G", getI18n("ComponentName.CoolantCell1GNeutronium"), TextureFactory.getImage("gt.1g_Neutronium_Coolantcell.png"), 1, 1e9, "GT5.09"),
        new GGFuelRod(60, "fuelRodCompressedUranium", getI18n("ComponentName.FuelRodCompressedUranium"), TextureFactory.getImage("gg.CompressedUranium.png"), 70000, 1, "GTNH" , 400, 2, 1, false, 0),
        new GGFuelRod(61, "dualFuelRodCompressedUranium", getI18n("ComponentName.DualFuelRodCompressedUranium"), TextureFactory.getImage("gg.Double_CompressedUranium.png"), 70000, 1, "GTNH" , 800, 4, 2, false, 0),
        new GGFuelRod(62, "quadFuelRodCompressedUranium", getI18n("ComponentName.QuadFuelRodCompressedUranium"), TextureFactory.getImage("gg.Quad_CompressedUranium.png"), 70000, 1, "GTNH" , 1600, 8, 4, false, 0),
        new GGFuelRod(63, "fuelRodCompressedPlutonium", getI18n("ComponentName.FuelRodCompressedPlutonium"), TextureFactory.getImage("gg.CompressedPlutonium.png"), 30000, 1, "GTNH" , 200, 2, 1, false, 6),
        new GGFuelRod(64, "dualFuelRodCompressedPlutonium", getI18n("ComponentName.DualFuelRodCompressedPlutonium"), TextureFactory.getImage("gg.Double_CompressedPlutonium.png"), 30000, 1, "GTNH" , 400, 4, 2, false, 6),
        new GGFuelRod(65, "quadFuelRodCompressedPlutonium", getI18n("ComponentName.QuadFuelRodCompressedPlutonium"), TextureFactory.getImage("gg.Quad_CompressedPlutonium.png"), 30000, 1, "GTNH" , 800, 8, 4, false, 6),
        new GGFuelRod(66, "fuelRodLiquidUranium", getI18n("ComponentName.FuelRodLiquidUranium"), TextureFactory.getImage("gg.LiquidUranium.png"), 6000, 1, "GTNH" , 4800, 16, 1, false, 0),
        new GGFuelRod(67, "dualFuelRodLiquidUranium", getI18n("ComponentName.DualFuelRodLiquidUranium"), TextureFactory.getImage("gg.Double_LiquidUranium.png"), 6000, 1, "GTNH" , 9600, 32, 2, false, 0),
        new GGFuelRod(68, "quadFuelRodLiquidUranium", getI18n("ComponentName.QuadFuelRodLiquidUranium"), TextureFactory.getImage("gg.Quad_LiquidUranium.png"), 6000, 1, "GTNH" , 19200, 64, 4, false, 0),
        new GGFuelRod(69, "fuelRodLiquidPlutonium", getI18n("ComponentName.FuelRodLiquidPlutonium"), TextureFactory.getImage("gg.LiquidPlutonium.png"), 10000, 1, "GTNH" , 6400, 16, 1, false, 2),
        new GGFuelRod(70, "dualFuelRodLiquidPlutonium", getI18n("ComponentName.DualFuelRodLiquidPlutonium"), TextureFactory.getImage("gg.Double_LiquidPlutonium.png"), 10000, 1, "GTNH" , 12800, 32, 2, false, 2),
        new GGFuelRod(71, "quadFuelRodLiquidPlutonium", getI18n("ComponentName.QuadFuelRodLiquidPlutonium"), TextureFactory.getImage("gg.Quad_LiquidPlutonium.png"), 10000, 1, "GTNH" , 25600, 64, 4, false, 2),
        new BreederCell(72, "fuelRodGlowstone", getI18n("ComponentName.FuelRodGlowstone"), TextureFactory.getImage("gt.GlowstoneCell.png"), 10000, 1, "GTNH", 3000, 1),
    };
    
    private static final Map<String, ReactorItem> ITEM_MAP = makeItemMap();
    
    private static Map<String, ReactorItem> makeItemMap() {
        Map<String, ReactorItem> result = new HashMap<>((int)(ITEMS.length * 1.5));
        for (ReactorItem reactorItem : ITEMS) {
            if (reactorItem != null) {
                result.put(reactorItem.baseName, reactorItem);
            }
        }
        return Collections.unmodifiableMap(result);
    }
    
    private static ReactorItem copy(ReactorItem source) {
        if (source != null) {
            Class<? extends ReactorItem> aClass = source.getClass();
            if (aClass == Condensator.class) {
                return new Condensator((Condensator) source);
            } else if (aClass == CoolantCell.class) {
                return new CoolantCell((CoolantCell) source);
            } else if (aClass == Exchanger.class) {
                return new Exchanger((Exchanger) source);
            } else if (aClass == FuelRod.class) {
                return new FuelRod((FuelRod) source);
            } else if (aClass == Plating.class) {
                return new Plating((Plating) source);
            } else if (aClass == Reflector.class) {
                return new Reflector((Reflector) source);
            } else if (aClass == Vent.class) {
                return new Vent((Vent) source);
            } else if (aClass == GGFuelRod.class) {
                return new GGFuelRod((GGFuelRod) source);
            } else if (aClass == BreederCell.class) {
                return new BreederCell((BreederCell) source);
            }
        }
        return null;
    }
    
    /**
     * Gets a default instances of the specified component (such as for drawing button images)
     * @param id the id of the component.
     * @return the component with the specified id, or null if the id is out of range.
     */
    public static ReactorItem getDefaultComponent(int id) {
        if (id >= 0 && id < ITEMS.length) {
            return ITEMS[id];
        }
        return null;
    }
    
    /**
     * Gets a default instances of the specified component (such as for drawing button images)
     * @param name the name of the component.
     * @return the component with the specified name, or null if the name is not found.
     */
    public static ReactorItem getDefaultComponent(String name) {
        if (name != null) {
            return ITEM_MAP.get(name);
        }
        return null;
    }
    
    /**
     * Creates a new instance of the specified component.
     * @param id the id of the component to create.
     * @return a new instance of the specified component, or null if the id is out of range.
     */
    public static ReactorItem createComponent(int id) {
        if (id >= 0 && id < ITEMS.length) {
            return copy(ITEMS[id]);
        }
        return null;
    }
    
    /**
     * Creates a new instance of the specified component.
     * @param name the name of the component to create.
     * @return a new instance of the specified component, or null if the name is not found.
     */
    public static ReactorItem createComponent(String name) {
        if (name != null) {
            return copy(ITEM_MAP.get(name));
        }
        return null;
    }
       
    /**
     * Get the number of defined components.
     * @return the number of defined components.
     */
    public static int getComponentCount() {
        return ITEMS.length;
    }
    
}
