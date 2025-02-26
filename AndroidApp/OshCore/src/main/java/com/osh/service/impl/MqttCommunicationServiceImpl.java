package com.osh.service.impl;

import static com.osh.communication.mqtt.MqttConstants.MQTT_ACTOR_CMD_ATTR;
import static com.osh.communication.mqtt.MqttConstants.MQTT_BASE_PATH;
import static com.osh.communication.mqtt.MqttConstants.MQTT_HEALTH_STATE;
import static com.osh.communication.mqtt.MqttConstants.MQTT_PATH_SEP;
import static com.osh.communication.mqtt.MqttConstants.MQTT_SENDER_DEVICE_ID_ATTR;
import static com.osh.communication.mqtt.MqttConstants.MQTT_SINGLE_VALUE_ATTR;
import static com.osh.communication.mqtt.MqttConstants.MQTT_TS;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.datatypes.MqttTopic;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.osh.actor.ActorCmds;
import com.osh.actor.ActorMessage;
import com.osh.communication.MessageBase;
import com.osh.communication.mqtt.MessageTypeInfo;
import com.osh.communication.mqtt.MqttConstants;
import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.manager.IMqttSupport;
import com.osh.service.ICommunicationService;
import com.osh.controller.ControllerMessage;
import com.osh.device.DeviceDiscoveryMessage;
import com.osh.doorunlock.DoorUnlockMessage;
import com.osh.log.LogFacade;
import com.osh.log.LogMessage;
import com.osh.log.LogMessage.MsgType;
import com.osh.processor.ScriptResultMessage;
import com.osh.service.IDatamodelService;
import com.osh.time.SystemtimeMessage;
import com.osh.utils.IObservableBoolean;
import com.osh.utils.ObservableBoolean;
import com.osh.value.ValueMessage;
import com.osh.warn.SystemWarningMessage;

public class MqttCommunicationServiceImpl implements ICommunicationService {

	private static final String TAG = MqttCommunicationServiceImpl.class.getName();

	private ExecutorService executorService;
	private MqttConfig config;

	private final ObservableBoolean connectedState = new ObservableBoolean(false);

	@Override
	public IObservableBoolean connectedState() {
		return connectedState;
	}

	private String deviceId;

	private Mqtt3AsyncClient mqttClient;

	private Map<MessageBase.MESSAGE_TYPE, MessageTypeInfo> messageTypes = new ConcurrentHashMap<>();

	private Map<MessageBase.MESSAGE_TYPE, IMqttSupport> messageTypeServices = new ConcurrentHashMap<>();

	public MqttCommunicationServiceImpl(MqttConfig config) {
		this.executorService = Executors.newFixedThreadPool(1);
		this.config = config;
		this.deviceId = config.getClientId();		// use clientID as device id
	}


	@Override
	public void datamodelReady() {
		connectMqtt();
	}

	@Override
	public void connectMqtt() {
		LogFacade.i(TAG, "Init Mqtt");
		
	    registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_DEVICE_DISCOVERY, false, MqttConstants.MQTT_MESSAGE_TYPE_DD, 2, false);
	    registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_SYSTEM_TIME, false, MqttConstants.MQTT_MESSAGE_TYPE_ST, 0, true);
	    registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_SYSTEM_WARNING, false, MqttConstants.MQTT_MESSAGE_TYPE_SW, 1, true);
	    registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_CONTROLLER, false, MqttConstants.MQTT_MESSAGE_TYPE_CO, 1, true);
	    registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_LOG, false, MqttConstants.MQTT_MESSAGE_TYPE_LO, 2, false);
	    registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_SCRIPT_RESULT, false, MqttConstants.MQTT_MESSAGE_TYPE_SR, 1, true);
		registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_DOOR_UNLOCK, false, MqttConstants.MQTT_MESSAGE_TYPE_DU, 2, true);

		registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_VALUE, true, MqttConstants.MQTT_MESSAGE_TYPE_VA, 2, false);
		registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_ACTOR, true, MqttConstants.MQTT_MESSAGE_TYPE_AC, 2, false);

		mqttClient = MqttClient.builder()
				.identifier(config.getClientId())
				.automaticReconnect()
					.initialDelay(5, TimeUnit.SECONDS)
					.maxDelay(10, TimeUnit.SECONDS)
					.applyAutomaticReconnect()
				.serverHost(config.getServerHost())
				.serverPort(config.getServerPort())
				.addConnectedListener(listener -> {
					LogFacade.d(TAG, "Connected");
					connectedState.changeValue(true);
				})
				.addDisconnectedListener(listener -> {
					LogFacade.w(TAG, "Disconnected " + listener.getCause());
					connectedState.changeValue(false);
				})
				.useMqttVersion3().buildAsync();

		CompletableFuture<Mqtt3ConnAck> connAckFuture = mqttClient.connectWith()
				.cleanSession(false)
				.keepAlive(5)
				.send();

		connAckFuture.whenComplete((connAck, error) -> {
			if (connAck.getReturnCode() == Mqtt3ConnAckReturnCode.SUCCESS) {
				LogFacade.i(TAG, "Connected");
				subscribeChannels();
			}
		});
	}

	@Override
	public void registerMessageType(MessageBase.MESSAGE_TYPE messageType, IMqttSupport service) {
		messageTypeServices.put(messageType, service);
	}
	
	private void subscribeChannels() {
		subscribeChannels(Arrays.asList(MqttConstants.MQTT_MESSAGE_TYPE_VA, MqttConstants.MQTT_MESSAGE_TYPE_DD, MqttConstants.MQTT_MESSAGE_TYPE_ST, MqttConstants.MQTT_MESSAGE_TYPE_SW, MqttConstants.MQTT_MESSAGE_TYPE_AC, MqttConstants.MQTT_MESSAGE_TYPE_SR, MqttConstants.MQTT_MESSAGE_TYPE_LO, MqttConstants.MQTT_MESSAGE_TYPE_DU));
    }
	
	private void subscribeChannels(List<String> topics) {
		LogFacade.d(TAG, "Subscribing to channels " + topics.size());

	    for (String topic : topics) {
	    	String path = MQTT_BASE_PATH + MQTT_PATH_SEP + topic + MQTT_PATH_SEP + MqttConstants.MQTT_WILDCARD;
	    	subscribe(path);
	    }
	}
	
	private void onMessage(@NotNull MqttTopic topic, @NotNull Optional<ByteBuffer> payload, boolean retain) {
		LogFacade.d(TAG, "Msg arrived " + topic.toString() + (retain ? " retained" : ""));


		try {
			MessageBase msg = getMessage(topic.toString(), payload);
			if (msg != null) {
				if (messageTypeServices.containsKey(msg.getMessageType())) {
					executorService.submit(() -> {
						try {
							messageTypeServices.get(msg.getMessageType()).handleReceivedMessage(msg);
						} catch (Exception ex) {
							LogFacade.w(TAG, "Error while executing handler: " + ex.toString());
						}
					});
				} else {
					LogFacade.w(TAG, "No handlers for msg type " + msg.getMessageType());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void subscribe(String path) {
		if (mqttClient.getState() == MqttClientState.CONNECTED) {
			mqttClient.subscribeWith()
					.topicFilter(path)
					.qos(MqttQos.EXACTLY_ONCE)
					.callback(cb -> {
						onMessage(cb.getTopic(), cb.getPayload(), cb.isRetain());
					})
					.send()
					.whenComplete((subAck, error) -> {
						if (subAck.getReturnCodes().stream().filter(returnCode -> returnCode.isError()).count() == 0) {
							LogFacade.i(TAG, "Subscribed to " + path);
						} else {
							LogFacade.w(TAG, "Failed to subscribe to path " + path + ": " + subAck.getReturnCodes().stream().map(rc -> Integer.toString(rc.getCode())).collect(Collectors.joining()));
						}
					});
		} else {
			LogFacade.w(TAG, "Cannot subscribe - not connected!");
		}
	}

	@Override
	public boolean sendMessage(MessageBase msg) {
		try {
			if (mqttClient != null && mqttClient.getState() == MqttClientState.CONNECTED) {
				String topic = getTopicName(msg);
				String payload = serializePayload(msg);
				boolean isRetained = isRetainedMessage(msg);

				mqttClient.publishWith()
						.topic(topic)
						.payload(payload.getBytes())
						.retain(isRetained)
						.qos(MqttQos.AT_LEAST_ONCE)
						.send().whenComplete((result, error) -> {
							if (error == null) {
								LogFacade.d(TAG, "Publish complete");
							} else {
								LogFacade.w(TAG, "Publish failed: " + error.toString());
							}
						});
				return true;
			} else {
				LogFacade.w(TAG, "Cannot send - not connected!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String serializePayload(MessageBase msg) throws JSONException {
	    switch(msg.getMessageType()) {
	    case MESSAGE_TYPE_VALUE:
	        return serializeSingleJSONValue(((ValueMessage) msg).getRawValue());
	    case MESSAGE_TYPE_ACTOR:
			Map<String, Object> map = new HashMap<>();
			if (((ActorMessage) msg).getRawValue() != null) {
				map.put(MqttConstants.MQTT_SINGLE_VALUE_ATTR, ((ActorMessage) msg).getRawValue());
			}
			map.put(MQTT_ACTOR_CMD_ATTR, ((ActorMessage) msg).getCmd().getValue());
	        return serializeJSONMap(map);
	    case MESSAGE_TYPE_SYSTEM_TIME:
	        return serializeSingleJSONValue(((SystemtimeMessage) msg).getTs());
	    case MESSAGE_TYPE_SYSTEM_WARNING:
	        return serializeSingleJSONValue(((SystemWarningMessage) msg).getMsg());
	    case MESSAGE_TYPE_DEVICE_DISCOVERY:
	    	return serializeJSONMap(((DeviceDiscoveryMessage) msg).getValues());
	    case MESSAGE_TYPE_CONTROLLER:
	    	return serializeSingleJSONValue(((ControllerMessage) msg).getData());
	    case MESSAGE_TYPE_LOG:
	    	return serializeSingleJSONValue(((LogMessage) msg).getMessage());
	    case MESSAGE_TYPE_SCRIPT_RESULT:
	    	return serializeSingleJSONValue(((ScriptResultMessage) msg).getValue());
		case MESSAGE_TYPE_DOOR_UNLOCK:
			return serializeJSONMap(((DoorUnlockMessage) msg).getValues());
	    default:
			LogFacade.w(TAG, "Unknown message type " + msg.getMessageType());
			return null;
	    }
	}
	
	/*
	private String serializeSingleJSONValue(boolean value) {
		JsonObject obj = Json.createObject();
		serializeSingleJSONValue(value, obj);
		obj.put(MqttConstants.MQTT_SINGLE_VALUE_ATTR, value);
		return obj.asString();
	}*/

	private void serializeSingleJSONValue(String key, Object value, JSONObject obj) throws JSONException {
		if (value instanceof Integer) {
			obj.put(key, (int)value);
		} else if (value instanceof Long) {
			obj.put(key, (long)value);
		} else if (value instanceof String) {
			obj.put(key, (String)value);
		} else if (value instanceof Boolean) {
			obj.put(key, (boolean) value);
		} else if (value instanceof Enum) {
			obj.put(key, ((Enum) value).ordinal());
		} else {
			LogFacade.w(TAG, "Unsupported data type: " + value.getClass());
		}
	}

	private String serializeJSONMap(Map<String, Object> values) throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(MQTT_SENDER_DEVICE_ID_ATTR, deviceId);
		obj.put(MQTT_TS, System.currentTimeMillis());

		for (String key : values.keySet()) {
			obj.put(key, values.get(key));
		}
		return obj.toString();
	}
	private String serializeSingleJSONValue(Object value) throws JSONException {
		Map<String, Object> map = new HashMap<>();
		map.put(MqttConstants.MQTT_SINGLE_VALUE_ATTR, value);
		return serializeJSONMap(map);
	}

	private void registerMessageType(MessageBase.MESSAGE_TYPE messageType, boolean isRetained, String mqttTypePath, int mqttPathLevels, boolean dropOwnMessages) {
	    MessageTypeInfo info = new MessageTypeInfo();

	    info.messageType = messageType;
	    info.isRetained = isRetained;
	    info.mqttTypePath = mqttTypePath;
	    info.mqttPathLevels = mqttPathLevels;
		info.dropOwnMessages = dropOwnMessages;

	    messageTypes.put(messageType, info);
	}

	private synchronized MessageBase getMessage(String topic, Optional<ByteBuffer> payload) throws JSONException {

		if (topic.startsWith(MQTT_BASE_PATH)) {
			topic = topic.substring(MQTT_BASE_PATH.length() + 1);
		}
		
		String[] paths = topic.split("/");
	    List<String> firstLevelPath = removeMessageTypePath(paths);
	    MessageTypeInfo info = getMessageType(paths[0]);

		if (info != null) {
			if (info.mqttPathLevels == firstLevelPath.size()) {
				Map<String, Object> rawValue = parseJSONPayload(payload);

				String senderDeviceId = "";
				if (rawValue.containsKey(MQTT_SENDER_DEVICE_ID_ATTR)) {
					senderDeviceId = rawValue.get(MQTT_SENDER_DEVICE_ID_ATTR).toString();
					if (info.dropOwnMessages && senderDeviceId.equals(deviceId)) {
						LogFacade.w(TAG, "Dropping own message " + info.messageType);
						return null;
					}
				}

				long ts = 0;
				if (rawValue.containsKey(MQTT_TS)) {
					ts =  ((Number)rawValue.get(MQTT_TS)).longValue();
				}

				LogFacade.d(TAG,info.messageType + " " +  rawValue);

				switch (info.messageType) {
				case MESSAGE_TYPE_VALUE: {
					return new ValueMessage(firstLevelPath.get(0), firstLevelPath.get(1), rawValue);
				}
				case MESSAGE_TYPE_ACTOR: {
					if (rawValue.containsKey(MQTT_ACTOR_CMD_ATTR)) {
						ActorCmds cmd = ActorCmds.of(Integer.parseInt(rawValue.get(MQTT_ACTOR_CMD_ATTR).toString()));
						Object value = rawValue.get(MQTT_SINGLE_VALUE_ATTR);
						return new ActorMessage(firstLevelPath.get(0), firstLevelPath.get(1), value, cmd);
					} else {
						LogFacade.w(TAG, "Cmd attribute missing: " + rawValue.toString());
						return null;
					}
				}
				case MESSAGE_TYPE_DEVICE_DISCOVERY: {
					DeviceDiscoveryMessage.DeviceHealthState healthState = DeviceDiscoveryMessage.DeviceHealthState.Unknown;
					if (rawValue.containsKey(MQTT_HEALTH_STATE)) {
						healthState = DeviceDiscoveryMessage.DeviceHealthState.values()[((Number) rawValue.get(MQTT_HEALTH_STATE)).byteValue()];
					}
					return new DeviceDiscoveryMessage(firstLevelPath.get(0), firstLevelPath.get(1), Long.parseLong(parseSingleValue(rawValue).toString()), healthState);
				}
				case MESSAGE_TYPE_SYSTEM_TIME: {
					return new SystemtimeMessage(((Number) parseSingleValue(rawValue)).longValue());
				}
				case MESSAGE_TYPE_SYSTEM_WARNING: {
					return new SystemWarningMessage(firstLevelPath.get(0), parseSingleValue(rawValue).toString());
				}
				case MESSAGE_TYPE_CONTROLLER: {
					return new ControllerMessage(firstLevelPath.get(0), rawValue);
				}
				case MESSAGE_TYPE_LOG: {
					return new LogMessage(firstLevelPath.get(0), MsgType.fromString(firstLevelPath.get(0)), parseSingleValue(rawValue).toString());
				}
				case MESSAGE_TYPE_SCRIPT_RESULT: {
					return new ScriptResultMessage(firstLevelPath.get(0), rawValue);
				}
				case MESSAGE_TYPE_DOOR_UNLOCK: {
					return new DoorUnlockMessage(firstLevelPath.get(0), firstLevelPath.get(1), rawValue);
				}
				default:
					LogFacade.w(TAG, "Unknown message type " + info.messageType);
					return null;
				}
			} else {
				LogFacade.w(TAG, "Invalid path levels " + firstLevelPath.size() + ", expected " + info.mqttPathLevels);
				return null;
			}
		} else {
			LogFacade.w(TAG, "Invalid message info");
			return null;
		}
	}
	
	private Object parseSingleValue(Map<String, Object> value) {
	    if (value.containsKey(MqttConstants.MQTT_SINGLE_VALUE_ATTR)) {
	        return value.get(MqttConstants.MQTT_SINGLE_VALUE_ATTR);
	    } else {
	    	return null;
	    }
	}

	
	synchronized List<String> removeMessageTypePath(String[] paths) {
		for (MessageTypeInfo messageTypeInfo : messageTypes.values()) {
			
	        if (paths[0].equals(messageTypeInfo.mqttTypePath)) {
	        	List<String> returnList = Arrays.stream(paths).collect(Collectors.toList());
	        	returnList.remove(0);
	        	return returnList;
	        }
		}

	    return List.of();
	}

	synchronized MessageTypeInfo getMessageType(String name) {
	    for (MessageTypeInfo messageTypeInfo : messageTypes.values()) {
	    	if (messageTypeInfo.mqttTypePath.equals(name)) {
	    		return messageTypeInfo;
	    	}
	    }

		//LogFacade.w(TAG, "Unknown message type " + name);
	    return null;
	}
	
	Object parsePayload(MessageBase.MESSAGE_TYPE messageType, Optional<ByteBuffer> payload) throws JSONException {
        if (payload.isPresent()) {
            return parseJSONPayload(payload);
        } else {
            return null;
        }
	}

	Map<String, Object> parseJSONPayload(Optional<ByteBuffer> payload) throws JSONException {
		Map <String, Object> map = new HashMap<>();
		if (payload.isPresent()) {
			JSONObject obj = new JSONObject(Charset.defaultCharset().decode(payload.get()).toString());
			Iterator<String> it = obj.keys();
			while (it.hasNext()) {
				String key = it.next();
				map.put(key, obj.get(key));
			}
		}
	    return map;
	}

	private String getTopicName(MessageBase message) {
		StringBuilder sb = new StringBuilder();
		sb.append(MQTT_BASE_PATH);

		MessageTypeInfo info = messageTypes.get(message.getMessageType());
		sb.append(MQTT_PATH_SEP);
		sb.append(info.mqttTypePath);

		if (!message.getFirstLevelId().isEmpty()) {
			sb.append(MQTT_PATH_SEP);
			sb.append(message.getFirstLevelId());
		}

		if (!message.getSecondLevelId().isEmpty()) {
			sb.append(MQTT_PATH_SEP);
			sb.append(message.getSecondLevelId());
		}

		return sb.toString();
	}

	private boolean isRetainedMessage(MessageBase message) {
		MessageTypeInfo info = messageTypes.get(message.getMessageType());
		return info.isRetained;
	}


}
