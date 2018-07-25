package comviktorgozhiy.github.ordertohome.Models;

import java.util.HashMap;

public class Order {

    public HashMap<String, Item> content;
    private String uid;
    private String clientName;
    private String address;
    private String phone;
    private boolean isDelayedDelivery;
    private String deliveryConditions;

    public Order() {

    }

    public HashMap<String, Item> getContent() {
        return content;
    }

    public void setContent(HashMap<String, Item> content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDelayedDelivery() {
        return isDelayedDelivery;
    }

    public void setDelayedDelivery(boolean delayedDelivery) {
        isDelayedDelivery = delayedDelivery;
    }

    public String getDeliveryConditions() {
        return deliveryConditions;
    }

    public void setDeliveryConditions(String deliveryConditions) {
        this.deliveryConditions = deliveryConditions;
    }

    public static class Item {
        private String title;
        private int quantity;
        private float price;
        private String categoryName;

        public Item() {

        }

        public Item(String title, int quantity, float price, String categoryName) {
            this.title = title;
            this.quantity = quantity;
            this.price = price;
            this.categoryName = categoryName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

    }
}
