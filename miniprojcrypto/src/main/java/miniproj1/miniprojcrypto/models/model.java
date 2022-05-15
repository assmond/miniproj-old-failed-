package miniproj1.miniprojcrypto.models;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Crypto {
    private static final Logger logger = LoggerFactory.getLogger(Crypto.class);
    
    private String symbol;
    private String priceChangePercent;
    private String lastPrice;
    private String openPrice;
    private String volume;

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getPriceChangePercent() {
        return priceChangePercent;
    }
    public void setPriceChangePercent(String priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }
    public String getLastPrice() {
        return lastPrice;
    }
    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }
    public String getOpenPrice() {
        return openPrice;
    }
    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    
    public static List<Crypto> create(String json){

    List<Crypto> cryptoList = new LinkedList<>();
    try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
        JsonReader r = Json.createReader(is);
        JsonArray arrayOfCrypto = r.readArray();
        
        arrayOfCrypto.stream()
        .map(v -> (JsonObject)v)
        .filter(v -> v.getString("symbol").contains("USDT"))
        .forEach((JsonObject v) -> {
            Crypto coin = new Crypto();
            coin.setSymbol(v.getString("symbol"));
            coin.setLastPrice(v.getString("lastPrice"));
            coin.setOpenPrice(v.getString("openPrice"));
            coin.setPriceChangePercent(v.getString("priceChangePercent"));
            coin.setVolume(v.getString("volume"));
            cryptoList.add(coin);
        });


    } catch (Exception e) {
        //TODO: handle exception
        e.printStackTrace();
    }
        return cryptoList;
    }

}