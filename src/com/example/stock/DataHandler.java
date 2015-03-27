package com.example.stock;

import java.util.ArrayList;

import android.content.Context;

public class DataHandler {
	private static final String TAG = "DataHandler";
	// 股票查询网址
	private static final String QUERY_URL = "http://hq.sinajs.cn/list=";
	// 股票K线图
	private static final String QUERY_IMG = "http://image.sinajs.cn/newchart/daily/n/";
	// 存储股票代码的文件
	private static final String SYMBOL_FILE_NAME = "symbols.txt";
	private int BUF_SIZE = 16384;
	// 存储股票代码的数组
	private ArrayList<String> stocks;
	// 存储股票数据的数组
	private ArrayList<StockInfo> stockInfo = new ArrayList<StockInfo>();
	// 各个股票信息在数组中的索引
	private final int NAME = 0;
	private final int OPENING_PRICE = 1;
	private final int CLOSING_PRICE = 2;
	private final int CURRENT_PRICE = 3;
	private final int MAX_PRICE = 4;
	private final int MIN_PRICE = 5;
	Context context;

	public DataHandler(Context mContext) {
		super();
		// 读取存储在文件中的股票代码信息
		// this.readStockFromFile();
		context = mContext;
		if (stocks != null) {
			// 更新股票数据
			// refreshStocks();
		}
	}

}
