package com.osh.value;

public class IntegerValue extends ValueBase<IntegerValue, Integer> {

	@Override
	protected Integer _updateValue(Object newValue) {
		if (newValue instanceof Integer) {
			return (Integer) newValue;
		} else if (newValue instanceof Number) {
			return ((Number) newValue).intValue();
		} else if (newValue instanceof String) {
			return Integer.parseInt((String) newValue);
		} else {
			return null;
		}
	}

	public IntegerValue(ValueGroup valueGroup, String id, ValueType valueType) {
		super(valueGroup, id, valueType);
	}

	public IntegerValue() {
	}

}
