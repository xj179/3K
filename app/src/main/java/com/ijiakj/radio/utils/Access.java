package com.ijiakj.radio.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Access {

	/** 禁止构造本类对象 */
	private Access() {
	}
	
	/** 返回数据表主键所需的32位主键值 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "").toUpperCase();
	}
	
	public static String getDeviceID(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String serial = Build.SERIAL;
		
		StringBuilder sb =  new StringBuilder();
		if(!TextUtils.isEmpty(deviceId)){
			sb.append(deviceId);
		}
		if(!TextUtils.isEmpty(serial)){
			sb.append(serial);
		}
		if(sb.toString().length()==0){
			sb.append(Access.getUUID());
		}
		sb.append("child");
		UUID deviceUuid = UUID.nameUUIDFromBytes(sb.toString().getBytes());
		return deviceUuid.toString().replace("-", "").toLowerCase();
	}


	/**
	 * 方法的作用：取多条记录放在一个List中，每条记录用map保存
	 * 注意1：返回的List如果是null则表示查询失败，如果size为0则表示操作成功但表中无对应记录
	 * 注意2：不用担心查询的某些字段的值为null，因为字段值为null时赋予了空字符串
	 */
	public static List<Map<String, String>> getListMap(Cursor cursor) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		String temp;
		try {
			if (cursor != null && cursor.getCount() > 0) {
				int count = cursor.getCount();
				int columnCount = cursor.getColumnCount();
				String[] columnNames = new String[columnCount];// 定义一个数组存放字段名称
				for (int i = 0; i < columnCount; i++) {
					columnNames[i] = cursor.getColumnName(i);
				}
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					map = new HashMap<String, String>();
					for (int j = 0; j < columnCount; j++) {
						temp = cursor.getString(cursor
								.getColumnIndex(columnNames[j]));
						map.put(columnNames[j], temp == null ? "" : temp);
					}
					list.add(map);
					cursor.moveToNext();
				}
			}
			return list;
		} catch (Exception e) {
			return null;
		} 
	}



}
