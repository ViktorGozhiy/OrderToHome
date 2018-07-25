package comviktorgozhiy.github.ordertohome.Models;

public class Product {

    private String title;
    private String content;
    private int weight;
    private float price;
    private float oldPrice;
    private boolean hitLabel;
    private boolean newLabel;
    private int discount;
    private String imagePath;
    private String weightUnit;

    public Product() {

    }

    public Product(String title, String content, int weight, int price) {
        this.title = title;
        this.content = content;
        this.weight = weight;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isHitLabel() {
        return hitLabel;
    }

    public void setHitLabel(boolean hitLabel) {
        this.hitLabel = hitLabel;
    }

    public boolean isNewLabel() {
        return newLabel;
    }

    public void setNewLabel(boolean newLabel) {
        this.newLabel = newLabel;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public float getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(float oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
}
