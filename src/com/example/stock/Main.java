package com.example.stock;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Main  extends ListActivity implements View.OnClickListener, KeyEvent.Callback {
	//股票数据适配器
		private QuoteAdapter quoteAdapter;
		//股票代码输入框
		private EditText symbolText;
		//股票代码输入按钮
		private Button addButton;
		//返回按钮
		private Button cancelButton;
		//删除按钮
		private Button deleteButton;
		//对话框
		private Dialog dialog = null;
		//股票详细信息
		private TextView currentTextView,noTextView, openTextView, closeTextView, dayLowTextView, dayHighTextView;
		//日K线图
		private ImageView chartView;
		//股票数据处理类
		DataHandler mDataHandler;
		//当前Activity实例
		Main mContext;
		//当前选中的股票的序号
		int currentSelectedIndex;
		//初始化界面
		public void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			setContentView(R.layout.main);
			mContext=this;
			//验证当前存放股票代码的文件是否存在
			File mFile =new File("/mnt/sdcard/symbols.txt");
			if(mFile.exists())
			{
				Log.e("guojs","file exist");
			}else{
				try {
					//新建存放股票代码的文件
					FileOutputStream outputStream=openFileOutput("symbols.txt",MODE_PRIVATE);
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
				Log.e("guojs","file no exist");
			}
			//初始化股票代码处理类
			mDataHandler = new DataHandler(mContext);
			//如果adapter数据为空显示的内容
			getListView().setEmptyView(findViewById(R.id.empty));
			quoteAdapter = new QuoteAdapter(this, this,mDataHandler);
			//为列表设置适配器
			this.setListAdapter(quoteAdapter);
			//添加股票按钮
			addButton = (Button) findViewById(R.id.add_symbols_button);
			//设置添加按钮监听器
			addButton.setOnClickListener(this);
			//股票输入文本框
			symbolText= (EditText) findViewById(R.id.stock_symbols);
		}
		//生命周期onCreate->onStart->onResume
		protected void onResume(){
			super.onResume();
			if(quoteAdapter != null)
			{
				//开始更新界面
				quoteAdapter.startRefresh();
			}
		}
		//界面不可见时，停止更新
		protected void onStop(){
			super.onStop();
			//停止更新界面
			quoteAdapter.stopRefresh();
		}
		//列表元素被点击之后触发
		protected void onListItemClick(ListView l, View v, int position, long id){
			super.onListItemClick(l,v, position, id);
			//取得点击位置的股票
			StockInfo quote = quoteAdapter.getItem(position);
			//取得当前位置的序号
			currentSelectedIndex=position;
			if(dialog == null){
				dialog = new Dialog(mContext);
				dialog.setContentView(R.layout.quote_detail);
				//删除按钮
				deleteButton = (Button) dialog.findViewById(R.id.delete);
				//设置删除按钮监听器
				deleteButton.setOnClickListener(this);
				//返回主界面按钮
				cancelButton = (Button) dialog.findViewById(R.id.close);
				//设置返回按钮监听器
				cancelButton.setOnClickListener(this);
				//当前股票价格
				currentTextView = (TextView) dialog.findViewById(R.id.current);
				//当前股票编码
				noTextView = (TextView) dialog.findViewById(R.id.no);
				//昨日收盘价
				openTextView = (TextView) dialog.findViewById(R.id.opening_price);
				//今日收盘价
				closeTextView = (TextView) dialog.findViewById(R.id.closing_price);
				//今日最低价
				dayLowTextView = (TextView) dialog.findViewById(R.id.day_low);
				//今日最高价
				dayHighTextView = (TextView) dialog.findViewById(R.id.day_high);
				//股票K线图
				chartView = (ImageView)dialog.findViewById(R.id.chart_view);
			}
			//设置对话框标题
			dialog.setTitle(quote.getName());
			//设置股票当前价格
			double current=Double.parseDouble(quote.getCurrent_price());
			double closing_price=Double.parseDouble(quote.getClosing_price());
			//保留两位小数
			DecimalFormat df=new DecimalFormat("#0.00"); 
			String percent=df.format(((current-closing_price)*100/closing_price))+"%";
			//若股票价格超过昨日收盘价
			if(current > closing_price)
			{
				//设置字体颜色为红色
				currentTextView.setTextColor(0xffEE3B3B);			
			}
			else 
			{
				//设置字体颜色为绿色
				currentTextView.setTextColor(0xff2e8b57);
			}
			//设置TextView内容
			currentTextView.setText(df.format(current)+"  ("+percent+")");
			openTextView.setText(quote.opening_price);
			closeTextView.setText(quote.closing_price);
			dayLowTextView.setText(quote.min_price);
			dayHighTextView.setText(quote.max_price);
			noTextView.setText(quote.no);
			//设置K线图
			chartView.setImageBitmap(mDataHandler.getChartForSymbol(quote.no));
			dialog.show();
		}
		//判断回车键按下时添加股票
		public boolean onKeyUp(int keyCode, KeyEvent event){
			if(keyCode == KeyEvent.KEYCODE_ENTER){
				//添加秃瓢
				
				addSymbol();
				return true;
			}
			return false;
		}
		//添加股票代码，以空格或者回车分隔多个股票
		private void addSymbol(){

			String symbolsStr = symbolText.getText().toString();
			//将回车符替换成空格
			symbolsStr = symbolsStr.replace("\n", " ");
			//以空格分割字符串
//			System.out.println(symbolsStr);
			String symbolArray[] = symbolsStr.split(" ");
			int index, count = symbolArray.length;
			ArrayList<String> symbolList = new ArrayList<String>();
			for(index = 0; index < count; index++){
				symbolList.add(symbolArray[index]);
				
			}
			//将股票代码添加进文件中
			    
					quoteAdapter.addSymbolsToFile(symbolList);
					//设置文本框为空
					symbolText.setText(null);
			} 
		

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == addButton){
				//添加股票到文件中
				
				addSymbol();
			} else if(v == cancelButton){
				//关闭对话框
				dialog.dismiss();
			} else if(v == deleteButton){
				//删除当前股票
				quoteAdapter.removeQuoteAtIndex(currentSelectedIndex);
				dialog.dismiss();
			} else if(v.getParent() instanceof RelativeLayout){
				RelativeLayout rl = (RelativeLayout)v.getParent();
				this.onListItemClick(getListView(), rl, rl.getId()-33, rl.getId());
			} else if(v instanceof RelativeLayout){
				this.onListItemClick(getListView(), v, v.getId()-33, v.getId());
			}
		}
}
