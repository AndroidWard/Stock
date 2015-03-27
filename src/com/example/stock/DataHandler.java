package com.example.stock;

import java.util.ArrayList;

import android.content.Context;

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

}
