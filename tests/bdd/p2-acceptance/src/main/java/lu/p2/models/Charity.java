package lu.p2.models;

public class Charity {
    private String name;
    private String description;
    private String image;
    private String regNumber;
    private String bankAccount;
    private String irdNumber;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(final String regNumber) {
        this.regNumber = regNumber;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(final String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getIrdNumber() {
        return irdNumber;
    }

    public void setIrdNumber(final String irdNumber) {
        this.irdNumber = irdNumber;
    }

    public String getFormattedBankAccount(){
        final StringBuilder sb = new StringBuilder(bankAccount);
        sb.insert(2, "-");
        sb.insert(7, "-");
        sb.insert(15, "-");
        return sb.toString();
    }

    public String getFormattedIrdNumber(){
        final StringBuilder sb = new StringBuilder(irdNumber);
        sb.insert(3, "-");
        sb.insert(7, "-");
        return sb.toString();
    }
}
