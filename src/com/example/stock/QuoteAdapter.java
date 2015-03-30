package com.example.stock;

import com.example.stock.DataHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuoteAdapter extends BaseAdapter implements ListAdapter, Runnable {
	private static final int DISPLAY_COUNT = 10;
	public DataHandler dataHandler;
	//强制更新标志
	private boolean forceUpdate = false;
	//保存上下文
	Context context;
	//保存Activity实例
	Main stocker;
	LayoutInflater inflater;

	QuoteRefreshTask quoteRefreshTask = null;
	int progressInterval;
	//消息处理器
	Handler messageHandler = new Handler();
	public  QuoteAdapter(Main aController, Context mContext,DataHandler mdataHandler) {
		//保存当前的上下文和Activity实例
		context = mContext;
		stocker = aController;
		dataHandler = mdataHandler;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataHandler.stocksSize();

	}

	@Override
	public StockInfo getItem(int position) {
		// TODO Auto-generated method stub
		return dataHandler.getQuoteForIndex(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		StockInfo quote;
		inflater = LayoutInflater.from(context);
		RelativeLayout cellLayout = (RelativeLayout)inflater.inflate(R.layout.quote_cell, null);
		cellLayout.setMinimumWidth(parent.getWidth());
		int color;
		stocker.setProgress(progressInterval*(position + 1));
		if(position % 2 > 0)
			color = Color.rgb(48,92,131);
		else 
			color = Color.rgb(119,138,170);
		cellLayout.setBackgroundColor(color);
		quote = dataHandler.getQuoteForIndex(position);
		TextView field = (TextView)cellLayout.findViewById(R.id.symbol);
		//设置股票的代码
		field.setText(quote.getNo());
		field.setClickable(true);
		field.setOnClickListener(stocker);

		//股票名字
		field = (TextView)cellLayout.findViewById(R.id.name);
		field.setClickable(true);
		field.setOnClickListener(stocker);
		field.setText(quote.getName());
		
		field = (TextView)cellLayout.findViewById(R.id.current);
		//设置股票当前价格
		double current=Double.parseDouble(quote.getCurrent_price());
		double closing_price=Double.parseDouble(quote.getClosing_price());
		//保留两位小数
		DecimalFormat df=new DecimalFormat("#0.00"); 
		String percent=df.format(((current-closing_price)*100/closing_price))+"%";
		field.setText(df.format(current));
		field.setClickable(true);
		field.setOnClickListener(stocker);		
		
		field = (TextView)cellLayout.findViewById(R.id.percent);
		//若股票价格超过昨日收盘价
		if(current > closing_price)
		{
			//设置字体颜色为红色
			field.setTextColor(0xffEE3B3B);			
		}
		else 
		{
			//设置字体颜色为绿色
			field.setTextColor(0xff2e8b57);
		}
		field.setText(percent);
		cellLayout.setId(position + 33);
		cellLayout.setClickable(true);
		cellLayout.setOnClickListener(stocker);
		return cellLayout;
	}
	public boolean areAllItemsSelectable() {
		return true;
	}
	public boolean isSelectable(int arg0) {
		return true;
	}
	//停止更新股票
	public void stopRefresh(){
		quoteRefreshTask.cancelTimer();
		quoteRefreshTask = null;
	}
	//开始更新股票
	public void startRefresh(){
		if(quoteRefreshTask == null)
			quoteRefreshTask = new QuoteRefreshTask(this);
	}
	//更新适配器
	public void refreshQuotes(){
		messageHandler.post(this);		
	}
	//更新适配器内容
	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		if(stocker.mDataHandler.stocksSize() > 0){
			if(forceUpdate ){
				forceUpdate = false;
				progressInterval = 10000/DISPLAY_COUNT;
				stocker.setProgressBarVisibility(true);
				stocker.setProgress(progressInterval);
				stocker.mDataHandler.refreshStocks();
			}
			//通知数据更改
			this.notifyDataSetChanged();
		}
	}

	//添加股票代码到文件中
			public void addSymbolsToFile(ArrayList<String> symbols){
				//强行更新页面数据
				forceUpdate = true;
				//添加股票到文件中
				stocker.mDataHandler.addSymbolsToFile(symbols);
				//添加消息到消息队列
				messageHandler.post(this);
			}
			//移除列表中的数据
			public void removeQuoteAtIndex(int index){
				forceUpdate = true;
				stocker.mDataHandler.removeQuoteByIndex(index);
				messageHandler.post(this);
			}
			//股票更新定时器
			public class QuoteRefreshTask extends TimerTask {
				QuoteAdapter quoteAdaptor;
				Timer        refreshTimer;
				final static int  TENSECONDS = 10000;
				public QuoteRefreshTask(QuoteAdapter anAdaptor){
					refreshTimer = new Timer("Quote Refresh Timer");
					refreshTimer.schedule(this, TENSECONDS, TENSECONDS);
					quoteAdaptor = anAdaptor;
				}

				public void run(){
					messageHandler.post(quoteAdaptor);
				}

				public void startTimer(){
					if(refreshTimer == null){
						refreshTimer = new Timer("Quote Refresh Timer");
						refreshTimer.schedule(this, TENSECONDS, TENSECONDS);
					}
				}
				//取消定时器
				public void cancelTimer(){
					this.cancel();
					refreshTimer.cancel();
					refreshTimer = null;
				}
}

}
