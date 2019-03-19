/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ic2ExpReactorPlanner;

import java.awt.HeadlessException;
import javax.swing.JOptionPane;

/**
 * Represents an IndustrialCraft2 Nuclear Reactor.
 * @author Brian McCloud
 */
public class Reactor {
    
    private final ReactorComponent[][] grid = new ReactorComponent[6][9];
    
    private double currentEUoutput = 0.0;
    
    private double currentHeat = 0.0;
    
    private double maxHeat = 10000.0;
    
    private double ventedHeat = 0.0;
    
    private boolean fluid = false;
    
    private char simulationType = 's';
    
    private boolean usingReactorCoolantInjectors = false;
    
    private static final int DEFAULT_ON_PULSE = 5000000;
    
    private int onPulse = DEFAULT_ON_PULSE;
    
    private static final int DEFAULT_OFF_PULSE = 0;
    
    private int offPulse = DEFAULT_OFF_PULSE;
    
    private static final int DEFAULT_SUSPEND_TEMP = 120000;
    
    private int suspendTemp = DEFAULT_SUSPEND_TEMP;
    
    private static final int DEFAULT_RESUME_TEMP = 120000;
    
    private int resumeTemp = DEFAULT_RESUME_TEMP;
    
    // maximum paramatter types for a reactor component (current initial heat, automation threshold, reactor pause
    private final int MAX_PARAM_TYPES = 3;
    
    public ReactorComponent getComponentAt(final int row, final int column) {
        if (row >= 0 && row < grid.length && column >= 0 && column < grid[row].length) {
            return grid[row][column];
        }
        return null;
    }
    
    public void setComponentAt(final int row, final int column, final ReactorComponent component) {
        if (row >= 0 && row < grid.length && column >= 0 && column < grid[row].length) {
            if (grid[row][column] != null) {
                grid[row][column].removeFromReactor();
            }
            grid[row][column] = component;
            if (component != null) {
                component.setRow(row);
                component.setColumn(column);
                component.setParent(this);
                component.addToReactor();
            }
        }
    }

    public void clearGrid() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] != null) {
                    grid[row][col].removeFromReactor();
                }
                grid[row][col] = null;
            }
        }
    }
    
    /**
     * @return the amount of EU output in the reactor tick just simulated.
     */
    public double getCurrentEUoutput() {
        return currentEUoutput;
    }

    /**
     * @return the current heat level of the reactor.
     */
    public double getCurrentHeat() {
        return currentHeat;
    }

    /**
     * @return the maximum heat of the reactor.
     */
    public double getMaxHeat() {
        return maxHeat;
    }
    
    /**
     * Adjust the maximum heat
     * @param adjustment the adjustment amount (negative values decrease the max heat).
     */
    public void adjustMaxHeat(final double adjustment) {
        maxHeat += adjustment;
    }

    /**
     * Set the current heat of the reactor.  Mainly to be used for simulating a pre-heated reactor, or for resetting to 0 for a new simulation.
     * @param currentHeat the heat to set
     */
    public void setCurrentHeat(final double currentHeat) {
        this.currentHeat = currentHeat;
    }
    
    /**
     * Adjusts the reactor's current heat by a specified amount
     * @param adjustment the adjustment amount.
     */
    public void adjustCurrentHeat(final double adjustment) {
        currentHeat += adjustment;
        if (currentHeat < 0.0) {
            currentHeat = 0.0;
        }
    }
    
    /**
     * add some EU output.
     * @param amount the amount of EU to output over 1 reactor tick (20 game ticks).
     */
    public void addEUOutput(final double amount) {
        currentEUoutput += amount;
    }
    
    /**
     * clears the EU output (presumably to start simulating a new reactor tick).
     */
    public void clearEUOutput() {
        currentEUoutput = 0.0;
    }
    
    /**
     * Gets a list of the materials needed to build the components.
     * @return a list of the materials needed to build the components.
     */
    public MaterialsList getMaterials() {
        MaterialsList result = new MaterialsList();
        for (int col = 0; col < grid[0].length; col++) {
            for (int row = 0; row < grid.length; row++) {
                if (grid[row][col] != null) {
                    result.add(grid[row][col].getMaterials());
                }
            }
        }
        return result;
    }

    public MaterialsList getComponentList() {
        MaterialsList result = new MaterialsList();
        for (int col = 0; col < grid[0].length; col++) {
            for (int row = 0; row < grid.length; row++) {
                if (grid[row][col] != null) {
                    result.add(ComponentFactory.getDisplayName(grid[row][col]));
                }
            }
        }
        return result;
    }
    
    /**
     * @return the amount of heat vented this reactor tick.
     */
    public double getVentedHeat() {
        return ventedHeat;
    }
    
    /**
     * Adds to the amount of heat vented this reactor tick, in case it is a new-style reactor with a pressure vessel and outputting heat to fluid instead of EU.
     * @param amount the amount to add.
     */
    public void ventHeat(final double amount) {
        ventedHeat += amount;
    }
    
    /**
     * Clears the amount of vented heat, in case a new reactor tick is starting.
     */
    public void clearVentedHeat() {
        ventedHeat = 0;
    }
    
    /**
     * Get a code that represents the component set, which can be passed between forum users, etc.
     * @return a code representing some ids for the components and arrangement.  Passing the same code to setCode() should re-create an identical reactor setup, even if other changes have happened in the meantime.
     */
    public String getCode() {
        StringBuilder result = new StringBuilder(108);
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                final ReactorComponent component = grid[row][col];
                final int id = ComponentFactory.getID(component);
                result.append(String.format("%02X", id)); //NOI18N
                if (component != null && (component.getInitialHeat() > 0 || component.automationThreshold != ComponentFactory.getDefaultComponent(id).automationThreshold 
                        || component.reactorPause != ComponentFactory.getDefaultComponent(id).reactorPause)) {
                    result.append("(");
                    if (component.getInitialHeat() > 0) {
                        result.append(String.format("h%s,", Integer.toString((int)component.getInitialHeat(), 36))); //NOI18N
                    }
                    if (component.automationThreshold != ComponentFactory.getDefaultComponent(id).automationThreshold) {
                        result.append(String.format("a%s,", Integer.toString(component.automationThreshold, 36))); //NOI18N
                    }
                    if (component.reactorPause != ComponentFactory.getDefaultComponent(id).reactorPause) {
                        result.append(String.format("p%s,", Integer.toString(component.reactorPause, 36))); //NOI18N
                    }
                    result.setLength(result.length() - 1); // remove the last comma, whichever parameter it came from.
                    result.append(")");
                }
            }
        }
        result.append('|');
        if (fluid) {
            result.append('f');
        } else {
            result.append('e');
        }
        result.append(simulationType);
        if (usingReactorCoolantInjectors) {
            result.append('i');
        } else {
            result.append('n');
        }
        if (currentHeat > 0) {
            result.append(Integer.toString((int)currentHeat, 36));
        }
        if (onPulse != DEFAULT_ON_PULSE) {
            result.append(String.format("|n%s", Integer.toString(onPulse, 36)));
        }
        if (offPulse != DEFAULT_OFF_PULSE) {
            result.append(String.format("|f%s", Integer.toString(offPulse, 36)));
        }
        if (suspendTemp != DEFAULT_SUSPEND_TEMP) {
            result.append(String.format("|s%s", Integer.toString(suspendTemp, 36)));
        }
        if (resumeTemp != DEFAULT_SUSPEND_TEMP) {
            result.append(String.format("|r%s", Integer.toString(resumeTemp, 36)));
        }
        return result.toString();
    }
    
    /**
     * Sets a code to configure the entire grid all at once.  Expects the code to have originally been output by getCode().
     * @param code the code of the reactor setup to use.
     */
    public void setCode(final String code) {
        int pos = 0;
        int[][] ids = new int[grid.length][grid[0].length];
        char[][][] paramTypes = new char[grid.length][grid[0].length][MAX_PARAM_TYPES];
        int[][][] params = new int[grid.length][grid[0].length][MAX_PARAM_TYPES];
        if (code.length() >= 108 && code.matches("[0-9A-Za-z(),|]+")) { //NOI18N
            try {
                for (int row = 0; row < grid.length; row++) {
                    for (int col = 0; col < grid[row].length; col++) {
                        ids[row][col] = Integer.parseInt(code.substring(pos, pos + 2), 16);
                        pos += 2;
                        int paramNum = 0;
                        if (pos + 1 < code.length() && code.charAt(pos) == '(') {
                            paramTypes[row][col][paramNum] = code.charAt(pos + 1);
                            int tempPos = pos + 2;
                            StringBuilder param = new StringBuilder(10);
                            while (tempPos < code.length() && code.charAt(tempPos) != ')') {
                                if (code.charAt(tempPos) == ',') {
                                    params[row][col][paramNum] = Integer.parseInt(param.toString(), 36);
                                    paramNum++;
                                    if (tempPos + 1 < code.length()) {
                                        tempPos++;
                                        paramTypes[row][col][paramNum] = code.charAt(tempPos);
                                    }
                                    param.setLength(0);
                                } else {
                                    param.append(code.charAt(tempPos));
                                }
                                tempPos++;
                            }
                            params[row][col][paramNum] = Integer.parseInt(param.toString(), 36);
                            pos = tempPos + 1;
                        }
                    }
                }
                for (int row = 0; row < grid.length; row++) {
                    for (int col = 0; col < grid[row].length; col++) {
                        final ReactorComponent component = ComponentFactory.createComponent(ids[row][col]);
                        for (int paramNum = 0; paramNum < MAX_PARAM_TYPES; paramNum++) {
                            switch (paramTypes[row][col][paramNum]) {
                                case 'h':
                                    component.setInitialHeat(params[row][col][paramNum]);
                                    break;
                                case 'a':
                                    component.automationThreshold = params[row][col][paramNum];
                                    break;
                                case 'p':
                                    component.reactorPause = params[row][col][paramNum];
                                    break;
                                default:
                                    break;
                            }
                        }
                        setComponentAt(row, col, component);
                    }
                }
                if (code.split("\\|").length > 1) {
                    String extraCode = code.split("\\|")[1];
                    switch (extraCode.charAt(0)) {
                        case 'f':
                            fluid = true;
                            break;
                        case 'e':
                            fluid = false;
                            break;
                        default:
                            break;
                    }
                    switch (extraCode.charAt(1)) {
                        case 's':
                            simulationType = 's';
                            break;
                        case 'p':
                            simulationType = 'p';
                            break;
                        case 'a':
                            simulationType = 'a';
                            break;
                        default:
                            break;
                    }
                    switch (extraCode.charAt(2)) {
                        case 'i':
                            usingReactorCoolantInjectors = true;
                            break;
                        case 'n':
                            usingReactorCoolantInjectors = false;
                            break;
                        default:
                            break;
                    }
                    if (extraCode.length() > 3) {
                        currentHeat = Integer.parseInt(extraCode.substring(3), 36);
                    } else {
                        currentHeat = 0;
                    }
                }
                if (code.split("\\|").length > 2) {
                    String[] moreCodes = code.split("\\|");
                    for (int i = 2; i < moreCodes.length; i++) {
                        switch (moreCodes[i].charAt(0)) {
                            case 'n':
                                onPulse = Integer.parseInt(moreCodes[i].substring(1), 36);
                                break;
                            case 'f':
                                offPulse = Integer.parseInt(moreCodes[i].substring(1), 36);
                                break;
                            case 's':
                                suspendTemp = Integer.parseInt(moreCodes[i].substring(1), 36);
                                break;
                            case 'r':
                                resumeTemp = Integer.parseInt(moreCodes[i].substring(1), 36);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } else {
            String tempCode = code;
            if (code.startsWith("http://www.talonfiremage.pwp.blueyonder.co.uk/v3/reactorplanner.html?")) { //NOI18N
                tempCode = code.replace("http://www.talonfiremage.pwp.blueyonder.co.uk/v3/reactorplanner.html?", ""); //NOI18N
            }
            if (tempCode.matches("[0-9a-z]+")) { //NOI18N
                // Possibly a code from Talonius's old planner
                handleTaloniusCode(tempCode);
            }
        }
    }

    private void handleTaloniusCode(String tempCode) throws HeadlessException {
        StringBuilder warnings = new StringBuilder(500);
        TaloniusDecoder decoder = new TaloniusDecoder(tempCode);
        // initial heat, ignored by new planner.
        decoder.readInt(10);
        // reactor grid
        for (int x = 8; x >= 0; x--) {
            for (int y = 5; y >= 0; y--) {
                int nextValue = decoder.readInt(7);
                
                // items are no longer stackable in IC2 reactors, but stack sizes from the planner code still need to be handled
                if (nextValue > 64) {
                    nextValue = decoder.readInt(7);
                }
                
                switch (nextValue) {
                    case 0:
                        setComponentAt(y, x, null);
                        break;
                    case 1:
                        setComponentAt(y, x, new FuelRodUranium());
                        break;
                    case 2:
                        setComponentAt(y, x, new DualFuelRodUranium());
                        break;
                    case 3:
                        setComponentAt(y, x, new QuadFuelRodUranium());
                        break;
                    case 4:
                        warnings.append(String.format(java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.DepletedIsotope"), y, x));
                        break;
                    case 5:
                        setComponentAt(y, x, new NeutronReflector());
                        break;
                    case 6:
                        setComponentAt(y, x, new ThickNeutronReflector());
                        break;
                    case 7:
                        setComponentAt(y, x, new HeatVent());
                        break;
                    case 8:
                        setComponentAt(y, x, new ReactorHeatVent());
                        break;
                    case 9:
                        setComponentAt(y, x, new OverclockedHeatVent());
                        break;
                    case 10:
                        setComponentAt(y, x, new AdvancedHeatVent());
                        break;
                    case 11:
                        setComponentAt(y, x, new ComponentHeatVent());
                        break;
                    case 12:
                        setComponentAt(y, x, new RshCondensator());
                        break;
                    case 13:
                        setComponentAt(y, x, new LzhCondensator());
                        break;
                    case 14:
                        setComponentAt(y, x, new HeatExchanger());
                        break;
                    case 15:
                        setComponentAt(y, x, new ReactorHeatExchanger());
                        break;
                    case 16:
                        setComponentAt(y, x, new ComponentHeatExchanger());
                        break;
                    case 17:
                        setComponentAt(y, x, new AdvancedHeatExchanger());
                        break;
                    case 18:
                        setComponentAt(y, x, new ReactorPlating());
                        break;
                    case 19:
                        setComponentAt(y, x, new HeatCapacityReactorPlating());
                        break;
                    case 20:
                        setComponentAt(y, x, new ContainmentReactorPlating());
                        break;
                    case 21:
                        setComponentAt(y, x, new CoolantCell10k());
                        break;
                    case 22:
                        setComponentAt(y, x, new CoolantCell30k());
                        break;
                    case 23:
                        setComponentAt(y, x, new CoolantCell60k());
                        break;
                    case 24:
                        warnings.append(String.format(java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.Heating"), y, x));
                        break;
                    case 32:
                        setComponentAt(y, x, new FuelRodThorium());
                        break;
                    case 33:
                        setComponentAt(y, x, new DualFuelRodThorium());
                        break;
                    case 34:
                        setComponentAt(y, x, new QuadFuelRodThorium());
                        break;
                    case 35:
                        warnings.append(String.format(java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.Plutonium"), y, x));
                        break;
                    case 36:
                        warnings.append(String.format(java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.DualPlutonium"), y, x));
                        break;
                    case 37:
                        warnings.append(String.format(java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.QuadPlutonium"), y, x));
                        break;
                    case 38:
                        setComponentAt(y, x, new IridiumNeutronReflector());
                        break;
                    case 39:
                        setComponentAt(y, x, new CoolantCell60kHelium());
                        break;
                    case 40:
                        setComponentAt(y, x, new CoolantCell180kHelium());
                        break;
                    case 41:
                        setComponentAt(y, x, new CoolantCell360kHelium());
                        break;
                    case 42:
                        setComponentAt(y, x, new CoolantCell60kNak());
                        break;
                    case 43:
                        setComponentAt(y, x, new CoolantCell180kNak());
                        break;
                    case 44:
                        setComponentAt(y, x, new CoolantCell360kNak());
                        break;
                    default:
                        warnings.append(String.format(java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.Unrecognized"), nextValue, y, x));
                        break;
                }
            }
        }
        if (warnings.length() > 0) {
            warnings.setLength(warnings.length() - 1);  // to remove last newline character
            JOptionPane.showMessageDialog(null, warnings, java.util.ResourceBundle.getBundle("Ic2ExpReactorPlanner/Bundle").getString("Warning.Title"), JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Checks whether the reactor is to simulate a fluid-style reactor, rather than a direct EU-output reactor.
     * @return true if this was set to be a fluid-style reactor, false if this was set to be direct EU-output reactor.
     */
    public boolean isFluid() {
        return fluid;
    }

    /**
     * Sets whether the reactor is to simulate a fluid-style reactor, rather than a direct EU-output reactor.
     * @param fluid true if this is to be a fluid-style reactor, false if this is to be direct EU-output reactor.
     */
    public void setFluid(final boolean fluid) {
        this.fluid = fluid;
    }
    
    /**
     * Checks whether the reactor is using Reactor Coolant Injectors (RCIs)
     * @return true if this reactor was set to use RCIs, false otherwise.
     */
    public boolean isUsingReactorCoolantInjectors() {
        return usingReactorCoolantInjectors;
    }
    
    /**
     * Sets whether the reactor is to use Reactor Coolant Injectors (RCIs)
     * @param usingReactorCoolantInjectors true if this reactor should use RCIs, false otherwise.
     */
    public void setUsingReactorCoolantInjectors(final boolean usingReactorCoolantInjectors) {
        this.usingReactorCoolantInjectors = usingReactorCoolantInjectors;
    }
    
    /**
     * Gets the character indicating the simulation type, which might have been read from a reactor code.
     * @return 
     */
    public char getSimulationType() {
        return simulationType;
    }
    
    /**
     * Sets the simulation type, so it can be stored in the reactor code.
     * 's' is for Simple Cycle
     * 'p' is for Pulsed Cycle
     * 'a' is for Automation Cycle
     * @param simulationType 
     */
    public void setSimulationType(final char simulationType) {
        this.simulationType = simulationType;
    }
    
    public int getOnPulse() {
        return onPulse;
    }
    
    public void setOnPulse(final int onPulse) {
        this.onPulse = onPulse;
    }
    
    public int getOffPulse() {
        return offPulse;
    }
    
    public void setOffPulse(final int offPulse) {
        this.offPulse = offPulse;
    }
    
    public int getSuspendTemp() {
        return suspendTemp;
    }
    
    public void setSuspendTemp(final int suspendTemp) {
        this.suspendTemp = suspendTemp;
    }
    
    public int getResumeTemp() {
        return resumeTemp;
    }
    
    public void setResumeTemp(final int resumeTemp) {
        this.resumeTemp = resumeTemp;
    }
    
}
