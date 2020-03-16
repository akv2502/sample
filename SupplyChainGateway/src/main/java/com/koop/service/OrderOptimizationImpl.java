package com.koop.service;

import java.util.Map;

import org.springframework.stereotype.Service;
@Service
public class OrderOptimizationImpl implements OrderOptimization {

	@Override
	public Map<String, Object> modifyOrder(Map<String, Object> order) {
		//this logic will change once we get the exact structure of OrderOptimization
		return order;
	}

}
