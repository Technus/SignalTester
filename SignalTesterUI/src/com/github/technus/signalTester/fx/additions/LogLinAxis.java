package com.github.technus.signalTester.fx.additions;

import com.sun.javafx.charts.ChartLayoutAnimator;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogLinAxis extends ValueAxis<Number> {
    private static double EPSILON= Math.pow(10,-14);
    private Object currentAnimationID;
    private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
    private final StringProperty currentFormatterProperty = new SimpleStringProperty(this, "currentFormatter", "");
    private final DefaultFormatter defaultFormatter = new DefaultFormatter(this);

    //region -------------- PUBLIC PROPERTIES --------------------------------------------------------------------------------

    /** Base of the logarithm, set to 1,<=0,-1, for linear */
    private DoubleProperty logBase = new DoublePropertyBase(10) {
        @Override
        protected void invalidated() {
            invalidateRange();
            requestAxisLayout();
        }

        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "logBase";
        }

        @Override
        public void set(double newValue) {
            if(newValue==0 || newValue==-1 || newValue==1){
                super.set(-10);
            }else{
                if(newValue>0){
                    log10logBase.set(Math.log10(newValue));
                }
                super.set(newValue);
            }
        }
    };
    public boolean isUsingLogBase(double base){ return base!=1 && base>0; }
    public boolean isUsingLinBase(double base){ return base==1 || base<=0; }
    public final boolean isUsingLogBase(){ return isUsingLogBase(logBase.getValue()); }
    public final boolean isUsingLinBase(){ return isUsingLinBase(logBase.getValue()); }
    public final double logBase() { return logBase.getValue(); }
    public final void setLogBase(double value) {logBase.set(value);}
    public final void setUsingLogBase(boolean usingLogBase){
        if(usingLogBase!=isUsingLogBase()){
            logBase.set(-logBase.getValue());
        }
    }
    public final DoubleProperty logBaseProperty() { return logBase; }

    private ReadOnlyDoubleWrapper log10logBase =new ReadOnlyDoubleWrapper(this,"log10logBase",1);

    /** When true zero is always included in the visible range. This only has effect if auto-ranging is on. */
    private BooleanProperty linForceZeroInRange = new BooleanPropertyBase(true) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "linForceZeroInRange";
        }
    };
    public final boolean isLinForceZeroInRange() { return linForceZeroInRange.getValue(); }
    public final void setLinForceZeroInRange(boolean value) { linForceZeroInRange.set(value); }
    public final BooleanProperty linForceZeroInRangeProperty() { return linForceZeroInRange; }

    /** When true zero is always included in the visible range. This only has effect if auto-ranging is on. */
    private BooleanPropertyBase forceZeroInRange = new BooleanPropertyBase() {
        @Override protected void invalidated() {
            // This will effect layout if we are auto ranging
            if(isAutoRanging()) {
                requestAxisLayout();
                invalidateRange();
            }
        }

        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "forceZeroInRange";
        }
    };
    public final boolean isForceZeroInRange() { return forceZeroInRange.getValue(); }
    public final void setForceZeroInRange(boolean value) { forceZeroInRange.set(value); }
    public final BooleanProperty forceZeroInRangeProperty() { return forceZeroInRange; }
    {
        forceZeroInRangeProperty().bind(new BooleanBinding() {
            {
                bind(logBaseProperty(),linForceZeroInRangeProperty());
            }

            @Override
            protected boolean computeValue() {
                return isUsingLinBase() && linForceZeroInRangeProperty().get();
            }
        });
    }

    /**  The value between each major tick mark in data units. This is automatically set if we are auto-ranging. */
    private DoubleProperty logTickCount = new DoublePropertyBase(10) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "logTickCount";
        }
    };
    public final double getLogTickCount() { return logTickCount.get(); }
    public final void setLogTickCount(double value) { logTickCount.set(value); }
    public final DoubleProperty logTickCountProperty() { return logTickCount; }

    /**  The value between each major tick mark in data units. This is automatically set if we are auto-ranging. */
    private DoubleProperty linTickUnit = new DoublePropertyBase(10) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "linTickUnit";
        }
    };
    public final double getLinTickUnit() { return linTickUnit.get(); }
    public final void setLinTickUnit(double value) { linTickUnit.set(value); }
    public final DoubleProperty linTickUnitProperty() { return linTickUnit; }

    /**  The value between each major tick mark in data units. This is automatically set if we are auto-ranging. */
    private DoubleProperty tickUnit = new StyleableDoubleProperty() {
        @Override
        protected void invalidated() {
            if(!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public CssMetaData<LogLinAxis,Number> getCssMetaData() {
            return StyleableProperties.TICK_UNIT;
        }

        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "tickUnit";
        }
    };
    public final double getTickUnit() { return tickUnit.get(); }
    public final void setTickUnit(double value) { tickUnit.set(value); }
    final DoubleProperty tickUnitProperty() { return tickUnit; }
    {
        tickUnitProperty().bind(new DoubleBinding() {
            {
                bind(logBaseProperty(),linTickUnitProperty(),logTickCountProperty());
            }

            @Override
            protected double computeValue() {
                return isUsingLinBase()?linTickUnitProperty().get(): logTickCountProperty().get();
            }
        });
    }

    /** The value for the upper bound of this axis, ie max value. This is automatically set if auto ranging is on. */
    private DoubleProperty linUpperBound = new DoublePropertyBase(100) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "linUpperBound";
        }
    };
    public final double getLinUpperBound() { return linUpperBound.get(); }
    public final void setLinUpperBound(double value) { linUpperBound.set(value); }
    public final DoubleProperty linUpperBoundProperty() { return linUpperBound; }

    /** The value for the upper bound of this axis, ie max value. This is automatically set if auto ranging is on. */
    private DoubleProperty logUpperBound = new DoublePropertyBase(25000) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "logUpperBound";
        }

        @Override
        public void set(double newValue) {
            if(newValue<=0 || Double.isInfinite(newValue) || Double.isNaN(newValue)){
                newValue=Double.MAX_VALUE;
            }
            super.set(newValue);
        }
    };
    public final double getLogUpperBound() { return logUpperBound.get(); }
    public final void setLogUpperBound(double value) { logUpperBound.set(value); }
    public final DoubleProperty logUpperBoundProperty() { return logUpperBound; }
    {
        upperBoundProperty().bind(new DoubleBinding() {
            {
                bind(logBaseProperty(),linUpperBoundProperty(),logUpperBoundProperty());
            }

            @Override
            protected double computeValue() {
                return isUsingLinBase()?linUpperBoundProperty().get():logUpperBoundProperty().get();
            }
        });
    }

    /** The value for the lower bound of this axis, ie min value. This is automatically set if auto ranging is on. */
    private DoubleProperty linLowerBound = new DoublePropertyBase(0) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "linLowerBound";
        }
    };
    public final double getLinLowerBound() { return linLowerBound.get(); }
    public final void setLinLowerBound(double value) { linLowerBound.set(value); }
    public final DoubleProperty linLowerBoundProperty() { return linLowerBound; }

    /** The value for the lower bound of this axis, ie min value. This is automatically set if auto ranging is on. */
    private DoubleProperty logLowerBound = new DoublePropertyBase(25) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "logLowerBound";
        }

        @Override
        public void set(double newValue) {
            if(newValue<=0 || Double.isInfinite(newValue) || Double.isNaN(newValue)){
                newValue=Double.MIN_NORMAL;
            }
            super.set(newValue);
        }
    };
    public final double getLogLowerBound() { return logLowerBound.get(); }
    public final void setLogLowerBound(double value) { logLowerBound.set(value); }
    public final DoubleProperty logLowerBoundProperty() { return logLowerBound; }
    {
        lowerBoundProperty().bind(new DoubleBinding() {
            {
                bind(logBaseProperty(),linLowerBoundProperty(),logLowerBoundProperty());
            }

            @Override
            protected double computeValue() {
                return isUsingLinBase()?linLowerBoundProperty().get():logLowerBoundProperty().get();
            }
        });
    }

    /**
     * The number of minor tick divisions to be displayed between each major tick mark.
     * The number of actual minor tick marks will be one less than this.
     */
    private IntegerProperty logMinorTickCount = new IntegerPropertyBase(10) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "logMinorTickCount";
        }
    };
    public final int getLogMinorTickCount() { return logMinorTickCount.get(); }
    public final void setLogMinorTickCount(int value) { logMinorTickCount.set(value); }
    public final IntegerProperty logMinorTickCountProperty() { return logMinorTickCount; }

    /**
     * The number of minor tick divisions to be displayed between each major tick mark.
     * The number of actual minor tick marks will be one less than this.
     */
    private IntegerProperty linMinorTickCount = new IntegerPropertyBase(10) {
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "linMinorTickCount";
        }
    };
    public final int getLinMinorTickCount() { return linMinorTickCount.get(); }
    public final void setLinMinorTickCount(int value) { linMinorTickCount.set(value); }
    public final IntegerProperty linMinorTickCountProperty() { return linMinorTickCount; }
    {
        minorTickCountProperty().bind(new IntegerBinding() {
            {
                bind(logBaseProperty(),logMinorTickCountProperty(),linMinorTickCountProperty());
            }

            @Override
            protected int computeValue() {
                return isUsingLinBase()?linMinorTickCountProperty().get():logMinorTickCountProperty().get();
            }
        });
    }

    //endregion

    private final DoubleProperty currentUpperBound = new SimpleDoubleProperty(this, "currentUpperBound");
    private final DoubleProperty currentUpperLogBound = new SimpleDoubleProperty(this, "currentUpperLogBound");
    {
        currentUpperLogBound.bind(new DoubleBinding() {
            {
                bind(currentUpperBound,logBaseProperty(),upperBoundProperty());
            }

            @Override
            protected double computeValue() {
                return Math.log10(currentUpperBound.get())/log10logBase.get()+EPSILON;
            }
        });
    }
    private final DoubleProperty currentLowerLogBound = new SimpleDoubleProperty(this, "currentLowerLogBound");
    {
        currentLowerLogBound.bind(new DoubleBinding() {
            {
                bind(currentLowerBound,logBaseProperty(),lowerBoundProperty());
            }

            @Override
            protected double computeValue() {
                return Math.log10(currentLowerBound.get())/log10logBase.get()-EPSILON;
            }
        });
    }

    private double offset=0;
    private final ReadOnlyDoubleWrapper logScale=new ReadOnlyDoubleWrapper(LogLinAxis.this,"logScale");
    {
        InvalidationListener listener= (observable) -> {
            if(isUsingLogBase()) {
                final Side side = getEffectiveSide();
                final double upperBound = currentUpperLogBound.get();
                final double lowerBound = currentLowerLogBound.get();
                if (side.isVertical()) {
                    final double length = getHeight();
                    offset = length;
                    final double scale = ((upperBound - lowerBound) == 0) ? -length : -(length / (upperBound - lowerBound));
                    setScale(scale);
                    logScale.set(scale);
                } else { // HORIZONTAL
                    final double length = getWidth();
                    offset = 0;
                    final double scale = ((upperBound - lowerBound) == 0) ? length : length / (upperBound - lowerBound);
                    setScale(scale);
                    logScale.set(scale);
                }
            }
        };
        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
        currentLowerLogBound.addListener(listener);
        currentUpperLogBound.addListener(listener);
        sideProperty().addListener(listener);
    }

    private final ComplexDoublePropertyBase scaleController =new ComplexDoublePropertyBase(getScale()){
        @Override
        public Object getBean() {
            return LogLinAxis.this;
        }

        @Override
        public String getName() {
            return "scaleController";
        }

        @Override
        public void set(double newValue) {
            setScale(newValue);
        }
    };
    {
        scaleProperty().addListener((observable, oldValue, newValue) -> scaleController.setRawValue(newValue.doubleValue()));
    }


    //region -------------- CONSTRUCTORS -------------------------------------------------------------------------------------

    /**
     * Create a auto-ranging LogarithmicAxis2
     */
    public LogLinAxis() {}

    /**
     * Create a non-auto-ranging LogarithmicAxis2 with the given upper bound, lower bound and tick unit
     *
     * @param lowerBound The lower bound for this axis, ie min plottable value
     * @param upperBound The upper bound for this axis, ie max plottable value
     * @param tickUnit The tick unit, ie space between tickmarks
     */
    public LogLinAxis(double lowerBound, double upperBound, double tickUnit) {
        super(lowerBound, upperBound);
        setLogLowerBound(lowerBound);
        setLogUpperBound(upperBound);
        setTickUnit(tickUnit);
        setLogTickCount(tickUnit);
    }

    /**
     * Create a non-auto-ranging LogarithmicAxis2 with the given upper bound, lower bound and tick unit
     *
     * @param axisLabel The name to display for this axis
     * @param lowerBound The lower bound for this axis, ie min plottable value
     * @param upperBound The upper bound for this axis, ie max plottable value
     * @param tickUnit The tick unit, ie space between tickmarks
     */
    public LogLinAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        super(lowerBound, upperBound);
        setLogLowerBound(lowerBound);
        setLogUpperBound(upperBound);
        setTickUnit(tickUnit);
        setLogTickCount(tickUnit);
        setLabel(axisLabel);
    }


    private static double EPSILON_FORMAT=Math.pow(10,-6);

    {
        setTickLabelFormatter(new StringConverter<Number>() {
            DecimalFormat format = new DecimalFormat("0.00E0");
            DecimalFormat shorter=new DecimalFormat("");
            {
                DecimalFormatSymbols decimalFormatSymbols=new DecimalFormatSymbols();
                decimalFormatSymbols.setGroupingSeparator(' ');

                format.setMaximumFractionDigits(3);
                format.setMinimumIntegerDigits(1);
                format.setRoundingMode(RoundingMode.HALF_UP);
                format.setDecimalFormatSymbols(decimalFormatSymbols);

                shorter.setMaximumFractionDigits(5);
                shorter.setMinimumIntegerDigits(1);
                shorter.setRoundingMode(RoundingMode.HALF_UP);
                shorter.setDecimalFormatSymbols(decimalFormatSymbols);
            }

            @Override
            public String toString(Number object) {
                if((object.intValue()<object.doubleValue()+EPSILON_FORMAT && object.intValue()>object.doubleValue()-EPSILON_FORMAT &&
                        object.doubleValue()<=100_000 && object.doubleValue()>=-100_000) ||
                        (object.doubleValue()<=1 && object.doubleValue()>=-1 && (object.doubleValue()<-0.001 || object.doubleValue()>0.001))){
                    return shorter.format(object);
                }
                return format.format(object);
            }

            @Override
            public Number fromString(String string) {
                try{
                    return (Number) format.parseObject(string);
                }catch (ParseException e){
                    return Double.parseDouble(string);
                }
            }
        });
    }

    //endregion

    // -------------- PROTECTED METHODS --------------------------------------------------------------------------------

    /**
     * Get the string label name for a tick mark with the given value
     *
     * @param value The value to format into a tick label string
     * @return A formatted string for the given value
     */
    @Override protected String getTickMarkLabel(Number value) {
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) formatter = defaultFormatter;
        return formatter.toString(value);
    }

    /**
     * Called to get the current axis range.
     *
     * @return A range object that can be passed to setRange() and calculateTickValues()
     */
    @Override protected Object getRange() {
        return new Object[]{
                getLowerBound(),
                getUpperBound(),
                getTickUnit(),
                getMinorTickCount(),
                getScale(),
                currentFormatterProperty.get()
        };
    }

    @Override
    protected void layoutChildren() {
        if(!isAutoRanging()) {
            currentUpperBound.set(getUpperBound());
        }
        super.layoutChildren();

    }

    /**
     * Called to set the current axis range to the given range. If isAnimating() is true then this method should
     * animate the range to the new range.
     *
     * @param range A range object returned from autoRange()
     * @param animate If true animate the change in range
     */
    @Override protected void setRange(Object range, boolean animate) {
        final Object[] rangeProps = (Object[]) range;
        final double lowerBound = (Double)rangeProps[0];
        final double upperBound = (Double)rangeProps[1];
        final double tick = (Double)rangeProps[2];
        final int minorTick = (Integer) rangeProps[3];
        final double scale = (Double)rangeProps[4];
        final String formatter = (String)rangeProps[5];
        currentFormatterProperty.set(formatter);
        final double oldLowerBound = getLowerBound();
        final double oldUpperBound = getUpperBound();
        if(isUsingLinBase()){
            setLinLowerBound(lowerBound);
            setLinUpperBound(upperBound);
            setLinTickUnit(tick);
            setLinMinorTickCount(minorTick);
            if(animate) {
                animator.stop(currentAnimationID);
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(currentLowerBound, oldLowerBound),
                                new KeyValue(scaleController, getScale())
                        ),
                        new KeyFrame(Duration.millis(700),
                                new KeyValue(currentLowerBound, lowerBound),
                                new KeyValue(scaleController, scale)
                        )
                );
                currentAnimationID = animator.animate(timeline);
            } else {
                currentLowerBound.set(lowerBound);
                scaleController.set(scale);
            }
        }else {
            setLogLowerBound(lowerBound);
            setLogUpperBound(upperBound);
            setLogTickCount(tick);
            setLogMinorTickCount(minorTick);
            if(animate) {
                animator.stop(currentAnimationID);
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(currentLowerBound, oldLowerBound),
                                new KeyValue(currentUpperBound, oldUpperBound)
                        ),
                        new KeyFrame(Duration.millis(700),
                                new KeyValue(currentLowerBound, lowerBound),
                                new KeyValue(currentUpperBound, upperBound)
                        )
                );
                timeline.setOnFinished(event -> {
                    getLowerBound();
                    getUpperBound();
                });
                currentAnimationID = animator.animate(timeline);
            } else {
                currentLowerBound.set(lowerBound);
                currentUpperBound.set(upperBound);
            }
        }
    }

    /**
     * Calculate a list of all the data values for each tick mark in range
     *
     * @param length The length of the axis in display units
     * @param range A range object returned from autoRange()
     * @return A list of tick marks that fit along the axis if it was the given length
     */
    @Override protected List<Number> calculateTickValues(double length, Object range) {
        final Object[] rangeProps = (Object[]) range;
        final double lowerBound = (Double)rangeProps[0];
        final double upperBound = (Double)rangeProps[1];
        final double tickUnit = (Double)rangeProps[2];

        List<Number> tickValues = new ArrayList<>();
        if (lowerBound == upperBound) {
            tickValues.add(lowerBound);
        } else if (tickUnit <= 0) {
            tickValues.add(lowerBound);
            tickValues.add(upperBound);
        } else if (tickUnit > 0) {
            tickValues.add(lowerBound);
            if(isUsingLinBase()) {
                if (((upperBound - lowerBound) / tickUnit) > 2000) {
                    // This is a ridiculous amount of major tick marks, something has probably gone wrong
                    System.err.println("Warning we tried to create more than 2000 major tick marks on a LogLinAxis. " +
                            "Lower Bound=" + lowerBound + ", Upper Bound=" + upperBound + ", Tick Unit=" + tickUnit);
                } else {
                    if (lowerBound + tickUnit < upperBound) {
                        // If tickUnit is integer, start with the nearest integer
                        double major = Math.rint(tickUnit) == tickUnit ? Math.ceil(lowerBound) : lowerBound + tickUnit;
                        int count = (int) Math.ceil((upperBound - major) / tickUnit);
                        for (int i = 0; major < upperBound && i < count; major += tickUnit, i++) {
                            if (!tickValues.contains(major)) {
                                tickValues.add(major);
                            }
                        }
                    }
                }
            }else {//todo maybe for <1 log base
                //using amount of ticks per 1 whole log increase
                final double logUpperBound=Math.log10(upperBound)/log10logBase.get();
                final double logLowerBound=Math.log10(lowerBound)/log10logBase.get();
                final int tickUnitInt=(int) Math.ceil(tickUnit);
                if (((logUpperBound - logLowerBound) * tickUnitInt) > 2000) {
                    // This is a ridiculous amount of major tick marks, something has probably gone wrong
                    System.err.println("Warning we tried to create more than 2000 major tick marks on a LogLinAxis. " +
                            "Lower Bound=" + lowerBound + ", Upper Bound=" + upperBound + ", Tick Unit=" + tickUnit);
                } else {
                    final int logLower=(int)Math.floor(logLowerBound);
                    final int logUpper=(int)Math.ceil(logUpperBound);
                    calculateTicks(tickValues, lowerBound, upperBound, tickUnitInt, logLower, logUpper);
                }
            }
            tickValues.add(upperBound);
        }
        return tickValues;
    }

    /**
     * Calculate a list of the data values for every minor tick mark
     *
     * @return List of data values where to draw minor tick marks
     */
    protected List<Number> calculateMinorTickMarks() {
        final List<Number> minorTickMarks = new ArrayList<>();
        final double lowerBound = getLowerBound();
        final double upperBound = getUpperBound();
        final double tickUnit = getTickUnit();
        final int minorTickCount = getMinorTickCount();
        if (tickUnit > 0 && minorTickCount>1) {
            if(isUsingLinBase()) {
                final double minorUnit = tickUnit/Math.max(1, getMinorTickCount());
                if (((upperBound - lowerBound) / minorUnit) > 10000) {
                    // This is a ridiculous amount of major tick marks, something has probably gone wrong
                    System.err.println("Warning we tried to create more than 10000 minor tick marks on a LogLinAxis. " +
                            "Lower Bound=" + getLowerBound() + ", Upper Bound=" + getUpperBound() + ", Tick Unit=" + tickUnit+ ", Minor Tick Count=" + minorTickCount);
                    return minorTickMarks;
                }
                final boolean tickUnitIsInteger = Math.rint(tickUnit) == tickUnit;
                if (tickUnitIsInteger) {
                    double minor = Math.floor(lowerBound) + minorUnit;
                    int count = (int) Math.ceil((Math.ceil(lowerBound) - minor) / minorUnit);
                    for (int i = 0; minor < Math.ceil(lowerBound) && i < count; minor += minorUnit, i++) {
                        if (minor > lowerBound) {
                            minorTickMarks.add(minor);
                        }
                    }
                }
                double major = tickUnitIsInteger ? Math.ceil(lowerBound) : lowerBound;
                int count = (int) Math.ceil((upperBound - major) / tickUnit);
                for (int i = 0; major < upperBound && i < count; major += tickUnit, i++) {
                    final double next = Math.min(major + tickUnit, upperBound);
                    double minor = major + minorUnit;
                    int minorCount = (int) Math.ceil((next - minor) / minorUnit);
                    for (int j = 0; minor < next && j < minorCount; minor += minorUnit, j++) {
                        minorTickMarks.add(minor);
                    }
                }
            }else{
                final double logUpperBound=Math.log10(upperBound)/log10logBase.get();
                final double logLowerBound=Math.log10(lowerBound)/log10logBase.get();
                final int tickUnitInt=(int) Math.ceil(tickUnit);
                final int minorTickUnitInt=tickUnitInt*minorTickCount;
                if (((logUpperBound - logLowerBound) * tickUnit * (minorTickCount-1)) > 10000) {
                    // This is a ridiculous amount of major tick marks, something has probably gone wrong
                    System.err.println("Warning we tried to create more than 10000 minor tick marks on a LogLinAxis. " +
                            "Lower Bound=" + getLowerBound() + ", Upper Bound=" + getUpperBound() + ", Tick Unit=" + tickUnit+ ", Minor Tick Count=" + minorTickCount);
                } else {
                    final int logBaseLower=(int)Math.floor(logLowerBound);
                    final int logBaseUpper=(int)Math.ceil(logUpperBound);
                    calculateTicks(minorTickMarks, lowerBound, upperBound, minorTickUnitInt, logBaseLower, logBaseUpper);
                }
            }
        }
        return minorTickMarks;
    }

    private void calculateTicks(List<Number> ticks, double lowerBound, double upperBound, int countPerBase, int logBaseLower, int logBaseUpper) {
        final double[] subDiv=new double[countPerBase-1];
        for(int i=0;i<subDiv.length;i++){
            subDiv[i]=(i+1)*logBase.get()/countPerBase;
        }
        for(int log=logBaseLower;log<=logBaseUpper;log++) {
            final double logTickBase=Math.pow(logBase.get(),log);
            for(int i=0;i<subDiv.length;i++){
                final double tickValue=logTickBase*subDiv[i];
                if(tickValue>lowerBound) {
                    if(tickValue>upperBound){
                        return;
                    }else{
                        ticks.add(tickValue);
                    }
                }
            }
        }
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
     *
     * @param value tick mark value
     * @param range range to use during calculations
     * @return size of tick mark label for given value
     */
    @Override protected Dimension2D measureTickMarkSize(Number value, Object range) {
        final Object[] rangeProps = (Object[]) range;
        final String formatter = (String)rangeProps[5];
        return measureTickMarkSize(value, getTickLabelRotation(), formatter);
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
     *
     * @param value     tick mark value
     * @param rotation  The text rotation
     * @param numFormatter The number formatter
     * @return size of tick mark label for given value
     */
    private Dimension2D measureTickMarkSize(Number value, double rotation, String numFormatter) {
        String labelText;
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) formatter = defaultFormatter;
        if(formatter instanceof DefaultFormatter) {
            labelText = ((DefaultFormatter)formatter).toString(value, numFormatter);
        } else {
            labelText = formatter.toString(value);
        }
        return measureTickMarkLabelSize(labelText, rotation);
    }

    private Side getEffectiveSide() {
        return getSide()==null?Side.BOTTOM:getSide();
    }

    /**
     * Called to set the upper and lower bound and anything else that needs to be auto-ranged
     *
     * @param minValue The min data value that needs to be plotted on this axis
     * @param maxValue The max data value that needs to be plotted on this axis
     * @param length The length of the axis in display coordinates
     * @param labelSize The approximate average size a label takes along the axis
     * @return The calculated range
     */
    @Override protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
        if(isUsingLogBase()){
            final double minRounded=Math.pow(logBase.get(),Math.floor(Math.log10(minValue)/log10logBase.get()));
            final double maxRounded=Math.pow(logBase.get(),Math.ceil(Math.log10(maxValue)/log10logBase.get()));
            return new Object[]{minRounded, maxRounded, logBase.get(),10, getScale(),""};
        }
        final Side side = getEffectiveSide();
        // check if we need to force zero into range
        if (isForceZeroInRange()) {
            if (maxValue < 0) {
                maxValue = 0;
            } else if (minValue > 0) {
                minValue = 0;
            }
        }
        // calculate the number of tick-marks we can fit in the given length of the axis
        int numOfTickMarks = (int)Math.floor(length/labelSize);
        // can never have less than 2 tick marks one for each end
        numOfTickMarks = Math.max(numOfTickMarks, 2);
        int minorTickCount = Math.max(getMinorTickCount(), 1);

        double range = maxValue-minValue;

        if (range != 0 && range/(numOfTickMarks*minorTickCount) <= Math.ulp(minValue)) {
            range = 0;
        }
        // pad min and max by 2%, checking if the range is zero
        final double paddedRange = (range == 0)
                ? minValue == 0 ? 2 : Math.abs(minValue)*0.02
                : Math.abs(range)*1.02;
        final double padding = (paddedRange - range) / 2;
        // if min and max are not zero then add padding to them
        double paddedMin = minValue - padding;
        double paddedMax = maxValue + padding;
        // check padding has not pushed min or max over zero line
        if ((paddedMin < 0 && minValue >= 0) || (paddedMin > 0 && minValue <= 0)) {
            // padding pushed min above or below zero so clamp to 0
            paddedMin = 0;
        }
        if ((paddedMax < 0 && maxValue >= 0) || (paddedMax > 0 && maxValue <= 0)) {
            // padding pushed min above or below zero so clamp to 0
            paddedMax = 0;
        }
        //todo
        // calculate tick unit for the number of ticks can have in the given data range
        double tickUnit = paddedRange/(double)numOfTickMarks;
        // search for the best tick unit that fits
        double tickUnitRounded = 0;
        double minRounded = 0;
        double maxRounded = 0;
        int count = 0;
        double reqLength = Double.MAX_VALUE;
        String formatter = "0.00000000";
        // loop till we find a set of ticks that fit length and result in a total of less than 20 tick marks
        while (reqLength > length || count > 20) {
            int exp = (int)Math.floor(Math.log10(tickUnit));
            final double mant = tickUnit / Math.pow(10, exp);
            double ratio = mant;
            if (mant > 5d) {
                exp++;
                ratio = 1;
            } else if (mant > 1d) {
                ratio = mant > 2.5 ? 5 : 2.5;
            }
            if (exp > 1) {
                formatter = "#,##0";
            } else if (exp == 1) {
                formatter = "0";
            } else {
                final boolean ratioHasFrac = Math.rint(ratio) != ratio;
                final StringBuilder formatterB = new StringBuilder("0");
                int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);
                if (n > 0) formatterB.append(".");
                for (int i = 0; i < n; ++i) {
                    formatterB.append("0");
                }
                formatter = formatterB.toString();

            }
            tickUnitRounded = ratio * Math.pow(10, exp);
            // move min and max to nearest tick mark
            minRounded = Math.floor(paddedMin / tickUnitRounded) * tickUnitRounded;
            maxRounded = Math.ceil(paddedMax / tickUnitRounded) * tickUnitRounded;
            // calculate the required length to display the chosen tick marks for real, this will handle if there are
            // huge numbers involved etc or special formatting of the tick mark label text
            double maxReqTickGap = 0;
            double last = 0;
            count = (int)Math.ceil((maxRounded - minRounded)/tickUnitRounded);
            double major = minRounded;
            for (int i = 0; major <= maxRounded && i < count; major += tickUnitRounded, i++)  {
                Dimension2D markSize = measureTickMarkSize(major, getTickLabelRotation(), formatter);
                double size = side.isVertical() ? markSize.getHeight() : markSize.getWidth();
                if (i == 0) { // first
                    last = size/2;
                } else {
                    maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
                }
            }
            reqLength = (count-1) * maxReqTickGap;
            tickUnit = tickUnitRounded;

            // fix for RT-35600 where a massive tick unit was being selected
            // unnecessarily. There is probably a better solution, but this works
            // well enough for now.
            if (numOfTickMarks == 2 && reqLength > length) {
                break;
            }
            if (reqLength > length || count > 20) tickUnit *= 2; // This is just for the while loop, if there are still too many ticks
        }
        // calculate new scale
        final double newScale = calculateNewScale(length, minRounded, maxRounded);
        // return new range
        return new Object[]{minRounded, maxRounded, tickUnitRounded,minorTickCount, newScale, formatter};
    }

    // -------------- STYLESHEET HANDLING ------------------------------------------------------------------------------

    /** @treatAsPrivate implementation detail */
    private static class StyleableProperties {
        private static final CssMetaData<LogLinAxis,Number> TICK_UNIT =
                new CssMetaData<LogLinAxis,Number>("-fx-tick-unit",
                        SizeConverter.getInstance(), 5.0) {

                    @Override
                    public boolean isSettable(LogLinAxis n) {
                        return n.logTickCount == null || !n.logTickCount.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(LogLinAxis n) {
                        return (StyleableProperty<Number>) n.tickUnitProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(ValueAxis.getClassCssMetaData());
            styleables.add(TICK_UNIT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     * @since JavaFX 8.0
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     * @since JavaFX 8.0
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    // -------------- INNER CLASSES ------------------------------------------------------------------------------------

    /**
     * Default number formatter for LogarithmicAxis2, this stays in sync with auto-ranging and formats values appropriately.
     * You can wrap this formatter to add prefixes or suffixes;
     * @since JavaFX 2.0
     */
    public static class DefaultFormatter extends StringConverter<Number> {
        private DecimalFormat formatter;
        private String prefix = null;
        private String suffix = null;

        /**
         * Construct a DefaultFormatter for the given LogarithmicAxis2
         *
         * @param axis The axis to format tick marks for
         */
        public DefaultFormatter(final LogLinAxis axis) {
            DecimalFormatSymbols symbols=new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            setFormatter(axis, symbols);
            final ChangeListener<Object> axisListener = (observable, oldValue, newValue) -> setFormatter(axis, symbols);
            axis.currentFormatterProperty.addListener(axisListener);
            axis.autoRangingProperty().addListener(axisListener);
        }

        private void setFormatter(LogLinAxis axis, DecimalFormatSymbols symbols) {
            formatter = axis.isAutoRanging()? new DecimalFormat(axis.currentFormatterProperty.get(),symbols) : new DecimalFormat("",symbols);
            formatter.setRoundingMode(RoundingMode.HALF_UP);
            formatter.setMaximumFractionDigits(3);
            formatter.setMinimumIntegerDigits(1);
        }

        /**
         * Construct a DefaultFormatter for the given LogarithmicAxis2 with a prefix and/or suffix.
         *
         * @param axis The axis to format tick marks for
         * @param prefix The prefix to append to the start of formatted number, can be null if not needed
         * @param suffix The suffix to append to the end of formatted number, can be null if not needed
         */
        public DefaultFormatter(LogLinAxis axis, String prefix, String suffix) {
            this(axis);
            this.prefix = prefix;
            this.suffix = suffix;
        }

        /**
         * Converts the object provided into its string form.
         * Format of the returned string is defined by this converter.
         * @return a string representation of the object passed in.
         * @see StringConverter#toString
         */
        @Override public String toString(Number object) {
            return toString(object, formatter);
        }

        private String toString(Number object, String numFormatter) {
            if (numFormatter == null || numFormatter.isEmpty()) {
                return toString(object, formatter);
            } else {
                return toString(object, new DecimalFormat(numFormatter));
            }
        }

        private static final StringBuilder builder=new StringBuilder();

        private String toString(Number object, DecimalFormat formatter) {
            builder.setLength(0);
            if(prefix!=null){
                builder.append(prefix);
            }
            String number=formatter.format(object);
            //if(object.doubleValue()<3 || number.length()>6){
            //    builder.append();
            //}else {
                builder.append(number);
            //}
            if(suffix!=null){
                builder.append(suffix);
            }
            return builder.toString();
        }

        /**
         * Converts the string provided into a Number defined by the this converter.
         * Format of the string and type of the resulting object is defined by this converter.
         * @return a Number representation of the string passed in.
         * @see StringConverter#toString
         */
        @Override public Number fromString(String string) {
            try {
                int prefixLength = (prefix == null)? 0: prefix.length();
                int suffixLength = (suffix == null)? 0: suffix.length();
                return formatter.parse(string.substring(prefixLength, string.length() - suffixLength));
            } catch (ParseException e) {
                return null;
            }
        }
    }

    //pixel relative to value?
    @Override
    public Number getValueForDisplay(double displayPosition) {
        if(isUsingLinBase()){
            return super.getValueForDisplay(displayPosition);
        }else {
            //return toRealValue(Math.pow(logBase.get(), (displayPosition - offset) / logScale.get() + currentLowerBound.get()));
            return toRealValue(Math.pow(logBase.get(), (displayPosition - offset) / logScale.get() + currentLowerLogBound.get()));
        }
    }

    //value to pixel relative?
    @Override
    public double getDisplayPosition(Number value) {
        System.out.println("currentLowerLogBound2 = " + currentLowerLogBound.get());
        if(isUsingLinBase()){
            return super.getDisplayPosition(value);
        }else {
            //return offset + (Math.log10(value.doubleValue())/log10logBase.get()-currentLowerLogBound.get())*logScale.get();
            return offset + (Math.log10(value.doubleValue())/log10logBase.get()-currentLowerLogBound.get())*logScale.get();
        }
    }
}