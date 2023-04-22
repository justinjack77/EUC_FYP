package com.example.euc;
public class GPIO {
    private String GPIO;
    private int PIN;

    public GPIO(String GPIO) {
        this.GPIO = GPIO;
        this.PIN = GPIO_PIN(GPIO);
    }

    public String getGPIO() {
        return GPIO;
    }

    public void setGPIO(String GPIO) {
        this.GPIO = GPIO;
    }

    public int getPIN() {
        return PIN;
    }

    public void setPIN(int PIN) {
        this.PIN = PIN;
    }

    public int GPIO_PIN(String GPIO){
        int pin = 0;
        switch (GPIO){
            case "GPIO0":
                pin = 0;
                break;
            case "GPIO1":
                pin = 1;
                break;
            case "GPIO2":
                pin = 2;
                break;
            case "GPIO3":
                pin = 3;
                break;
            case "GPIO4":
                pin = 4;
                break;
            case "GPIO5":
                pin = 5;
                break;
            case "GPIO6":
                pin = 6;
                break;
            case "GPIO7":
                pin = 7;
                break;
            case "GPIO8":
                pin = 8;
                break;
            case "GPIO9":
                pin = 1;
                break;
            case "GPIO10":
                pin = 10;
                break;
            case "GPIO11":
                pin = 11;
                break;
            case "GPIO12":
                pin = 12;
                break;
            case "GPIO13":
                pin = 13;
                break;
            case "GPIO14":
                pin = 10;
                break;
            case "GPIO15":
                pin = 15;
                break;
            case "GPIO16":
                pin = 16;
                break;
        }

        return pin;
    }
}
