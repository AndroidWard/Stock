package com.example.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

public class DataHandler {
	private static final String TAG = "DataHandler";
	// ��Ʊ��ѯ��ַ
	private static final String QUERY_URL = "http://hq.sinajs.cn/list=";
	// ��ƱK��ͼ
	private static final String QUERY_IMG = "http://image.sinajs.cn/newchart/daily/n/";
	// �洢��Ʊ������ļ�
	private static final String SYMBOL_FILE_NAME = "symbols.txt";
	private int BUF_SIZE = 16384;
	// �洢��Ʊ���������
	private ArrayList<String> stocks;
	// �洢��Ʊ���ݵ�����
	private ArrayList<StockInfo> stockInfo = new ArrayList<StockInfo>();
	// ������Ʊ��Ϣ�������е�����
	private final int NAME = 0;
	private final int OPENING_PRICE = 1;
	private final int CLOSING_PRICE = 2;
	private final int CURRENT_PRICE = 3;
	private final int MAX_PRICE = 4;
	private final int MIN_PRICE = 5;
	Context context;

	public DataHandler(Context mContext) {
		super();
		// ��ȡ�洢���ļ��еĹ�Ʊ������Ϣ
		// this.readStockFromFile();
		context = mContext;
		if (stocks != null) {
			// ���¹�Ʊ����
			// refreshStocks();
		}
	}
	//��ȡ�洢���ļ��еĹ�Ʊ������Ϣ
		private void readStockFromFile(){
			File fullPath;
			if(stocks == null) 
			{
				//��ʼ����Ʊ��������
				stocks = new ArrayList<String>();
			}
			FileInputStream inStream;
			BufferedReader bReader;
			String quoteStr="";
			//��ȡ�洢��Ʊ���ļ�
			fullPath = new File("/data/data/com.supermario.stocker/files/symbols.txt");
				//��ȡ�ļ�
			try {
				inStream = new FileInputStream(fullPath);
				bReader = new BufferedReader(new InputStreamReader(inStream));			
				quoteStr = bReader.readLine();
				bReader.close();
				inStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//����ļ��в�������Ʊ��������
			if(quoteStr == "" || quoteStr == null)
			{
				//�������
				stocks.clear();
				return;
			}
			//���ַ����и������
			String strArray[] = quoteStr.split(",");
			int index, count = strArray.length;
			//��������
			stocks.clear();
			//������洢��������
			for(index = 0; index < count; index++) 
				stocks.add(strArray[index]);
		}
		//��ӹ�Ʊ���뵽�ļ���
		public synchronized void addSymbolsToFile(ArrayList<String> stockList){
			if(stockList != null){
				//�����Ʊ������û������
				if(stocks == null || stocks.size() == 0){ 
					//ֱ�ӽ�������ӵ���������
					stocks = new ArrayList<String>();
					stocks.addAll(stockList);
				} else {
					int i1, i2;
					//�ļ��й�Ʊ�����С
					int c1 = stocks.size();
					//����ӵĹ�Ʊ�����С
					int c2 = stockList.size();
					ArrayList<String> newStocks = new ArrayList<String>();
					//�ж�����ӵĹ�Ʊ�����Ƿ���ԭ����������
					boolean foundSymbol = false;
					//ѭ�����������ҳ��¹�Ʊ
					for(i2 = 0; i2 < c2; i2++){
						String newSymbol = stockList.get(i2);
						for(i1 = 0; i1 < c1; i1++){
							if(newSymbol.equals(stocks.get(i1))){
								foundSymbol = true;
								break;
							}
						}
						//��Ϊ�¹�Ʊ������ӽ��¹�Ʊ������
						if(!foundSymbol){
							newStocks.add(newSymbol);
						}
					}
					if(newStocks.size() > 0){
						
						__addQuotes(newStocks);
					}		
				}
				//�����Ʊ
				savePortfolio();
			}
		}
		//���ݹ�Ʊ����������ӹ�Ʊ
		protected void __addQuotes(ArrayList<String> stockSymbols){
			if(stockSymbols != null && stockSymbols.size() > 0){
				int index, count = stockSymbols.size();
				//ȡ��http�ͻ���ʵ��
				HttpClient req = new DefaultHttpClient();
				//���ڴ����ַ
				StringBuffer buf = new StringBuffer();
				//������ַ
				buf.append(QUERY_URL);
				buf.append(stockSymbols.get(0));
				//�����Ʊ������1
				for(index = 1; index < count; index++){
					buf.append(",");	
					buf.append(stockSymbols.get(index));
				}
				try {
					//����get��ʽ��ȡ��ҳ����
					HttpGet httpGet = new HttpGet(buf.toString());
					HttpResponse response = req.execute(httpGet);
					InputStream iStream = response.getEntity().getContent();
					//������ҳ���ݣ��������ɹ�����ӹ�Ʊ
					if(parseQuotesFromStream(iStream))
					{
						for(index = 0; index < count; index++){
							stocks.add(stockSymbols.get(index));
						}
						Toast.makeText(context, "��Ʊ��ӳɹ���", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "��Ʊ���ʧ�ܣ�", Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e){
					Log.e(TAG, e.getMessage());
				}
			}
		}
		//����Ʊ��ӽ��ļ���
		private void savePortfolio(){
			//����ǰstocks�е�ֵ����д���ļ���
			if(stocks.size() > 0){
				FileOutputStream outStream = null;
				OutputStreamWriter oWriter;
				try {
					//���ļ�
					outStream = context.openFileOutput(SYMBOL_FILE_NAME, Context.MODE_PRIVATE);
					oWriter = new OutputStreamWriter(outStream); 
					StringBuffer buf = new StringBuffer();
					int index, count = stocks.size();
					//�����ַ���д���ļ���
					buf.append(stocks.get(0));
					for(index = 1; index < count; index++){
						buf.append(",");
						buf.append(stocks.get(index));
					}
					String outStr = buf.toString();
					oWriter.write(outStr, 0, outStr.length());
					oWriter.close();
					outStream.close();
				} catch(Exception e){
					Log.e(TAG, e.getMessage());
				}
			}
		}
		//������Ʊ����
		private boolean parseQuotesFromStream(InputStream aStream){
			boolean flag=false;
			if(aStream != null){
				stockInfo.clear();
				//��ȡ����
				BufferedInputStream iStream = new BufferedInputStream(aStream);
				InputStreamReader iReader=null;
				try {
					//ʹ��GBK��ʽ��������
					iReader = new InputStreamReader(iStream,"GBK");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				StringBuffer strBuf = new StringBuffer();
				char buf[] = new char[BUF_SIZE];
				try {
					int charsRead;
					//�����ݶ�ȡ��StringBuffer��
					while((charsRead = iReader.read(buf, 0, buf.length)) != -1){
						strBuf.append(buf, 0, charsRead);
					}
					//ƥ���Ʊ����
					Pattern pattern=Pattern.compile("str_(.+)=\"(.+)\"");
					Matcher matcher=pattern.matcher(strBuf);
					while(matcher.find()){      
						//��Ʊ��ϢΪ�ڶ��������ж�Ӧ������
			    		String result=matcher.group(2);
			    		String[] data=result.split(",");
			    		StockInfo mStockInfo=new StockInfo();
			    		//�洢��Ʊ�Ĵ���
			    		mStockInfo.setNo(matcher.group(1));
			    		//�洢��Ʊ����
			    		mStockInfo.setName(data[NAME]);
			    		//�洢��Ʊ���տ��̼�
			    		mStockInfo.setOpening_price(data[OPENING_PRICE]);
			    		//�洢��Ʊ�������̼�
			    		mStockInfo.setClosing_price(data[CLOSING_PRICE]);
			    		//�洢��Ʊ��ǰ�۸�
			    		mStockInfo.setCurrent_price(Double.parseDouble(data[CURRENT_PRICE])+0.01*(int)(10*Math.random())+"");
			    		//�洢��Ʊ������߼۸�
			    		mStockInfo.setMax_price(data[MAX_PRICE]);
			    		//�洢��Ʊ������ͼ۸�
			    		mStockInfo.setMin_price(data[MIN_PRICE]);		
			    		//�����ݴ洢��������
			    		stockInfo.add(mStockInfo);
			    		flag=true;
			    	}
				}catch(IOException iox){
					Log.e(TAG, iox.getMessage());
				}
			}
			return flag;
		}
		//ȡ�ô洢��Ʊ���ݵ�����
		protected ArrayList<StockInfo> getQuotesForArray(ArrayList<String> stockSymbols){
			if(stockSymbols != null && stockSymbols.size() > 0){
				//ȡ��http�ͻ���ʵ��
				HttpClient req = new DefaultHttpClient();
				//���ڴ����ַ
				StringBuffer buf = new StringBuffer();
				int index;
				//ȡ�ù�Ʊ������
				int count = stockSymbols.size();
				//������ַ
				buf.append(QUERY_URL);
				buf.append(stockSymbols.get(0));
				//�����Ʊ������1
				for(index = 1; index < count; index++){
					buf.append(",");	
					buf.append(stockSymbols.get(index));
				}
				try {
					//����get��ʽ��ȡ��ҳ����
					HttpGet httpGet = new HttpGet(buf.toString());
					HttpResponse response = req.execute(httpGet);
					InputStream iStream = response.getEntity().getContent();
					//������ҳ����
					parseQuotesFromStream(iStream);
					//���ع�Ʊ����
					return stockInfo;
				} catch (IOException e){
					Log.e(TAG, e.getMessage());
				}
				return null;
			}
			return null;
		}
		//ȡ��K��ͼ
		public Bitmap getChartForSymbol(String symbol){
			try {	
				try {
					//������ƱK��ͼ��ַ
					StringBuilder sb = new StringBuilder(QUERY_IMG);
					sb = sb.append(symbol+".gif");
					HttpClient req = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(sb.toString());
					//����ִ����ַ
					HttpResponse response = req.execute(httpGet);
					InputStream iStream;
					BitmapDrawable bitMap;
					//ȡ����ַ���ص�����
					iStream = response.getEntity().getContent();
					//�����ص����ݽ�����ͼƬ
					bitMap = new BitmapDrawable(iStream);
					iStream.close();
					iStream = null;
					return bitMap.getBitmap();
				} catch ( IOException iox ){
					Log.d(TAG, iox.getMessage());	
				}
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
			}
			return null;
		}
		//���¹�Ʊ����
		public  void refreshStocks(){
			long startTime = System.currentTimeMillis();
			long endTime;
			//ȡ�ù�Ʊ����������
			getQuotesForArray(stocks);
			endTime = System.currentTimeMillis();
			Log.d(TAG, "Refresh ran for " + (endTime - startTime) + " millisenconds");
		}
		//���ع�Ʊ�����С
		public  int stocksSize(){
			if(stocks != null)
				return stocks.size();
			return 0;
		}
		//ͨ����Ʊ������ɾ����Ʊ
		public  void removeQuoteByIndex(int index){
			stocks.remove(index);
			//���浱ǰ��Ʊ
			savePortfolio();
		}
		public  StockInfo getQuoteForIndex(int index){
			return stockInfo.get(index);
}
}
