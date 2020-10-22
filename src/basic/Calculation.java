package basic;

public class Calculation{

    public double getBrutto() {
        return brutto;
    }

    public void setBrutto(double brutto) {
        this.brutto = brutto;
    }

    public double getEinkauf() {
        return einkauf;
    }

    public void setEinkauf(double einkauf) {
        this.einkauf = einkauf;
    }

    public double getNetto() {
        return netto;
    }

    public void setNetto(double netto) {
        this.netto = netto;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getMage() {
        return mage;
    }

    public void setMage(double mage) {
        this.mage = mage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    double brutto, einkauf, netto, tax, mage;
    String name;

    public Calculation(){
        name = " ";
        brutto = 0;
        einkauf = 0;
        einkauf = 0;
        netto = 0;
        tax = 0;
        mage = 0;
    }

    public Calculation(String name, double brutto, double einkauf, double netto, double tax, double mage){

        this.name = name;
        this.brutto = brutto;
        this.einkauf = einkauf;
        this.netto = netto;
        this.tax = tax;
        this.mage = mage;

    }


}
