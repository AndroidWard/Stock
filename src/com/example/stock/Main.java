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
	//��Ʊ����������
		private QuoteAdapter quoteAdapter;
		//��Ʊ���������
		private EditText symbolText;
		//��Ʊ�������밴ť
		private Button addButton;
		//���ذ�ť
		private Button cancelButton;
		//ɾ����ť
		private Button deleteButton;
		//�Ի���
		private Dialog dialog = null;
		//��Ʊ��ϸ��Ϣ
		private TextView currentTextView,noTextView, openTextView, closeTextView, dayLowTextView, dayHighTextView;
		//��K��ͼ
		private ImageView chartView;
		//��Ʊ���ݴ�����
		DataHandler mDataHandler;
		//��ǰActivityʵ��
		Main mContext;
		//��ǰѡ�еĹ�Ʊ�����
		int currentSelectedIndex;
		//��ʼ������
		public void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			setContentView(R.layout.main);
			mContext=this;
			//��֤��ǰ��Ź�Ʊ������ļ��Ƿ����
			File mFile =new File("/mnt/sdcard/symbols.txt");
			if(mFile.exists())
			{
				Log.e("guojs","file exist");
			}else{
				try {
					//�½���Ź�Ʊ������ļ�
					FileOutputStream outputStream=openFileOutput("symbols.txt",MODE_PRIVATE);
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
				Log.e("guojs","file no exist");
			}
			//��ʼ����Ʊ���봦����
			mDataHandler = new DataHandler(mContext);
			//���adapter����Ϊ����ʾ������
			getListView().setEmptyView(findViewById(R.id.empty));
			quoteAdapter = new QuoteAdapter(this, this,mDataHandler);
			//Ϊ�б�����������
			this.setListAdapter(quoteAdapter);
			//��ӹ�Ʊ��ť
			addButton = (Button) findViewById(R.id.add_symbols_button);
			//������Ӱ�ť������
			addButton.setOnClickListener(this);
			//��Ʊ�����ı���
			symbolText= (EditText) findViewById(R.id.stock_symbols);
		}
		//��������onCreate->onStart->onResume
		protected void onResume(){
			super.onResume();
			if(quoteAdapter != null)
			{
				//��ʼ���½���
				quoteAdapter.startRefresh();
			}
		}
		//���治�ɼ�ʱ��ֹͣ����
		protected void onStop(){
			super.onStop();
			//ֹͣ���½���
			quoteAdapter.stopRefresh();
		}
		//�б�Ԫ�ر����֮�󴥷�
		protected void onListItemClick(ListView l, View v, int position, long id){
			super.onListItemClick(l,v, position, id);
			//ȡ�õ��λ�õĹ�Ʊ
			StockInfo quote = quoteAdapter.getItem(position);
			//ȡ�õ�ǰλ�õ����
			currentSelectedIndex=position;
			if(dialog == null){
				dialog = new Dialog(mContext);
				dialog.setContentView(R.layout.quote_detail);
				//ɾ����ť
				deleteButton = (Button) dialog.findViewById(R.id.delete);
				//����ɾ����ť������
				deleteButton.setOnClickListener(this);
				//���������水ť
				cancelButton = (Button) dialog.findViewById(R.id.close);
				//���÷��ذ�ť������
				cancelButton.setOnClickListener(this);
				//��ǰ��Ʊ�۸�
				currentTextView = (TextView) dialog.findViewById(R.id.current);
				//��ǰ��Ʊ����
				noTextView = (TextView) dialog.findViewById(R.id.no);
				//�������̼�
				openTextView = (TextView) dialog.findViewById(R.id.opening_price);
				//�������̼�
				closeTextView = (TextView) dialog.findViewById(R.id.closing_price);
				//������ͼ�
				dayLowTextView = (TextView) dialog.findViewById(R.id.day_low);
				//������߼�
				dayHighTextView = (TextView) dialog.findViewById(R.id.day_high);
				//��ƱK��ͼ
				chartView = (ImageView)dialog.findViewById(R.id.chart_view);
			}
			//���öԻ������
			dialog.setTitle(quote.getName());
			//���ù�Ʊ��ǰ�۸�
			double current=Double.parseDouble(quote.getCurrent_price());
			double closing_price=Double.parseDouble(quote.getClosing_price());
			//������λС��
			DecimalFormat df=new DecimalFormat("#0.00"); 
			String percent=df.format(((current-closing_price)*100/closing_price))+"%";
			//����Ʊ�۸񳬹��������̼�
			if(current > closing_price)
			{
				//����������ɫΪ��ɫ
				currentTextView.setTextColor(0xffEE3B3B);			
			}
			else 
			{
				//����������ɫΪ��ɫ
				currentTextView.setTextColor(0xff2e8b57);
			}
			//����TextView����
			currentTextView.setText(df.format(current)+"  ("+percent+")");
			openTextView.setText(quote.opening_price);
			closeTextView.setText(quote.closing_price);
			dayLowTextView.setText(quote.min_price);
			dayHighTextView.setText(quote.max_price);
			noTextView.setText(quote.no);
			//����K��ͼ
			chartView.setImageBitmap(mDataHandler.getChartForSymbol(quote.no));
			dialog.show();
		}
		//�жϻس�������ʱ��ӹ�Ʊ
		public boolean onKeyUp(int keyCode, KeyEvent event){
			if(keyCode == KeyEvent.KEYCODE_ENTER){
				//���ͺư
				
				addSymbol();
				return true;
			}
			return false;
		}
		//��ӹ�Ʊ���룬�Կո���߻س��ָ������Ʊ
		private void addSymbol(){

			String symbolsStr = symbolText.getText().toString();
			//���س����滻�ɿո�
			symbolsStr = symbolsStr.replace("\n", " ");
			//�Կո�ָ��ַ���
//			System.out.println(symbolsStr);
			String symbolArray[] = symbolsStr.split(" ");
			int index, count = symbolArray.length;
			ArrayList<String> symbolList = new ArrayList<String>();
			for(index = 0; index < count; index++){
				symbolList.add(symbolArray[index]);
				
			}
			//����Ʊ������ӽ��ļ���
			    
					quoteAdapter.addSymbolsToFile(symbolList);
					//�����ı���Ϊ��
					symbolText.setText(null);
			} 
		

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == addButton){
				//��ӹ�Ʊ���ļ���
				
				addSymbol();
			} else if(v == cancelButton){
				//�رնԻ���
				dialog.dismiss();
			} else if(v == deleteButton){
				//ɾ����ǰ��Ʊ
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
