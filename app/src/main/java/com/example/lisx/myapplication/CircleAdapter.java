/*
 * @Title CircleAdapter.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author lisx
 * @date 2015-5-14 下午6:03:33
 * @version 1.0
 */
package com.example.lisx.myapplication;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author lisx
 * @date 2015-5-14 下午6:03:33
 */
public class CircleAdapter extends BaseAdapter {
	private LayoutInflater mInfllater;
	private Context context;
	private List<String> list = null;

	public CircleAdapter(Context context, List<String> data) {
		this.context = context;
		this.mInfllater = LayoutInflater.from(context);
		this.list = data;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInfllater.inflate(R.layout.circle_menu_item, parent,
					false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.id_circle_menu_item_image);
			// 暂时设置隐藏

			// holder.icon.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// Toast.makeText(v.getContext(), "点击了icon",
			// Toast.LENGTH_SHORT).show();
			// }
			// });
			holder.tv = (TextView) convertView
					.findViewById(R.id.id_circle_menu_item_text);
			holder.tv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "点击了文本框", Toast.LENGTH_SHORT)
							.show();
				}
			});
			convertView.setTag(holder);
		}
	    else {
			holder = (ViewHolder) convertView.getTag();
		}
		String content = list.get(position);
		holder.tv.setText(content);
		holder.tv.setVisibility(View.VISIBLE);
		holder.icon.setVisibility(View.VISIBLE);
		return convertView;
	}

	private class ViewHolder {
		private ImageView icon;
		private TextView tv;
	}
}
