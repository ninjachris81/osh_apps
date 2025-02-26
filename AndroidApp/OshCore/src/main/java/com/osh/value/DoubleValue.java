package com.osh.value;

public class DoubleValue extends ValueBase<DoubleValue, Double> {

	@Override
	protected Double _updateValue(Object newValue) {
		if (newValue instanceof Double) {
			return (Double) newValue;
		} else if (newValue instanceof Number) {
			return ((Number) newValue).doubleValue();
		} else if (newValue instanceof String) {
			return Double.parseDouble((String) newValue);
		} else {
			return null;
		}
	}

	public DoubleValue(ValueGroup valueGroup, String id, ValueType valueType) {
		super(valueGroup, id, valueType);
	}

	public DoubleValue() {
	}

}
