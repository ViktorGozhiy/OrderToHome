package comviktorgozhiy.github.ordertohome.Models;

public class ClientUser {

    private String uid;
    private int bonus;

    public ClientUser() {

    }

    public ClientUser(String uid) {
        this.uid = uid;
        bonus = 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
