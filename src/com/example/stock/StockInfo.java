package com.example.stock;

public class StockInfo {
	// ��Ʊ���
	String no;
	// ��Ʊ����
	String name;
	// ���տ��̼�
	String opening_price;
	// �������̼�
	String closing_price;
	// ��ǰ�۸�
	String current_price;
	// ������߼�
	String max_price;
	// ������ͼ�
	String min_price;

	// ȡ�ù�Ʊ���
	public String getNo() {
		return no;
	}

	// ���ù�Ʊ���
	public void setNo(String no) {
		this.no = no;
	}

	// ȡ�ù�Ʊ����
	public String getName() {
		return name;
	}

	// ���ù�Ʊ����
	public void setName(String name) {
		this.name = name;
	}

	// ȡ�ù�Ʊ���տ��̼�
	public String getOpening_price() {
		return opening_price;
	}

	// ���ù�Ʊ���տ��̼�
	public void setOpening_price(String opening_price) {
		this.opening_price = opening_price;
	}

	// ȡ�ù�Ʊ�������̼�
	public String getClosing_price() {
		return closing_price;
	}

	// ���ù�Ʊ�������̼�
	public void setClosing_price(String closing_price) {
		this.closing_price = closing_price;
	}

	// ȡ�ù�Ʊ��ǰ�۸�
	public String getCurrent_price() {
		return current_price;
	}

	// ���ù�Ʊ��ǰ�۸�
	public void setCurrent_price(String current_price) {
		this.current_price = current_price;
	}

	// ȡ�ù�Ʊ������߼�
	public String getMax_price() {
		return max_price;
	}

	// ���ù�Ʊ������߼�
	public void setMax_price(String max_price) {
		this.max_price = max_price;
	}

	// ȡ�ù�Ʊ������ͼ�
	public String getMin_price() {
		return min_price;
	}

	// ���ù�Ʊ������ͼ�
	public void setMin_price(String min_price) {
		this.min_price = min_price;
	}
}