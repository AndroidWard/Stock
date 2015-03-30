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
	//ǿ�Ƹ��±�־
	private boolean forceUpdate = false;
	//����������
	Context context;
	//����Activityʵ��
	Main stocker;
	LayoutInflater inflater;

	QuoteRefreshTask quoteRefreshTask = null;
	int progressInterval;
	//��Ϣ������
	Handler messageHandler = new Handler();
	public  QuoteAdapter(Main aController, Context mContext,DataHandler mdataHandler) {
		//���浱ǰ�������ĺ�Activityʵ��
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
		//���ù�Ʊ�Ĵ���
		field.setText(quote.getNo());
		field.setClickable(true);
		field.setOnClickListener(stocker);

		//��Ʊ����
		field = (TextView)cellLayout.findViewById(R.id.name);
		field.setClickable(true);
		field.setOnClickListener(stocker);
		field.setText(quote.getName());
		
		field = (TextView)cellLayout.findViewById(R.id.current);
		//���ù�Ʊ��ǰ�۸�
		double current=Double.parseDouble(quote.getCurrent_price());
		double closing_price=Double.parseDouble(quote.getClosing_price());
		//������λС��
		DecimalFormat df=new DecimalFormat("#0.00"); 
		String percent=df.format(((current-closing_price)*100/closing_price))+"%";
		field.setText(df.format(current));
		field.setClickable(true);
		field.setOnClickListener(stocker);		
		
		field = (TextView)cellLayout.findViewById(R.id.percent);
		//����Ʊ�۸񳬹��������̼�
		if(current > closing_price)
		{
			//����������ɫΪ��ɫ
			field.setTextColor(0xffEE3B3B);			
		}
		else 
		{
			//����������ɫΪ��ɫ
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
	//ֹͣ���¹�Ʊ
	public void stopRefresh(){
		quoteRefreshTask.cancelTimer();
		quoteRefreshTask = null;
	}
	//��ʼ���¹�Ʊ
	public void startRefresh(){
		if(quoteRefreshTask == null)
			quoteRefreshTask = new QuoteRefreshTask(this);
	}
	//����������
	public void refreshQuotes(){
		messageHandler.post(this);		
	}
	//��������������
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
			//֪ͨ���ݸ���
			this.notifyDataSetChanged();
		}
	}

	//��ӹ�Ʊ���뵽�ļ���
			public void addSymbolsToFile(ArrayList<String> symbols){
				//ǿ�и���ҳ������
				forceUpdate = true;
				//��ӹ�Ʊ���ļ���
				stocker.mDataHandler.addSymbolsToFile(symbols);
				//�����Ϣ����Ϣ����
				messageHandler.post(this);
			}
			//�Ƴ��б��е�����
			public void removeQuoteAtIndex(int index){
				forceUpdate = true;
				stocker.mDataHandler.removeQuoteByIndex(index);
				messageHandler.post(this);
			}
			//��Ʊ���¶�ʱ��
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
				//ȡ����ʱ��
				public void cancelTimer(){
					this.cancel();
					refreshTimer.cancel();
					refreshTimer = null;
				}
}

}
