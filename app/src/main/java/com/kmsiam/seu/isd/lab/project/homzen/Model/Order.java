package com.kmsiam.seu.isd.lab.project.homzen.Model;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private String orderId;
    private String status;
    private double total;
    private Date orderDate;
    private ArrayList<Object> items;
    private String deliveryAddress;
    
    public Order() {}
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    
    public ArrayList<Object> getItems() { return items; }
    public void setItems(ArrayList<Object> items) { this.items = items; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}
