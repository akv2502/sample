package com.koop.service;

import java.util.Map;

public interface OrderGateway {
	Map<String, Object> shipToCustomer(Map<String, Object> order);
}
