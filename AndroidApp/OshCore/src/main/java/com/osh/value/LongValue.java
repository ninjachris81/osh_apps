package com.osh.value;

public class LongValue extends ValueBase<LongValue, Long> {

	@Override
	protected Long _updateValue(Object newValue) {
		if (newValue instanceof Long) {
			return (Long) newValue;
		} else if (newValue instanceof Number) {
			return ((Number) newValue).longValue();
		} else if (newValue instanceof String) {
			return Long.parseLong((String) newValue);
		} else {
			return null;
		}
	}

	public LongValue(ValueGroup valueGroup, String id, ValueType valueType) {
		super(valueGroup, id, valueType);
	}

	public LongValue() {
	}

}
