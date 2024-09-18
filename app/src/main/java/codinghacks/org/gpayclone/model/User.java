package codinghacks.org.gpayclone.model;

public class User {
    private String phoneNum;
    private double availableAmount;

    public User(String phoneNum, double availableAmount) {
        this.phoneNum = phoneNum;
        this.availableAmount = availableAmount;
    }
    public User() {

    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }
}

