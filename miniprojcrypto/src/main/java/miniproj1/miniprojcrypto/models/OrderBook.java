package miniproj1.miniprojcrypto.models;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.common.base.Preconditions;

import org.thymeleaf.expression.Lists;
import org.thymeleaf.expression.Maps;

import pl.ciruk.nordea.orders.book.Order.OperationType;
public class OrderBook implements Runnable {
	String id;
	
	List<Order> buys;
	
	List<Order> sells;
	
	Map<Long, Order> ordersCache = Maps.newHashMap();
	
	BlockingQueue<Order> queue;
	
	public OrderBook(String id, BlockingQueue<Order> queue) {
		this.queue = queue;
		this.id = id;
		
		buys = Lists.newLinkedList();
		sells = Lists.newLinkedList();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Order order = queue.take();
				
				
				if (order == Order.EMPTY) {
					return;
				}
				
				if (OperationType.BUY == order.getOperationType()) {
					buy(order);
				} else if (OperationType.SELL == order.getOperationType()) {
					sell(order);
				} else if (OperationType.DELETE == order.getOperationType()) {
					remove(order.getId());
				}
			} catch (InterruptedException e) {
				
			}
		}
	}

	private void buy(Order order) {
		Preconditions.checkArgument(order != null, "Order cannot be null");
		Preconditions.checkArgument(order.getOperationType() == OperationType.BUY, "Operation must be of type BUY");
		
		processBuy(order);
		
		if (order.getVolume() > 0) {
			ordersCache.put(order.getId(), order);
			
			buys.add(order);
			Collections.sort(buys, new Comparator<Order>() {
				@Override
				public int compare(Order first, Order second) {
					if (first.getPrice().equals(second.getPrice())) {
						return first.getTimestamp().compareTo(second.getTimestamp());
					} else {
						return -1 * first.getPrice().compareTo(second.getPrice());
					}
				}
			});
		}
	}
	
	private void sell(Order order) {
		Preconditions.checkArgument(order != null, "Order cannot be null");
		Preconditions.checkArgument(order.getOperationType() == OperationType.SELL, "Operation must be of type SELL");
		
		processSell(order);
		
		if (order.getVolume() > 0) {
			ordersCache.put(order.getId(), order);
			
			sells.add(order);
			
			Collections.sort(sells, new Comparator<Order>() {
				@Override
				public int compare(Order first, Order second) {
					if (first.getPrice().equals(second.getPrice())) {
						return first.getTimestamp().compareTo(second.getTimestamp());
					} else {
						return first.getPrice().compareTo(second.getPrice());
					}
				}
			});
		}
	}
	

	private void processSell(Order sell) {
		for (Order buy : buys) {
			if (buy.getPrice().compareTo(sell.getPrice()) < 0 
					|| sell.getVolume() == 0) {
				break;
			}
			
			long contractVolume = Math.min(sell.getVolume(), buy.getVolume());
			sell.decreaseVolume(contractVolume);
			buy.decreaseVolume(contractVolume);
		}
		
		buys = clearEmptyOrders(buys);
	}
	
	private void processBuy(Order buy) {
		for (Order sell : sells) {
			if (sell.getPrice().compareTo(buy.getPrice()) > 0 
					|| buy.getVolume() == 0) {
				break;
			}
			
			long contractVolume = Math.min(buy.getVolume(), sell.getVolume());
			buy.decreaseVolume(contractVolume);
			sell.decreaseVolume(contractVolume);
		}
		
		sells = clearEmptyOrders(sells);
	}
	
	List<Order> clearEmptyOrders(List<Order> orders) {
		int index = 0;
		
		for (Order o : orders) {
			if (o.getVolume() > 0L) {
				break;
			}
			
			index++;
			this.ordersCache.remove(o.getId());
		}
		
		int lastIndex = Math.max(0, orders.size());
		
		return Lists.newArrayList(orders.subList(Math.min(index, lastIndex), lastIndex));
	}
	
	public void remove(Long orderId) {
		if (ordersCache.containsKey(orderId)) {
			Order toBeRemoved = ordersCache.remove(orderId);
			if (Order.OperationType.BUY == toBeRemoved.getOperationType()) {
				buys.remove(toBeRemoved);
			} else if (Order.OperationType.SELL == toBeRemoved.getOperationType()){
				sells.remove(toBeRemoved);
			}
		}
	}

	public void printContent(PrintStream out) {
		int lineWidth = 40;
		int columnWidth = (lineWidth+1) / 2;
		
		out.println(Strings.padStart("Buy -", columnWidth, ' ') + Strings.padEnd("- Sell", columnWidth, ' '));
		out.println(Strings.repeat("=", lineWidth));
		
		int higherNumberOfOrders = Math.max(buys.size(), sells.size());
		for (int i = 0; i < higherNumberOfOrders; i++) {
			out.println(
					Strings.padStart(getFormattedOrder(buys, i) + " -", columnWidth, ' ') + 
					Strings.padEnd("- " + getFormattedOrder(sells, i), columnWidth, ' '));
		}
	}
	
	private String getFormattedOrder(List<Order> orders, int index) {
		String formattedOrder = "";
		if (index >= 0 && index < orders.size()) {
			Order order =  orders.get(index);
			formattedOrder = String.format("%d@%.2f", order.getVolume(), order.getPrice());
		}
		return formattedOrder; 
	}
	
	public List<Order> getBuys() {
		return buys;
	}
	
	public List<Order> getSells() {
		return sells;
	}
	
	public Order getOrder(Long orderId) {
		return ordersCache.get(orderId);
	}
}
