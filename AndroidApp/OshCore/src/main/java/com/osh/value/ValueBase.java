package com.osh.value;

import com.osh.SerializableIdentifyable;
import com.osh.datamodel.ItemMetaInfo;
import com.osh.utils.IItemChangeListener;
import com.osh.utils.IObservableItem;
import com.osh.utils.IObservableListenerHolder;
import com.osh.utils.IObservableManager;
import com.osh.utils.ObservableManagerImpl;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class ValueBase<VALUE_TYPE extends ValueBase, NATIVE_TYPE> extends SerializableIdentifyable implements IObservableItem<VALUE_TYPE>, IObservableListenerHolder<VALUE_TYPE> {

	public static final int VT_NONE = 0;
	public static final int VT_SHORT = 5000;
	public static final int VT_MID = 30000;
	public static final int VT_LONG = 120000;

	public static final String VALUE_SEPARATOR = ".";

	protected abstract NATIVE_TYPE _updateValue(Object newValue);

	private int valueTimeout = VT_NONE;

	private final ObservableManagerImpl<VALUE_TYPE> observableManager = new ObservableManagerImpl<>();

	private ValueType valueType;

	private ItemMetaInfo meta = new ItemMetaInfo();

	private ValueGroup valueGroup;

	private boolean persistValue;

	protected NATIVE_TYPE value;
	
    double signalRate = 0;
    int signalCount = 0;
    long currentSignalCount = 0;

	
	long lastUpdate = 0;

	String comment;

	public ValueBase(ValueGroup valueGroup, String id, ValueType valueType) {
		super(id);
		this.valueGroup = valueGroup;
		this.valueType = valueType;
	}

	public ValueBase() {
	}

	public DBValue toDBValue() {
		return new DBValue(valueGroup.getId(), id, this.getClass().getSimpleName(), valueType, valueTimeout, getEnumCount());
	}

	protected int getEnumCount() {
		if (this instanceof EnumValue) {
			return ((EnumValue) this).getEnumCount();
		}
		return 0;
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public NATIVE_TYPE getValue() {
		return value;
	}

	public NATIVE_TYPE getValue(NATIVE_TYPE ifNullValue) {
		if (value == null) return ifNullValue;
		return value;
	}

	public String getValueAsString() {
		if (value == null) return StringUtils.EMPTY;
		return value.toString();
	}

	public ValueType getValueType() {
		return valueType;
	}

	public ValueGroup getValueGroup() {
		return valueGroup;
	}

	public void setValueGroup(ValueGroup valueGroup) {
		this.valueGroup = valueGroup;
	}

	/*
	 * public void setValueGroup(String valueGroupId) { this.valueGroup = new
	 * ValueGroup(valueGroupId); }
	 */

	public ValueBase withValueTimeout(int timeout) {
		this.valueTimeout = timeout;
		return this;
	}

	public int getValueTimeout() {
		return valueTimeout;
	}

	public void setValueTimeout(int valueTimeout) {
		this.valueTimeout = valueTimeout;
	}

	public String getFullId() {
		return getFullId(valueGroup.getId(), getId());
	}

	public static String getFullId(String valueGroupId, String valueId) {
		return valueGroupId + VALUE_SEPARATOR + valueId;
	}

	public ItemMetaInfo getMeta() {
		return meta;
	}

	public void setMeta(ItemMetaInfo meta) {
		this.meta = meta;
	}

	public boolean updateValue(Object newValue) {
		return updateValue(newValue, true);
	}

	public boolean updateValue(Object newValue, boolean invokeListeners) {
	    currentSignalCount++;

	    boolean isDifferent = Objects.equals(value, newValue);
	    value = _updateValue(newValue);

		if (invokeListeners) {
			itemChanged();
		}
	    //bool newValueApplied = m_value == newValue;
	    lastUpdate = System.currentTimeMillis();
	    return isDifferent;
	}

	@Override
	public void itemChanged() {
		observableManager.invokeListeners((VALUE_TYPE) this);
	}

	@Override
	public void addItemChangeListener(IItemChangeListener<VALUE_TYPE> listener) {
		observableManager.addItemChangeListener(listener);
	}

	@Override
	public void addItemChangeListener(IItemChangeListener<VALUE_TYPE> listener, boolean fireOnConnect) {
		addItemChangeListener(listener);
		if (fireOnConnect) {
			itemChanged();
		}
	}

	public boolean isPersistValue() {
		return persistValue;
	}

	public void setPersistValue(boolean persistValue) {
		this.persistValue = persistValue;
	}
	
}
